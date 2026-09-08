package com.example.soundguytoolkit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlin.math.pow

class DipswitchActivity : AppCompatActivity() {

    private val switches = arrayOfNulls<SwitchCompat>(9)
    private lateinit var tvRes: TextView
    private lateinit var etDmx: EditText

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dipswitch)

        switches[0] = findViewById(R.id.switch1)
        switches[1] = findViewById(R.id.switch2)
        switches[2] = findViewById(R.id.switch3)
        switches[3] = findViewById(R.id.switch4)
        switches[4] = findViewById(R.id.switch5)
        switches[5] = findViewById(R.id.switch6)
        switches[6] = findViewById(R.id.switch7)
        switches[7] = findViewById(R.id.switch8)
        switches[8] = findViewById(R.id.switch9)
        tvRes = findViewById(R.id.tv_valor_dmx)
        etDmx = findViewById(R.id.et_dmx_number)

        findViewById<View>(R.id.toolbar).setOnClickListener {
            onBackPressed()
        }

        // Asignamos un listener a cada switch
        for (i in switches.indices) {
            switches[i]?.setOnCheckedChangeListener { _, _ ->
                // Calculamos el valor de DMX y lo mostramos en el textView
                var dmxValue = 0
                for (j in switches.indices) {
                    if (switches[j]?.isChecked == true) {
                        dmxValue += 2.0.pow(j.toDouble()).toInt()
                    }
                }
                tvRes.text = "Valor de DMX: $dmxValue"
            }
        }

        findViewById<Button>(R.id.btn_calc).setOnClickListener {
            try {
                val dmxValue = etDmx.text.toString().toInt()
                if (dmxValue in 0..511) {
                    // Movemos los switches en función del valor de DMX
                    for (i in switches.indices) {
                        switches[i]?.isChecked = (dmxValue and (1 shl i)) != 0
                    }
                    // Mostramos el valor de DMX en el textView
                    tvRes.text = "Valor de DMX: $dmxValue"
                } else {
                    Toast.makeText(this, "Ingrese valor entre 1 y 511", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
