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

public class BorderManager{
    private int screenWidth;
    private int screenHeight;
    private int screenCenter;

    private int segWidth;

    private int X1;//   |1 2 3 4  center  5 6 7 8|
    private int X2;
    private int X3;
    private int X4;
    private int X5;
    private int X6;
    private int X7;
    private int X8;
    private int currentLeftBorderX;
    private int currentRightBorderX;
    private int nextLeftX;
    private int nextRightX;
    private Barrier currentLeftBorder;
    private Barrier currentRightBorder;

    public BorderManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        segWidth = GameLogic.SEGMENT_WIDTH;
        screenCenter = screenWidth/2;
        X1 = 0;
        X2 = X1 + screenWidth/10;
        X3 = X2 + screenWidth/10;
        X4 = X3 + screenWidth/10;

        X8 = screenWidth;
        X7 = X8 - screenWidth/10;
        X6 = X7 - screenWidth/10;
        X5 = X6 - screenWidth/10;
    }

    public void createNewGameBorders(){
        Barrier bottom = new Barrier(new Point(screenCenter, screenHeight-350), new Point(screenCenter, screenHeight-330), X5 - X4);
        Barrier leftVertical = new Barrier(new Point(X4, -screenHeight), new Point(X4, screenHeight-330), 20);
        Barrier rightVertical = new Barrier(new Point(X5, -screenHeight), new Point(X5, screenHeight-330), 20);

        GameLogic.getBarriers().add(bottom);
        GameLogic.getBarriers().add(leftVertical);
        GameLogic.getBarriers().add(rightVertical);

        currentLeftBorder = leftVertical;
        currentRightBorder = rightVertical;

        nextLeftX = leftVertical.getTop().x;
        nextRightX = rightVertical.getTop().x;
    }

    public void manageBorders(){
        setCurrentBorderXs();
        spawnNextBorder();
    }

    private void spawnNextBorder(){

        if(currentLeftBorder.getXWhereY0() != -1)return;

        Log.d("border","spawned new borders");

        Barrier newLeftBorder = newLeftBorder();
        Barrier newRightBorder = newRightBorder();

        GameLogic.getBarriers().add(newLeftBorder);
        GameLogic.getBarriers().add(newRightBorder);

        currentLeftBorder = newLeftBorder;
        currentRightBorder = newRightBorder;

        nextLeftX = newLeftBorder.getTop().x;
        nextRightX = newRightBorder.getTop().x;
    }

    private void setCurrentBorderXs(){
        currentLeftBorderX = currentLeftBorder.getXWhereY0();
        currentRightBorderX = currentRightBorder.getXWhereY0();
    }

    private Barrier newLeftBorder(){
        Barrier leftVertical = new Barrier(new Point(nextLeftX, -screenHeight), new Point(nextLeftX, currentLeftBorder.getTop().y), 20);
        return leftVertical;
    }

    private Barrier newRightBorder(){
        Barrier rightVertical = new Barrier(new Point(nextRightX, -screenHeight), new Point(nextRightX, currentRightBorder.getTop().y), 20);
        return rightVertical;
    }

    public int getCurrentLeftBorderX() {
        return currentLeftBorderX;
    }

    public int getCurrentRightBorderX() {
        return currentRightBorderX;
    }
}
