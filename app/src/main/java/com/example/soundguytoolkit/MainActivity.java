package com.example.soundguytoolkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btn_suma_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), SumaActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btn_dist = findViewById(R.id.btn_att_main);
        btn_dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(view.getContext(), DistanciaActivity.class);
                startActivityForResult(intent2, 0);
            }
        });

        Button btn_dmx = findViewById(R.id.btn_dmx);
        btn_dmx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(view.getContext(), DipswitchActivity.class);
                startActivity(intent3);
            }
        });

        Button btn_sonometro = findViewById(R.id.btn_sonometro);
        btn_sonometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(view.getContext(), SonometroActivity.class);
                startActivity(intent4);
            }
        });
    }
}