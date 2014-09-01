package com.shurup.myapplication.shurup3.app;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.shurup.myapplication.shurup3.app.SensorData.*;

public class MainActivity extends Activity {
    private SensorManager sensorManager;
    int iterator = 0;
    private FileSave SD;
    private int i;

    Boolean recordSD = false;
    private Date date;
    private TextView tvText;
    private Button buttonGo;
    private TextView textGo;
    private TextView delayText;
    private int delay;
    private Timer timer;
    private boolean RUN;

    public void setGo(View v){
        textGo.setText("Go");
        date = new Date();
    }
    public void setRun(View v){
        textGo.setText("Run");
        date = new Date();
    }
    public void setStairsUp(View v){
        textGo.setText("StairsUp");
        date = new Date();
    }
    public void setStairsDown(View v){
        textGo.setText("StairsDown");
        date = new Date();
    }
    public void setDefault(View v){
        textGo.setText("Default");
        date = new Date();
    }
    public void setSleep(View v){
        textGo.setText("Sleep");
        date = new Date();
    }

    public void setStop(View v){
        sensorManager.unregisterListener(listener);
        timer.cancel();
        RUN = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setTimer();
        SD = new FileSave();
        date = new Date();
        tvText = (TextView) findViewById(R.id.tvText);
        buttonGo = (Button)findViewById(R.id.Go);
        textGo = (TextView)findViewById(R.id.textGo);
        delayText = (TextView) findViewById(R.id.delayText);
        tvText.setTextSize(12);
        textGo.setText("Default");
    }

    /**
     * Создание/обновление таймера.
     */
    private void setTimer() {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();

                    }
                });
            }
        };
        timer.schedule(task, 0, 25);
    }

    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return dateFormat.format(date).toString();
    }

    void showInfo() {
        SensorManager.getRotationMatrix(rotationMatrix, null, gravityData, magnetData);
        SensorManager.getOrientation(rotationMatrix, orientationData);

        resultGravity = accelData[0] * rotationMatrix[8] + accelData[1] * rotationMatrix[9] + accelData[2] * rotationMatrix[10];
        arrayGravity[iterator] = resultGravity;
        if(iterator>0){

            if((Math.abs(arrayGravity[iterator-1])-Math.abs(arrayGravity[iterator]))<3){
                averageGravity[iterator] = (arrayGravity[iterator-1]+0.1f*(arrayGravity[iterator]-arrayGravity[iterator-1]));
            } else {
                averageGravity[iterator] = arrayGravity[iterator];
            }

        }
        iterator++;
        if(iterator==40){
            StringBuffer sb = new StringBuffer();
            sb.setLength(0);
            for(iterator=0;iterator<40;iterator++){
                sb
                        .append("{gr:\"")
                        .append(arrayGravity[iterator])
                        .append("\",avgGr:\"")
                        .append(averageGravity[iterator])
                        .append("\",rawGrX:\"")
                        .append(accelData[0])
                        .append("\",rawGrY:\"")
                        .append(accelData[1])
                        .append("\",rawGrZ:\"")
                        .append(accelData[2])
                        .append("\"},");
            }
            SD.save((String) textGo.getText(),getCurrentTime()+".txt",sb);
            iterator = 0;
            arrayGravity = new float[40];
            averageGravity = new float[40];
        }
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magnetData = event.values.clone();
                    break;
                case Sensor.TYPE_GRAVITY:
                    gravityData = event.values.clone();
            }

        }
    };

}
