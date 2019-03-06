package com.example.Divide;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;

import java.util.ArrayList;

public class Barrier implements GameObject{
    private ArrayList<Point> points;
    private Point top;
    private Point bottom;
    private Path path;
    private Region region;

    public Barrier(Point top, Point bottom, int width) {
        this.top = top;
        this.bottom = bottom;

        Point topRight = new Point(top.x + width/2, top.y);
        Point topLeft = new Point(top.x - width/2, top.y);
        Point bottomRight = new Point(bottom.x + width/2, bottom.y);
        Point bottomLeft = new Point(bottom.x - width/2, bottom.y);

        points = new ArrayList<>();
        points.add(topLeft);
        points.add(topRight);
        points.add(bottomRight);
        points.add(bottomLeft);

        region = new Region();
        createShape();
    }

    private void move(long frameTime){
        int speed = GameLogic.getSpeed();
        int pixelsToMove = (int) (speed * (frameTime/(1000/MainThread.MAX_FPS)));

        for(Point p: points){
            p.y += pixelsToMove;
        }

        top.y += pixelsToMove;
        bottom.y += pixelsToMove;
    }

    @Override
    public void update(long frameTime){
        move(frameTime);
        createShape();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
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

    public Point getTop() {
        return top;
    }

    public Point getBottom() {
        return bottom;
    }

    //Returns the X where Y = 0.
    //Returns -1 if the top point is below Y = 0
    public int getXWhereY0(){
        int x1 = top.x;
        int x2 = bottom.x;
        int y1 = top.y;
        int y2 = bottom.y;

        if(y1 >= 0) return -1;

        if(x1 == x2) return x1;


        int dx = x2-x1;
        int dy = y2-y1;
        double m = dy/dx;
        double b = y1 - m*x1;
        double xAtY0 = -b/m;

        return (int) xAtY0;
    }
}
