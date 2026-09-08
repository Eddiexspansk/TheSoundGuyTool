package com.example.soundguytoolkit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_suma_card).setOnClickListener { v ->
            val intent = Intent(v.context, SumaActivity::class.java)
            startActivityForResult(intent, 0)
        }

        findViewById<View>(R.id.btn_att_card).setOnClickListener { view ->
            val intent2 = Intent(view.context, DistanciaActivity::class.java)
            startActivityForResult(intent2, 0)
        }

        findViewById<View>(R.id.btn_dmx_card).setOnClickListener { view ->
            val intent3 = Intent(view.context, DipswitchActivity::class.java)
            startActivity(intent3)
        }

        findViewById<View>(R.id.btn_sonometro_card).setOnClickListener { view ->
            val intent4 = Intent(view.context, SonometroActivity::class.java)
            startActivity(intent4)
        }
    }
}
