package com.example.project3;


import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import java.util.ArrayList;

public class Barrier {
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

    private void move(){
        int speed = GameLogic.getSpeed();

        for(Point p: points){
            p.y += speed;
        }

    }

    public void updatePosition(){
        if(isStatic)return;
        move();
        createShape();
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
