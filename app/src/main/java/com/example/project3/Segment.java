package com.example.project3;


import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

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
        Segment collidedSegment = this;

        if (!isLeading) return; //only check for collision if it is leading

        for(Segment check: GameLogic.getLeadingSegments()){
            if(check == this)continue;

            int checkLeftBound = check.getUpper().x - GameLogic.getSpeed();
            int checkRightBound = check.getUpper().x + GameLogic.getSpeed();

            if(upperX < checkLeftBound && upperX > checkRightBound)continue; //no collision

            if(upperX > checkLeftBound && upperX < checkRightBound){
                collidedSegment = check;
                break;
            }
        }

        if(collidedSegment == this) return;

        //if this is straight.
        if(direction == 0){
            collidedSegment.setLeading(false);
        }

        //if the collided segment is straight.
        else if(collidedSegment.getDirection() == 0){
            this.isLeading = false;
        }

        //if neither are straight.
        else{
            collidedSegment.setLeading(false);
            this.isLeading = false;

            int newUpperX = (int) ((upperX + collidedSegment.getUpper().x)*0.5);

            Segment newSegment = new Segment(newUpperX, GameLogic.leadingYPosition, 0);
            GameLogic.getSegments().add(newSegment);
        }
    }

    public void updatePosition(){
        move();
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
