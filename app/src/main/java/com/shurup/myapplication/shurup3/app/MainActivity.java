package com.shurup.myapplication.shurup3.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import static com.shurup.myapplication.shurup3.app.SensorData.*;

public class MainActivity extends Activity {
    private SensorManager sensorManager;
    private DrawView view;
    int iterator = 0;
    private FileSave SD;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new DrawView(this);
        setContentView(view);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setTimer();
        SD = new FileSave();
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
        if(iterator==362){
            StringBuffer sb = new StringBuffer();
            sb.setLength(0);
            for(iterator=0;iterator<362;iterator++){
                sb.append("{gravity:\"").append(arrayGravity[iterator]).append("\",averageGravity:\"").append(averageGravity[iterator]).append("\"},");
            }
            SD.save("gravAverage","gravAverage.txt",sb);
            iterator = 0;
            arrayGravity = new float[801];
            arrayGravity = new float[801];
        } else if(iterator%25==0){
            view.invalidate();
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
