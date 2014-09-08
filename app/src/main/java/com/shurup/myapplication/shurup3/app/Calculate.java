package com.shurup.myapplication.shurup3.app;

import android.hardware.SensorManager;

import static com.shurup.myapplication.shurup3.app.SensorData.*;

public class Calculate {
    static int iterator = 0;
    static int max = 40;

    /**
     * Подсчитываем данные, которые выходят из сенсора.
     */
    public static boolean calculate(){
        /**
         * Считаем поворотную матрицу.
         */
        SensorManager.getRotationMatrix(rotationMatrix, null, gravityData, magnetData);

        resultGravity = accelData[0] * rotationMatrix[8] + accelData[1] * rotationMatrix[9] + accelData[2] * rotationMatrix[10];
        arrayGravity[iterator] = resultGravity;
        if(iterator>0){
            calculateAverage();
        }
        iterator++;
        if(iterator==max){
            iterator = 0;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Нахождение значения ускорения на ось гравитации
     */
    private static void calculateAverage() {
        if((Math.abs(arrayGravity[iterator-1])-Math.abs(arrayGravity[iterator]))<3){
            averageGravity[iterator] = (arrayGravity[iterator-1]+0.1f*(arrayGravity[iterator]-arrayGravity[iterator-1]));
        } else {
            averageGravity[iterator] = arrayGravity[iterator];
        }
    }
}
