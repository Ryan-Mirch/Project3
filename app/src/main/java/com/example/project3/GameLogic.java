package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 500;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;

    private static int speed = 1;
    private static boolean gameStarted = false;
    private static ArrayList<Segment> segments = new ArrayList<>();
    private static ArrayList<Segment> leadingSegments = new ArrayList<>();

    private static boolean splitting = false; //splitting or going straight
    private static double timeOfLastSplit = 0;



    public static void initializeGame(Canvas c){
        Segment initialSegment = new Segment(   c.getWidth()/2,
                                                c.getHeight() - 300,
                                                0);
        segments.add(initialSegment);
        leadingSegments.add(initialSegment);
        gameStarted = true;
    }

    //will only work if timeOfLastSplit + SPLIT_INTERVAL is less than current time
    public static void split(){
        if(timeOfLastSplit + SPLIT_INTERVAL < System.currentTimeMillis()){
            timeOfLastSplit = System.currentTimeMillis();
            addSegments();
        }
        else{
            //pressing the split button too fast.
        }
    }

    private static void addSegments(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();
        for(Segment s: leadingSegments){

            Point upper = s.getUpper();
            int upperX = upper.x;
            int upperY = upper.y;

            if(s.getDirection() != 0){
                Segment newSegment = new Segment(upperX, upperY, 0);
                segmentsToAdd.add(newSegment);
            }

            else{
                Segment newLeftSegment = new Segment(upperX, upperY, -1);
                Segment newRightSegment = new Segment(upperX, upperY, 1);
                segmentsToAdd.add(newLeftSegment);
                segmentsToAdd.add(newRightSegment);
            }
            s.setLeading(false);
        }
        leadingSegments.clear();

        for(Segment s: segmentsToAdd){
            segments.add(s);
            leadingSegments.add(s);
        }
    }

    public static ArrayList<Segment> getSegments(){
        return segments;
    }

    public static ArrayList<Segment> getLeadingSegments(){
        return leadingSegments;
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
