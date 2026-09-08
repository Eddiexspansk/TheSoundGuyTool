package com.example.soundguytoolkit

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log10

class DistanciaActivity : AppCompatActivity() {

    private lateinit var resultado: TextView
    private lateinit var splMax: EditText
    private lateinit var distancia: EditText
    private lateinit var sensAltPas: EditText
    private lateinit var potAmp: EditText
    private lateinit var distancia2: EditText
    private lateinit var resultado2: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnCalcular2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distancia)

        splMax = findViewById(R.id.et_db_altavoz)
        distancia = findViewById(R.id.et_dist)
        btnCalcular = findViewById(R.id.btn_dist)
        resultado = findViewById(R.id.tv_res_dist)

        sensAltPas = findViewById(R.id.et_sens_altavoz)
        potAmp = findViewById(R.id.et_pot_amp)
        distancia2 = findViewById(R.id.et_dist_pas)
        resultado2 = findViewById(R.id.tv_res_dist_pas)
        btnCalcular2 = findViewById(R.id.btn_calc_dist2)

        findViewById<View>(R.id.toolbar).setOnClickListener {
            onBackPressed()
        }

        btnCalcular.setOnClickListener {
            val splMaxSt = splMax.text.toString()
            val splMaxInt = splMaxSt.toDoubleOrNull() ?: 0.0
            val distanciaSt = distancia.text.toString()
            val distanciaInt = distanciaSt.toDoubleOrNull() ?: 1.0
            calcularSpl(splMaxInt, distanciaInt)
        }

        btnCalcular2.setOnClickListener {
            val sensAltavoz = sensAltPas.text.toString()
            val sensAltInt = sensAltavoz.toIntOrNull() ?: 0
            val potenciaAmp = potAmp.text.toString()
            val potAmpInt = potenciaAmp.toIntOrNull() ?: 0
            val distancia2St = distancia2.text.toString()
            val distancia2Double = distancia2St.toDoubleOrNull() ?: 1.0
            calcularSplNoA(sensAltInt, potAmpInt, distancia2Double)
        }
    }

    private fun calcularSpl(s: Double, d: Double) {
        val spl = s - (20 * log10(d))
        println(d)
        resultado.text = spl.toString()
    }

    private fun calcularSplNoA(sensibilidad: Int, potencia: Int, distancia: Double) {
        val spl = sensibilidad + ((10 * log10(potencia.toDouble())) - (20 * log10(distancia)))
        resultado2.text = spl.toString()
    }
}
