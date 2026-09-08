package com.example.soundguytoolkit

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*

class SonometroActivity : AppCompatActivity() {

    private lateinit var mResultTextView: TextView
    private lateinit var tvPeak: TextView
    private lateinit var levelIndicator: LinearProgressIndicator
    private lateinit var mStartButton: Button
    private lateinit var mStopButton: Button
    private lateinit var mCalibrateButton: Button
    private lateinit var toggleWeighting: MaterialButtonToggleGroup
    private lateinit var toggleTimeWeighting: MaterialButtonToggleGroup

    private var maxPeak = 0.0
    private var calibrationOffset = 0.0
    private var currentRawRms = 0.0
    private var isMeasuring = AtomicBoolean(false)
    private var useAWeighting = false
    private var useSlowTime = false // Default to Fast

    // AudioRecord settings
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private var minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    // Biquad state for filters
    private var aFilters = mutableListOf<BiquadFilter>()
    private var cFilters = mutableListOf<BiquadFilter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sonometro)

        loadCalibration()
        setupFilters()

        mResultTextView = findViewById(R.id.result_text_view)
        tvPeak = findViewById(R.id.tv_peak)
        levelIndicator = findViewById(R.id.sound_level_indicator)
        mStartButton = findViewById(R.id.start_button)
        mStopButton = findViewById(R.id.stop_button)
        mCalibrateButton = findViewById(R.id.btn_calibrate)
        toggleWeighting = findViewById(R.id.toggle_weighting)
        toggleTimeWeighting = findViewById(R.id.toggle_time_weighting)

        findViewById<View>(R.id.toolbar).setOnClickListener {
            onBackPressed()
        }

        mStartButton.setOnClickListener {
            startMeasuring()
        }

        mStopButton.setOnClickListener {
            stopMeasuring()
        }

        mCalibrateButton.setOnClickListener {
            showCalibrationDialog()
        }

        toggleWeighting.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                useAWeighting = (checkedId == R.id.btn_dba)
                maxPeak = 0.0
            }
        }

        toggleTimeWeighting.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                useSlowTime = (checkedId == R.id.btn_slow)
            }
        }
    }

    private fun setupFilters() {
        aFilters.clear()
        aFilters.add(BiquadFilter(0.169641, 0.339282, 0.169641, 1.0, -1.821360, 0.830601))
        aFilters.add(BiquadFilter(1.0, -2.0, 1.0, 1.0, -1.993478, 0.993488))
        aFilters.add(BiquadFilter(1.0, 0.0, -1.0, 1.0, -1.890666, 0.893113))

        cFilters.clear()
        cFilters.add(BiquadFilter(0.169641, 0.339282, 0.169641, 1.0, -1.821360, 0.830601))
        cFilters.add(BiquadFilter(1.0, -2.0, 1.0, 1.0, -1.993478, 0.993488))
    }

    private fun loadCalibration() {
        val prefs = getSharedPreferences("SoundGuyPrefs", MODE_PRIVATE)
        calibrationOffset = prefs.getFloat("calibration_offset", 0.0f).toDouble()
    }

    private fun saveCalibration(offset: Double) {
        val prefs = getSharedPreferences("SoundGuyPrefs", MODE_PRIVATE)
        prefs.edit().putFloat("calibration_offset", offset.toFloat()).apply()
        calibrationOffset = offset
        Toast.makeText(this, "Calibración guardada", Toast.LENGTH_SHORT).show()
    }

    private fun showCalibrationDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Calibración dB(C)")
        val currentStr = String.format(Locale.getDefault(), "%.1f", currentRawRms)
        builder.setMessage("Ingrese el valor real mostrado en su sonómetro.\nMedido ahora: " + currentStr + " dB")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Nivel real (dB)"
        builder.setView(input)

        builder.setPositiveButton("Calibrar") { _, _ ->
            val realValueStr = input.text.toString()
            if (realValueStr.isNotEmpty()) {
                try {
                    val realValue = realValueStr.toDouble()
                    val newOffset = realValue - currentRawRms
                    saveCalibration(newOffset)
                } catch (e: Exception) {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun startMeasuring() {
        if (checkPermissions()) {
            if (isMeasuring.get()) return
            isMeasuring.set(true)
            
            Thread {
                measureLoop()
            }.start()
            
            mStartButton.isEnabled = false
            mStopButton.isEnabled = true
            Toast.makeText(this, "Midiendo...", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun measureLoop() {
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            isMeasuring.set(false)
            return
        }

        recorder.startRecording()
        val buffer = ShortArray(minBufferSize)
        var smoothedPower = 0.0

        while (isMeasuring.get()) {
            val read = recorder.read(buffer, 0, buffer.size)
            if (read > 0) {
                var sumSquare = 0.0
                for (i in 0 until read) {
                    var sample = buffer[i].toDouble() / 32768.0
                    
                    val filters = if (useAWeighting) aFilters else cFilters
                    for (filter in filters) {
                        sample = filter.process(sample)
                    }
                    
                    sumSquare += sample * sample
                }
                
                val currentPower = sumSquare / read
                
                // Time weighting (Exponential Moving Average)
                // tau = 0.125 for Fast, 1.0 for Slow
                val tau = if (useSlowTime) 1.0 else 0.125
                val tInterval = read.toDouble() / sampleRate
                val alpha = 1.0 - exp(-tInterval / tau)
                
                if (smoothedPower == 0.0) {
                    smoothedPower = currentPower
                } else {
                    smoothedPower = alpha * currentPower + (1.0 - alpha) * smoothedPower
                }
                
                val db = if (smoothedPower > 0) 10.0 * log10(smoothedPower) + 100.0 else 0.0
                
                currentRawRms = db
                val finalDb = db + calibrationOffset
                
                if (finalDb > maxPeak) {
                    maxPeak = finalDb
                }

                runOnUiThread {
                    mResultTextView.text = String.format(Locale.getDefault(), "%.0f dB", finalDb)
                    tvPeak.text = String.format(Locale.getDefault(), "Pico: %.0f dB", maxPeak)
                    levelIndicator.progress = finalDb.toInt().coerceIn(0, 120)
                }
            }
        }

        recorder.stop()
        recorder.release()
    }

    private fun stopMeasuring() {
        isMeasuring.set(false)
        mStartButton.isEnabled = true
        mStopButton.isEnabled = false
        Toast.makeText(this, "Medición detenida", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMeasuring()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onDestroy() {
        super.onDestroy()
        isMeasuring.set(false)
    }

    class BiquadFilter(
        private val b0: Double, private val b1: Double, private val b2: Double,
        private val a0: Double, private val a1: Double, private val a2: Double
    ) {
        private var x1 = 0.0
        private var x2 = 0.0
        private var y1 = 0.0
        private var y2 = 0.0

        fun process(x: Double): Double {
            val y = (b0 / a0) * x + (b1 / a0) * x1 + (b2 / a0) * x2 - (a1 / a0) * y1 - (a2 / a0) * y2
            x2 = x1
            x1 = x
            y2 = y1
            y1 = y
            return y
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 1
    }
}
