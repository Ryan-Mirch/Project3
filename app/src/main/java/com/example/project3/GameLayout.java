package com.example.project3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GameLayout extends View {



    Paint red_paintbrush_fill;
    Paint white_paintbrush_stroke, blue_paintbrush_stroke, green_paintbrush_stroke;

    public GameLayout(Context context){
        super(context);
        setBackgroundColor(Color.BLACK);

        white_paintbrush_stroke = new Paint();
        white_paintbrush_stroke.setColor(Color.WHITE);
        white_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        white_paintbrush_stroke.setStrokeWidth(5);

        red_paintbrush_fill = new Paint();
        red_paintbrush_fill.setColor(Color.RED);
        red_paintbrush_fill.setStyle(Paint.Style.FILL);

        blue_paintbrush_stroke = new Paint();
        blue_paintbrush_stroke.setColor(Color.BLUE);
        blue_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        blue_paintbrush_stroke.setStrokeWidth(GameLogic.SEGMENT_WIDTH);

        green_paintbrush_stroke = new Paint();
        green_paintbrush_stroke.setColor(Color.GREEN);
        green_paintbrush_stroke.setStyle(Paint.Style.STROKE);
        green_paintbrush_stroke.setStrokeWidth(GameLogic.SEGMENT_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!GameLogic.getGameStarted()){
            GameLogic.initializeGame(canvas);
        }

        GameLogic.updateBarriers();
        GameLogic.updateSegments();

        for (Segment s : GameLogic.getSegments()){
            canvas.drawPath(s.getPath(), blue_paintbrush_stroke);
        }

        for (Barrier b : GameLogic.getBarriers()){
            canvas.drawPath(b.getPath(), green_paintbrush_stroke);
        }

        invalidate();

    }
}
