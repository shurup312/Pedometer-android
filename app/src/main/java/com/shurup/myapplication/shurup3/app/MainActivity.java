package com.shurup.myapplication.shurup3.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import java.lang.String;


public class MainActivity extends Activity {
    private SensorManager sensorManager;
    private TextView info;
    private SensorData SD;
    private StringBuffer sb = new StringBuffer();
    private TextView nameActionTV;
    private String nameAction = "default";

    public void setGo(View v){
        nameAction = "go";
        nameActionTV.setText(nameAction);
    }
    public void setRun(View v){
        nameAction = "run";
        nameActionTV.setText(nameAction);
    }
    public void setDefault(View v){
        nameAction = "default";
        nameActionTV.setText(nameAction);
    }
    public void setDrive(View v){
        nameAction = "drive";
        nameActionTV.setText(nameAction);
    }
    public void setToUp(View v){
        nameAction = "up";
        nameActionTV.setText(nameAction);
    }
    public void setToDown(View v){
        nameAction = "down";
        nameActionTV.setText(nameAction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (TextView) findViewById(R.id.info);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SD = new SensorData();
        nameActionTV = (TextView) findViewById(R.id.nameAction);
        setTimer();
    }

    /**
     * Создание/обновление таймера.
     */
    private void setTimer() {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);

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
        timer.schedule(task, 0, 200);
    }

    void showInfo() {
        SensorManager.getRotationMatrix(SD.rotationMatrix, null, SD.gravityData, SD.magnetData);
        SensorManager.getOrientation(SD.rotationMatrix,SD.orientationData);

        sb.setLength(0);
        sb
                .append("\naccX : ").append(SD.accelData[0])
                .append("\naccY : ").append(SD.accelData[1])
                .append("\naccZ : ").append(SD.accelData[2]);
        sb
                .append("\nmagnX : ").append(SD.magnetData[0])
                .append("\nmagnY : ").append(SD.magnetData[1])
                .append("\nmagnZ : ").append(SD.magnetData[2]);
        float gravity = SD.accelData[0] * SD.rotationMatrix[8] + SD.accelData[1] * SD.rotationMatrix[9] + SD.accelData[2] * SD.rotationMatrix[10];
        sb
                .append("\n")
                .append(String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f\t\t%4$.1f", SD.rotationMatrix[0], SD.rotationMatrix[1], SD.rotationMatrix[2], SD.rotationMatrix[3]))
                .append("\n")
                .append(String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f\t\t%4$.1f", SD.rotationMatrix[4], SD.rotationMatrix[5], SD.rotationMatrix[6], SD.rotationMatrix[7]))
                .append("\n")
                .append(String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f\t\t%4$.1f", SD.rotationMatrix[8], SD.rotationMatrix[9], SD.rotationMatrix[10], SD.rotationMatrix[11]))
                .append("\n")
                .append(String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f\t\t%4$.1f", SD.rotationMatrix[12], SD.rotationMatrix[13], SD.rotationMatrix[14], SD.rotationMatrix[15]))
                .append("\n")
                .append("Гравитация по оси Z ").append(gravity);
        FileSave fs = new FileSave();
        info.setText(sb);
        sb.setLength(0);
        sb.append("{\"Z\":").append(gravity).append("},");
        fs.save(nameAction,"test.txt",sb);
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    SD.accelData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    SD.magnetData = event.values.clone();
                    break;
                case Sensor.TYPE_GRAVITY:
                    SD.gravityData = event.values.clone();
            }

        }

    };

}
