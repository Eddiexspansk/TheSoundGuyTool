package com.example.soundguytoolkit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SonometroActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private MediaRecorder mRecorder;
    private TextView mResultTextView;
    private Button mStartButton;
    private Button mStopButton;
    private static String mFileName = null;
    static final private double EMA_FILTER = 0.6;
    private double mEMA = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sonometro);

        mResultTextView = (TextView) findViewById(R.id.result_text_view);
        mStartButton = (Button) findViewById(R.id.start_button);
        mStopButton = (Button) findViewById(R.id.stop_button);
        mStartButton.setBackgroundColor(Color.BLUE);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeasuring();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMeasuring();
            }
        });
    }

    private void startMeasuring() {

        if (CheckPermissions()) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File outputWaveFileDir = cw.getDir("waves", Context.MODE_PRIVATE);
                if (!outputWaveFileDir.exists()) {
                    if (!outputWaveFileDir.mkdirs()) {
                        Toast.makeText(getApplicationContext(), "Can not create dir " + outputWaveFileDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
                File outputWaveFile = new File(outputWaveFileDir, "wave_" + System.currentTimeMillis() + ".3gp");

                while (outputWaveFile.exists()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    outputWaveFile = new File(outputWaveFileDir, "wave_" + System.currentTimeMillis() + ".3gp");
                }
                try {
                    outputWaveFile.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Can not create file " + outputWaveFile.getName(), Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
                outputWaveFile.deleteOnExit();
                Log.i("TAG", "Writting to " + outputWaveFile.getAbsolutePath());
                mFileName = outputWaveFile.getAbsolutePath();

            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Toast.makeText(this.getApplicationContext(), "External storage mounted read only", Toast.LENGTH_LONG).show();
                this.onBackPressed();
            } else {
                Toast.makeText(this.getApplicationContext(), "External storage state: " + state, Toast.LENGTH_LONG).show();
                this.onBackPressed();
            }

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
                System.out.println("" + e);
            }

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            mRecorder.start();
            Runnable task = () -> {
                try {
                    Log.i(Thread.currentThread().getName(), "executor - started");
                    double rms = 0.0;
                    int readSize = mRecorder.getMaxAmplitude();
                    Log.i(Thread.currentThread().getName(), "readsize: " + readSize);
                    if (readSize > 0 && readSize < 1000000) {
                        rms = 20.0 * Math.log10(readSize);
                        Log.i(Thread.currentThread().getName(), "calculated " + rms);
                    }
                    // No estai en el UiThread, estai en un thread a parte (Runnable)
                    // tenis que ir a buscar el UiThread y ahí cambiai el mResultTextView asi:
                    // este "final" es para que el valor de _rms entre en el thread del runOnUiThread
                    //              se supone que el thread de adentro no puede modificar ese valor
                    //              (pero si envuelves el valor en otra clase, podría)
                    final double _rms=rms;
                    runOnUiThread(()->{mResultTextView.setText(String.format("%.0f dB", _rms));});
                    Log.i(Thread.currentThread().getName(), "executor - ended");
                } catch (Exception e){ // https://stackoverflow.com/questions/28007317/scheduled-executor-service-isnt-working
                    e.printStackTrace();
                }
            };

            executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();

        } else {
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {

        ActivityCompat.requestPermissions(SonometroActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void stopMeasuring() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;
    }


}