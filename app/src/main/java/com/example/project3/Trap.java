package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;

public class Trap implements GameObject {

    private Point center;
    private Path path;
    private Region region;
    private int width;

    public Trap(Point center, int width) {
        this.center = center;
        this.width = width;

        path = new Path();
        region = new Region();

        createShape();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(GameLogic.SEGMENT_WIDTH);

        canvas.drawPath(path, paint);
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
        path = new Path();
        Point current = new Point();

        current.x = center.x - width/2;
        current.y = center.y - width/2;
        path.moveTo(current.x, current.y); //top left

        current.x += width/2;
        path.lineTo(current.x, current.y); //top right
        path.moveTo(current.x, current.y);

        current.y += width/2;
        path.lineTo(current.x, current.y); //bottom right
        path.moveTo(current.x, current.y);

        current.x -= width/2;
        path.lineTo(current.x, current.y); //bottom right
        path.moveTo(current.x, current.y);

        path.close();

        region.setPath(path, GameLogic.clip);
    }

    public boolean belowPoint(int y){
        return(center.y - width/2) > y;
    }
}
