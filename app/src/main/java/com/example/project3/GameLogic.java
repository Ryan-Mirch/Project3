package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 500;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;

    private static int speed = 1;
    private static boolean gameStarted = false;
    private static ArrayList<Segment> segments = new ArrayList<>();

    private static double timeOfLastSplit = 0;

    public static int centerXPosition = 0;
    public static int leadingYPosition = 0;
    public static int garbageYPosition = 0;



    public static void initializeGame(Canvas c){
        centerXPosition = c.getWidth()/2;
        leadingYPosition = c.getHeight() - 300;
        garbageYPosition = c.getHeight() + 50;


        Segment initialSegment = new Segment(centerXPosition, leadingYPosition,0);
        segments.add(initialSegment);
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
        for(Segment s: segments){
            if(!s.getLeading())continue;

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

        for(Segment s: segmentsToAdd){
            segments.add(s);
        }
    }

    public static void updateSegments(){
        ArrayList<Segment> segmentsToRemove = new ArrayList<>();

        for(Segment s: segments){
            s.updatePosition();
            if(s.getUpper().x > garbageYPosition){
                segmentsToRemove.add(s);
            }
        }

        for(Segment s: segmentsToRemove){
            segments.remove(s);
        }

        for(Segment s: getLeadingSegments()){
            s.segmentCollisionCheck();
        }
    }

    public static ArrayList<Segment> getSegments(){
        return segments;
    }

    public static ArrayList<Segment> getLeadingSegments(){
         ArrayList<Segment> leadingSegments = new ArrayList<>();
         for(Segment s: segments){
             if(s.getLeading()){
                 leadingSegments.add(s);
             }
         }
        return leadingSegments;
    }

    public static int getSpeed(){
        return speed;
    }

    public static boolean getGameStarted(){
        return gameStarted;
    }
}
