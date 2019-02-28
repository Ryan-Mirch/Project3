package com.example.project3;

import android.graphics.Canvas;

import java.util.ArrayList;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 500;  //in milliseconds

    private static int speed = 1;
    private static boolean gameStarted = false;
    private static ArrayList<SegmentInterface> segments = new ArrayList<>();

    private static boolean splitting = false; //splitting or going straight
    private static double timeOfLastSplit = 0;



    public static void initializeGame(Canvas c){
        SegmentVertical initialSegment = new SegmentVertical(   c.getWidth()/2,
                                                                c.getHeight() - 300,
                                                                c.getWidth()/2,
                                                                c.getHeight() - 300);
        segments.add(initialSegment);
        gameStarted = true;
    }

    //will only work if timeOfLastSplit + SPLIT_INTERVAL is less than current time
    public static void toggleSplitting(){
        if(timeOfLastSplit + SPLIT_INTERVAL < System.currentTimeMillis()){
            splitting = !splitting;
            timeOfLastSplit = System.currentTimeMillis();
        }
        else{
            //pressing the split button too fast.
        }
    }

    public static ArrayList<SegmentInterface> getSegments(){
        return segments;
    }

    public static int getSpeed(){
        return speed;
    }

    public static boolean getGameStarted(){
        return gameStarted;
    }

    //splitting = true, going straight = false.
    public static boolean isSplitting(){
        return splitting;
    }




}
