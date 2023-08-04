package com.example.soundguytoolkit;

import static java.lang.Double.parseDouble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SumaActivity extends AppCompatActivity {

    private EditText et1;
    private TextView tv1;
    Button btnAdd;
    Button btnRes;
    double Po = 20;
    Double numero1, numero2;
    Double resultado;
    String operador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suma);

        et1 = findViewById(R.id.etx_insert_db);
        tv1 = findViewById(R.id.res_db);
        btnAdd = findViewById(R.id.btn_suma);
        btnRes = findViewById(R.id.btn_res);


    }

    public double convertToPascal(double a) {

        double pascales = 20 * Math.pow(10, (a / Po));
        convertToDecibel(pascales);
        return pascales;
    }


    public double convertToDecibel(double b) {

        System.out.println(b);
        double decibel = Math.round((20 * Math.log10(b / Po))*100.0)/100.0;
        System.out.println(decibel);
        String decibelSt = Double.toString(decibel);

        tv1.setText(decibelSt + " dBSpl");
        return decibel;

    }


    public void onClickIgual(View view) {
        try {
            numero2 = parseDouble((et1.getText().toString()));

            if (operador.equals("+")) {
                resultado = convertToPascal(numero1) + convertToPascal(numero2);
                convertToDecibel(resultado);
                onClickOperacionCapturaNumero1(view);
            } else

                tv1.setText(resultado.toString());
        }catch (Exception e){
            Toast.makeText(this, "Debe ingresar otro número", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickSuma(View view) {
        try {
            operador = "+";
            onClickOperacionCapturaNumero1(view);
            convertToPascal(numero1);
        } catch (Exception e) {
            Toast.makeText(this, "Ingrese valor", Toast.LENGTH_SHORT).show();
        }


    }


    public void onClickOperacionCapturaNumero1(View view) {

        numero1 = parseDouble(et1.getText().toString());
        et1.setText("");
    }
}