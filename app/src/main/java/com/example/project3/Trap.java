package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

public class Trap implements GameObject {

    private Point center;
    private Rect shape;
    private Region region;
    private int width;

    public Trap(Point center, int width) {
        this.center = center;
        this.width = width;
        createShape();
        Log.d("trap", "trap spawned at " + center.toString());
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(GameLogic.SEGMENT_WIDTH);

        canvas.drawRect(shape, paint);
    }

    @Override
    public void update(long frameTime) {
        move(frameTime);
        createShape();
    }

    private void move(long frameTime){
        int speed = GameLogic.getSpeed();
        int pixelsToMove = (int) (speed * (frameTime/(1000/MainThread.MAX_FPS)));

        center.y += pixelsToMove;
    }

    private void createShape(){
        shape = new Rect(center.x - width/2, center.y - width/2, center.x + width/2, center.y + width/2);
        region = new Region(shape);
    }

    public boolean belowPoint(int y){
        return(center.y - width/2) > y;
    }

    public Region getRegion(){ return region; }
}
