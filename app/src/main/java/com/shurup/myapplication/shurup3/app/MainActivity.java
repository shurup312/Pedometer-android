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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.shurup.myapplication.shurup3.app.SensorData.*;

public class MainActivity extends Activity {
    /**
     * Сенсор менеджер
     */
    private SensorManager sensorManager;
    /**
     * переменная для объекта сохранения в файл
     */
    private FileSave SD;
    /**
     * Записывать ли на SD флэху
     */
    Boolean recordSD = true ;
    /**
     * Дата для имени создаваемого файла
     */
    private Date date;
    /**
     * Данные о состоянии сенсоров
     */
    private TextView textGo;
    /**
     * Итератор
     */
    private int iterator;
    /**
     * Флаг запуска записи данных с сенсоров в файл
     */
    private boolean run = false;
    /**
     * Режим записи
     */
    private String mode;

    public void start(View v){
        run = true;
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        start.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);
        Toast toast = Toast.makeText(getApplicationContext(),"Старт", Toast.LENGTH_SHORT);
        toast.show();
    }
    public void stop(View v){
        run = false;
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        SensorData.reset();
        Toast toast = Toast.makeText(getApplicationContext(),"Стоп", Toast.LENGTH_SHORT);
        toast.show();
    }


    private void setMode(String modeName){
        mode = modeName;
        textGo.setText(mode);
        date = new Date();
    }

    /**
     * Кнопки в интерфейсе для переключения режимов работы программы.
     */
    public void setGo(View v){
        setMode("Go");
    }
    public void setRun(View v){
        setMode("Run");
    }
    public void setStairsUp(View v){
        setMode("StairsUp");
    }
    public void setStairsDown(View v){
        setMode("StairsDown");
    }
    public void setDefault(View v){
        setMode("Default");
    }
    public void setSleep(View v){
        setMode("Sleep");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /**
         * Запускаем таймер
         */
        setTimer();
        /**
         * Создаем объект для записи в файл
         */
        SD = new FileSave();
        /**
         * Задаем дефолтую дату для именования файла с записанными данными.
         */
        date = new Date();
        /**
         * Элемент для вывода данных в программе.
         */
        TextView tvText = (TextView) findViewById(R.id.tvText);
        tvText.setTextSize(12);
        /**
         * Элемент для вывода режима записи.
         */
        textGo = (TextView)findViewById(R.id.textGo);
        setMode("Default");
    }

    /**
     * Создание/обновление таймера.
     */
    private void setTimer() {
        /**
         * Инициализация опроса сенсоров
         */
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);

        /**
         * Запуск таймера событий.
         */
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Если стоит флаг, что надо калькулировать, то делаем это.
                         */
                        if(run){
                            showInfo();
                        }

                    }
                });
            }
        };
        /**
         * Опрос каждые 25мс и выполнение таски.
         */
        timer.schedule(task, 0, 25);
    }

    /**
     * Садание формата названия файла
     */
    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        return dateFormat.format(date);
    }


    void showInfo() {
        /**
         * Калькуляция значения гравитации. Если вернется true (раз в 40 итераций), то надо сбросить данные на диск.
         */
        if(Calculate.calculate()){
            StringBuffer sb = new StringBuffer();
            sb.setLength(0);
            for(iterator=0;iterator<40;iterator++){
                sb
                        .append("{\"gr\":\"")
                        .append(arrayGravity[iterator])
                        .append("\",\"avgGr\":\"")
                        .append(averageGravity[iterator])
                        .append("\",\"rawGrX\":\"")
                        .append(accelData[0])
                        .append("\",\"rawGrY\":\"")
                        .append(accelData[1])
                        .append("\",\"rawGrZ\"  :\"")
                        .append(accelData[2])
                        .append("\"},");
            }
            /**
             * Если есть в настройках указание сохранять на флэху, то сохраняем
             */
            if(recordSD){
                SD.save(mode,getCurrentTime()+".txt",sb);
            }
            /**
             * Скинем массив с данными.
             */
            SensorData.reset();
        }
    }

    /**
     * Опрос сенсоров
     */
    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            /**
             * Запись данных от сенсоров в переменные.
             */
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


