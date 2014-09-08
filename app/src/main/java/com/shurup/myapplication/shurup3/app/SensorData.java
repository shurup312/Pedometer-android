package com.shurup.myapplication.shurup3.app;

public class SensorData {
    public static float[] rotationMatrix = new float[16]; // матрица поворота
    public static float[] accelData = new float[3];      // данные с акселерометра
    public static float[] gravityData = new float[3];      // данные о гравитации
    public static float[] magnetData = new float[3];     // данные геомагнитного датчика
    public static float resultGravity;
    public static float[] arrayGravity = new float[40];
    public static float[] averageGravity = new float[40];

    public static void reset(){
        arrayGravity = new float[40];
        averageGravity = new float[40];
    }
}
