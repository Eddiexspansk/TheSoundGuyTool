package com.example.soundguytoolkit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Double.parseDouble
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

class SumaActivity : AppCompatActivity() {

    private lateinit var etInput: EditText
    private lateinit var tvResult: TextView
    private lateinit var rvLevels: RecyclerView
    private lateinit var btnClearAll: Button
    
    private val levels = mutableListOf<Double>()
    private val adapter = LevelsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suma)

        etInput = findViewById(R.id.etx_insert_db)
        tvResult = findViewById(R.id.res_db)
        rvLevels = findViewById(R.id.rv_levels)
        btnClearAll = findViewById(R.id.btn_clear_all)

        rvLevels.layoutManager = LinearLayoutManager(this)
        rvLevels.adapter = adapter

        findViewById<View>(R.id.toolbar).setOnClickListener {
            onBackPressed()
        }

        btnClearAll.setOnClickListener {
            val size = levels.size
            levels.clear()
            adapter.notifyItemRangeRemoved(0, size)
            updateCalculation()
        }
    }

    private fun updateCalculation() {
        if (levels.isEmpty()) {
            tvResult.text = "0.0 dB"
            return
        }
        
        var totalPower = 0.0
        for (level in levels) {
            totalPower += 10.0.pow(level / 10.0)
        }
        
        val result = round(10.0 * log10(totalPower) * 100.0) / 100.0
        tvResult.text = String.format(Locale.getDefault(), "%.2f dB", result)
    }

    fun onClickSuma(view: View) {
        try {
            val input = etInput.text.toString()
            if (input.isNotEmpty()) {
                val value = parseDouble(input)
                levels.add(0, value) // Add to top
                adapter.notifyItemInserted(0)
                rvLevels.scrollToPosition(0)
                etInput.setText("")
                updateCalculation()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
        }
    }

    inner class LevelsAdapter : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvValue: TextView = view.findViewById(R.id.tv_level_value)
            val btnDelete: Button = view.findViewById(R.id.btn_delete_level)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_db_level, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val level = levels[position]
            holder.tvValue.text = String.format(Locale.getDefault(), "%.1f dB", level)
            holder.btnDelete.setOnClickListener {
                val currentPos = holder.adapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    levels.removeAt(currentPos)
                    notifyItemRemoved(currentPos)
                    updateCalculation()
                }
            }
        }

        override fun getItemCount() = levels.size
    }
}
