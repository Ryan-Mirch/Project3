package com.example.Divide;


import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class BorderManager{
    private int screenWidth;
    private int screenHeight;
    private int screenCenter;

    private int segWidth;

    private Random random = new Random();

    private int X1;//   |1 2 3 4  center  5 6 7 8|
    private int X2;
    private int X3;
    private int X4;
    private int X5;
    private int X6;
    private int X7;
    private int X8;

    private ArrayList<Integer> leftXs = new ArrayList<>();
    private ArrayList<Integer> rightXs = new ArrayList<>();

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
        X1 = 0;                   leftXs.add(X1);
        X2 = X1 + screenWidth/10; leftXs.add(X2);
        X3 = X2 + screenWidth/10; leftXs.add(X3);
        X4 = X3 + screenWidth/10; leftXs.add(X4);



        X8 = screenWidth;         rightXs.add(X8);
        X7 = X8 - screenWidth/10; rightXs.add(X7);
        X6 = X7 - screenWidth/10; rightXs.add(X6);
        X5 = X6 - screenWidth/10; rightXs.add(X5);
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
        int xIndex = random.nextInt(leftXs.size());
        Barrier newBarrier = new Barrier(new Point(leftXs.get(xIndex), -screenHeight/2), new Point(nextLeftX, currentLeftBorder.getTop().y), 20);
        return newBarrier;
    }

    private Barrier newRightBorder(){
        int xIndex = random.nextInt(rightXs.size());
        Barrier newBarrier = new Barrier(new Point(rightXs.get(xIndex), -screenHeight/2), new Point(nextRightX, currentRightBorder.getTop().y), 20);
        return newBarrier;
    }

    public int getCurrentLeftBorderX() {
        return currentLeftBorderX;
    }

    public int getCurrentRightBorderX() {
        return currentRightBorderX;
    }
}
