package com.example.project3;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import java.util.ArrayList;

public class Barrier implements GameObject{
    private ArrayList<Point> points;
    private Path path;
    private boolean isStatic; //if static, the barrier will stay still
    private Region region;


    public Barrier(ArrayList<Point> points, boolean isStatic) {
        this.points = points;
        this.isStatic = isStatic;
        region = new Region();
        createShape();
        Log.d("barrier", path.toString());
    }

    public Barrier(Point topLeft, Point bottomRight, boolean isStatic) {
        Point topRight = new Point(bottomRight.x, topLeft.y);
        Point bottomLeft = new Point(topLeft.x, bottomRight.y);

        points = new ArrayList<>();
        points.add(topLeft);
        points.add(topRight);
        points.add(bottomRight);
        points.add(bottomLeft);

        this.isStatic = isStatic;
        region = new Region();
        createShape();
    }

    private void move(long frameTime){
        int speed = GameLogic.getSpeed();
        int pixelsToMove = (int) (speed * (frameTime/(1000/MainThread.MAX_FPS)));

        for(Point p: points){
            p.y += pixelsToMove;
        }

    }

    @Override
    public void update(long frameTime){
        if(isStatic)return;
        move(frameTime);
        createShape();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(GameLogic.SEGMENT_WIDTH);

        canvas.drawPath(path, paint);
    }

    private void createShape(){
        path = new Path();

        boolean firstPoint = true;

        for(Point p: points){
            if(firstPoint){
                path.moveTo(p.x, p.y);
            }

            else{
                path.lineTo(p.x, p.y);
            }

            firstPoint = false;
        }
        path.close();

        region.setPath(path, GameLogic.clip);
    }

    public boolean belowPoint(int y){
        for(Point check: points){
            if(check.y < y ){ //if any point is above the point passed in...
                return false;
            }
        }
        return true;
    }

    public ArrayList<Point> getPoints(){ return points; }

    public Path getPath(){
        return path;
    }

    public Region getRegion(){ return region; }

}
