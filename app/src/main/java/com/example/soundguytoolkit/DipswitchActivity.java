package com.example.soundguytoolkit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class DipswitchActivity extends AppCompatActivity {

    private Switch[] switches = new Switch[9];
    private TextView tv_res;
    private EditText et_dmx;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dipswitch);


        switches[0] =  findViewById(R.id.switch1);
        switches[1] =  findViewById(R.id.switch2);
        switches[2] =  findViewById(R.id.switch3);
        switches[3] =  findViewById(R.id.switch4);
        switches[4] =  findViewById(R.id.switch5);
        switches[5] =  findViewById(R.id.switch6);
        switches[6] =  findViewById(R.id.switch7);
        switches[7] =  findViewById(R.id.switch8);
        switches[8] =  findViewById(R.id.switch9);
        tv_res = findViewById(R.id.tv_valor_dmx);
        et_dmx = findViewById(R.id.et_dmx_number);

        // Asignamos un listener a cada switch
        for (int i = 0; i < switches.length; i++) {
            final int index = i;
            switches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Calculamos el valor de DMX y lo mostramos en el textView
                    int dmxValue = 0;
                    for (int j = 0; j < switches.length; j++) {
                        if (switches[j].isChecked()) {
                            dmxValue += Math.pow(2, j);
                        }
                    }
                    tv_res.setText("Valor de DMX: " + dmxValue);
                }
            });



        }

        findViewById(R.id.btn_calc).setOnClickListener(view -> {
            // Obtenemos el valor ingresado en el editText

try {
    int dmxValue = Integer.parseInt(et_dmx.getText().toString());
    if (dmxValue >= 0 && dmxValue <= 511) {

        // Movemos los switches en función del valor de DMX
        for (int i = 0; i < switches.length; i++) {
            switches[i].setChecked((dmxValue & (1 << i)) != 0);
        }

        // Mostramos el valor de DMX en el textView
        tv_res.setText("Valor de DMX: " + dmxValue);

    } else {
        Toast.makeText(this, "Ingrese valor entre 1 y 511", Toast.LENGTH_SHORT).show();
    }
}catch(Exception e){
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show();
            }
        });

    }
}