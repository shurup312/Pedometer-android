package com.shurup.myapplication.shurup3.app;

public class SensorData {
    float[] rotationMatrix = new float[16]; // матрица поворота
    float[] accelData = new float[3];      // данные с акселерометра
    float[] gravityData = new float[3];      // данные о гравитации
    float[] magnetData = new float[3];     // данные геомагнитного датчика
    float[] orientationData = new float[3];// амтрица положения в пространстве
}
