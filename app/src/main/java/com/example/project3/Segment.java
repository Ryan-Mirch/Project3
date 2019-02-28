package com.example.project3;


import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;

public class Segment {
    private int upperX;
    private int upperY;
    private int lowerX;
    private int lowerY;
    private Path shape;

    private boolean isLeading = true; //if leading, the upper of the segment doesn't move.

    private int direction;  //-1 = upLeft
                            //0  = up
                            //1  = upRight


    public Segment(int upperX, int upperY, int direction) {
        this.upperX = upperX;
        this.upperY = upperY;
        this.lowerX = upperX;
        this.lowerY = upperY;
        this.direction = direction;
        createShape();
    }

    public Path getPath(){
        return shape;
    }

    private void move(){
        int speed = GameLogic.getSpeed();

        lowerY += speed;

        if(direction != 0 && isLeading){
            upperX += speed * direction;
        }

        if(!isLeading){
            upperY += speed;
        }
    }

    public void segmentCollisionCheck(){
        for(Segment check: GameLogic.getLeadingSegments()){
            if(check == this)continue;

            int checkLeftBound = check.getUpper().x - GameLogic.getSpeed();
            int checkRightBound = check.getUpper().x + GameLogic.getSpeed();

            if(upperX < checkLeftBound && upperX > checkRightBound)continue; //no collision



        }
    }

    public void update(){
        move();
        segmentCollisionCheck();
        createShape();
    }

    private void createShape(){
        shape = new Path();
        shape.moveTo(upperX,upperY);
        shape.lineTo(lowerX, lowerY);
    }

    public void setLeading(boolean b){
        isLeading = b;
    }

    public boolean getLeading(){
        return isLeading;
    }

    public Point getUpper(){
        return new Point(upperX, upperY);
    }

    public int getDirection(){
        return direction;
    }

}
