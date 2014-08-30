package com.shurup.myapplication.shurup3.app;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.lang.String;


public class MainActivity extends Activity {

    TextView tvText;
    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;
    Sensor sensorGravity;
    final String DIR_SD = "/storage/extSdCard/Android/MyApp/";
    StringBuilder sb = new StringBuilder();
    Button buttonGo;
    TextView textGo;
    TextView delayText;
    Timer timer;
    File sdFile;
    Integer delay=100;
    Boolean RUN = false;

    TextView MathTv;

    Date date;

    Float powerGravity;
    Float powerAccelerator;
    Float powerAccelerometer;

    Boolean recordSD = false;

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

    public void set100(View v){
        date = new Date();
        delay =100;
        delayText.setText(delay.toString());
        setTimer();
    }
    public void set200(View v){
        date = new Date();
        delay =200;
        delayText.setText(delay.toString());
        setTimer();
    }
    public void set400(View v){
        date = new Date();
        delay =400;
        delayText.setText(delay.toString());
        setTimer();
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
        date = new Date();
        tvText = (TextView) findViewById(R.id.tvText);
        buttonGo = (Button)findViewById(R.id.Go);
        textGo = (TextView)findViewById(R.id.textGo);
        delayText = (TextView) findViewById(R.id.delayText);
        MathTv = (TextView) findViewById(R.id.Math);
        tvText.setTextSize(12);
        textGo.setText("Default");
        delayText.setText(delay.toString());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

    }

    @Override
    /**
     * перед запуском смотрим, если на задан таймер, то задаем его.
     */
    protected void onResume() {
        super.onResume();
    }

    /**
     * Создание/обновление таймера.
     */
    private void setTimer() {
        if(RUN){
            sensorManager.unregisterListener(listener);
            timer.cancel();
        }
        RUN = true;
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
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
        timer.schedule(task, 0, delay);
    }


    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }

    void showInfo() {

        File sdPath = Environment.getExternalStorageDirectory();
        sb.setLength(0);
        String fName = textGo.getText().toString() + "_" + delay + "_"+getCurrentTime()+".txt";
        sb.append("Ускорение плюс гравитация : \n" + format(valuesAccel))
                .append("\n\nЧистое ускорение : \n" + format(valuesAccelMotion))
                .append("\n\nГравитация из ускорения и гравитации :\n" + format(valuesAccelGravity))
                .append("\n\n\nЧистое ускорение : \n\n" + format(valuesLinAccel))
                .append("\n\nГравитация из ускорения : \n\n" + format(valuesGravity))
                .append("\n\n\n" + fName);
        tvText.setText(sb);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(recordSD){
            writeFileSD("{"+
                            "\"date\":\""+dateFormat.format(new Date()).toString()+"\","+
                            "\"accX\":\""+valuesAccel[0]+"\","+
                            "\"accY\":\""+valuesAccel[1]+"\","+
                            "\"accZ\":\""+valuesAccel[2]+"\","+
                            "\"gravX\":\""+valuesGravity[0]+"\","+
                            "\"gravY\":\""+valuesGravity[1]+"\","+
                            "\"gravZ\":\""+valuesGravity[2]+"\","+
                            "},", fName
            );
        }
    }

    public void getAngle(){
        TextView tv = (TextView) findViewById(R.id.power);

    }

    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return dateFormat.format(date).toString();
    }

    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    TextView tv;
                    float[] result;
                    tv = (TextView) findViewById(R.id.power);
                    SensorData SD = new SensorData();
                    SensorManager.remapCoordinateSystem(SD.rotationMatrix)
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i]
                                - valuesAccelGravity[i];
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    powerAccelerator = event.sensor.getPower();
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    powerGravity = event.sensor.getPower();
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
            }

        }

    };

    void writeFileSD(String S, String fName) {
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(DIR_SD+textGo.getText().toString()+"/");
        // создаем каталог
        sdPath.mkdirs();
        sdFile = new File(sdPath, fName);
        try {
            FileWriter wfile = new FileWriter(sdFile, true);
            // пишем данные
            wfile.append(S);
            wfile.flush();
            // закрываем поток
            wfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SensorData {
    float[] rotationMatrix; // матрица поворота
    float[] accelData;      // данные с акселерометра
    float[] magnetData;     // данные геомагнитного датчика
    float[] orientationData;// амтрица положения в пространстве

}
