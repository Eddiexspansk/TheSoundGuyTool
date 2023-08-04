package com.example.soundguytoolkit;

import static java.lang.Math.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DistanciaActivity extends AppCompatActivity {


    private TextView resultado;
    private EditText spl_Max;
    private EditText distancia;
    private EditText sens_alt_pas;
    private EditText pot_amp;
    private EditText et_dist_pas;
    private EditText distancia2;
    private TextView resultado2;
    Button btn_calcular2, btn_calcular;
    String spl_Max_st, distancia_st;
    double distancia_int, distancia_2_double,spl_Max_int;
    String sens_altavoz, potencia_amp, distancia_2;
    int sens_alt_int, pot_amp_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distancia);

        spl_Max = findViewById(R.id.et_db_altavoz);
        distancia = (EditText) findViewById(R.id.et_dist);
        btn_calcular = findViewById(R.id.btn_dist);
        resultado = findViewById(R.id.tv_res_dist);

        sens_alt_pas = findViewById(R.id.et_sens_altavoz);
        pot_amp = findViewById(R.id.et_pot_amp);
        distancia2 = findViewById(R.id.et_dist_pas);
        resultado2 = findViewById(R.id.tv_res_dist_pas);
        btn_calcular2 = findViewById(R.id.btn_calc_dist2);





        btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spl_Max_st = spl_Max.getText().toString();
                spl_Max_int = Double.parseDouble(spl_Max_st);
                distancia_st = distancia.getText().toString();
                distancia_int = Double.parseDouble(distancia_st);
                CalcularSpl(spl_Max_int, distancia_int);
            }

        });

        btn_calcular2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sens_altavoz = sens_alt_pas.getText().toString();
                sens_alt_int = Integer.parseInt(sens_altavoz);
                potencia_amp = pot_amp.getText().toString();
                pot_amp_int = Integer.parseInt(potencia_amp);
                distancia_2 = distancia2.getText().toString();
                distancia_2_double = Double.parseDouble(distancia_2);
                CalcularSplNoA(sens_alt_int, pot_amp_int, distancia_2_double);

            }
        });

    }
    public void CalcularSpl ( double s, double d){

        double spl = s - (20 * Math.log10(d));
        System.out.println(d);
        String decibelSpl = Double.toString(spl);
        resultado.setText(decibelSpl);

    }

    public void CalcularSplNoA ( int sensibilidad, int potencia, double distancia){

        double spl = sensibilidad + ((10 * Math.log10(potencia)) - (20 * Math.log10(distancia)));
        //System.out.println(distancia);
        String decibelSpl = Double.toString(spl);
        resultado2.setText(decibelSpl);

    }
}