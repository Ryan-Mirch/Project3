package com.example.Divide.Game.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

import com.example.Divide.Game.GameLogic;
import com.example.Divide.Game.MainThread;

public class Pickup implements GameObject {

    private static final int SCORE_INCREASE = 500;

    private String type;
    private int radius;
    private Rect hitbox;
    private Point center;
    private Region region;
    private boolean pickedUp;

    public Pickup(Point center, int radius, String type){
        this.radius = radius;
        this.center = center;
        this.type = type;
        pickedUp = false;
        createHitbox();
    }

    @Override
    public void draw(Canvas canvas){
        if(type.equals("score")){
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(7);
            //canvas.drawCircle(center.x, center.y, radius, paint);
            canvas.drawRect(hitbox,paint);
        }
    }

    @Override
    public void update(long frameTime) {
        move(frameTime);
        createHitbox();
    }

    private void move(long frameTime){
        int speed = GameLogic.getSpeed();
        int pixelsToMove = (int) (speed * (frameTime/(1000/ MainThread.MAX_FPS)));

        center.y += pixelsToMove;
    }

    private void createHitbox(){
        int hitboxRadius = radius + 2;
        hitbox = new Rect( center.x - hitboxRadius,
                                center.y - hitboxRadius,
                               center.x + hitboxRadius,
                             center.y + hitboxRadius);
        region = new Region(hitbox);
    }

    public void pickedUp(){
        if(type.equals("score")){
            // score is increased by 50 * number of branches
            GameLogic.increaseScore(SCORE_INCREASE * GameLogic.getLeadingSegments().size());
        }

        pickedUp = true;
    }

    public Region getRegion(){
        return region;
    }

    public boolean isPickedUp(){
        return pickedUp;
    }
    public boolean belowPoint(int y){
        return(center.y - radius/2) > y;
    }
}
