package com.example.project3;

import android.graphics.Canvas;

import java.util.ArrayList;

public class GameLogic {
    private static int speed = 1;
    private static ArrayList<SegmentInterface> segments = new ArrayList<>();
    private static boolean gameStarted = false;

    public static void initializeGame(Canvas c){
        SegmentVertical initialSegment = new SegmentVertical(   c.getWidth()/2,
                                                                c.getHeight() - 100,
                                                                c.getWidth()/2,
                                                                c.getHeight() - 90);
        segments.add(initialSegment);
        gameStarted = true;
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
}
