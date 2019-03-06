package com.example.Divide;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

public class Segment implements GameObject{
    private int upperX;
    private int upperY;
    private int lowerX;
    private int lowerY;
    private long id;

    private Path path;

    private boolean isLeading = true; //if leading, the upper of the segment doesn't move.

    private int direction;  //-1 = upLeft
                            //0  = up
                            //1  = upRight


    public Segment(int upperX, int upperY, int direction, long id) {
        this.upperX = upperX;
        this.upperY = upperY;
        this.lowerX = upperX;
        this.lowerY = upperY + 5;
        this.id = id;
        this.direction = direction;
        createShape();
    }

    @Override
    public void update(long frameTime){
        move(frameTime);
        segmentCollisionCheck();
        barrierCollisionCheck();
        trapCollisionCheck();
        createShape();
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(GameLogic.SEGMENT_WIDTH);

        canvas.drawPath(path,paint);
    }

    private void move(long frameTime){
        int speed = GameLogic.getSpeed();
        int pixelsToMove = (int) (speed * (frameTime/(1000/MainThread.MAX_FPS)));

        lowerY += pixelsToMove;

        if(direction != 0 && isLeading){
            upperX += pixelsToMove * direction;
        }

        if(!isLeading){
            upperY += pixelsToMove;
        }
    }

    private void barrierCollisionCheck(){
        if (!isLeading) return; //only check for collision if it is leading

        for(Barrier check: GameLogic.getBarriers()){
            if(check.getRegion().contains(upperX, upperY)){
                Log.d("segment","segment hit barrier at  x: " + upperX + "  y: " + upperY);
                isLeading = false;
            }
        }
    }

    private void trapCollisionCheck(){
        if (!isLeading) return; //only check for collision if it is leading

        for(Trap check: GameLogic.getTraps()){
            if(check.getRegion().contains(upperX, upperY)){
                Log.d("segment","segment hit trap at  x: " + upperX + "  y: " + upperY);
                isLeading = false;
            }
        }
    }

    private void segmentCollisionCheck(){
        if (!isLeading) return; //only check for collision if it is leading

        Segment collidedSegment = this;

        for(Segment check: GameLogic.getLeadingSegments()){
            if(check == this)continue;
            if(id == check.getID())continue; //segments with identical id's cant collide.

            int checkLeftBound = check.getUpper().x - GameLogic.getSpeed()*2;
            int checkRightBound = check.getUpper().x + GameLogic.getSpeed()*2;

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

            Segment newSegment = new Segment(newUpperX, GameLogic.leadingYPosition, 0, System.currentTimeMillis());
            GameLogic.getNewSegments().add(newSegment);
        }
    }

    private void createShape(){
        path = new Path();
        path.moveTo(upperX,upperY);
        path.lineTo(lowerX, lowerY);
        path.close();
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

    public long getID(){
        return id;
    }

}
