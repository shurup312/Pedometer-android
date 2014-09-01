package com.shurup.myapplication.shurup3.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import static com.shurup.myapplication.shurup3.app.SensorData.*;

public class DrawView extends View {
    private final Paint p;
    private int i;

    public DrawView(Context context) {
        super(context);
        p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(1);
        p.setTextSize(14);
    }
    public void onDraw(Canvas canvas){
        for(i=2; i<averageGravity.length-1;i++) {

            if (averageGravity[i-1] > 0.0f && averageGravity[i] > 0.0f) {
                canvas.drawLine((((averageGravity[i - 1] - 9.8f) * 70) + 240), (i - 1) * 2, (((averageGravity[i] - 9.8f) * 70) + 240), i * 2, p);
            }
        }
    }
}
