package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 750;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;

    public static Region clip = new Region();

    private static int speed = 1;
    private static boolean gameStarted = false;
    private static ArrayList<Segment> segments = new ArrayList<>();
    private static ArrayList<Barrier> barriers = new ArrayList<>();

    private static double timeOfLastSplit = 0;

    public static int centerXPosition = 0;
    public static int leadingYPosition = 0;
    public static int garbageYPosition = 0;



    public static void initializeGame(Canvas c){
        centerXPosition = c.getWidth()/2;
        leadingYPosition = c.getHeight() - 300;
        garbageYPosition = c.getHeight() + 50;
        clip.set(0,0, c.getWidth(), c.getHeight());


        Segment initialSegment = new Segment(centerXPosition, leadingYPosition,0);

        Barrier permanentLeftBarrier = new Barrier(new Point(100,0), new Point(110,c.getHeight()),true);
        Barrier permanentRightBarrier = new Barrier(new Point(c.getWidth() -110, 0), new Point(c.getWidth() - 100, c.getHeight()), true);

        segments.add(initialSegment);
        barriers.add(permanentLeftBarrier);
        barriers.add(permanentRightBarrier);

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

    public static void updateBarriers(){
        ArrayList<Barrier> barriersToRemove = new ArrayList<>();

        for(Barrier b: barriers){
            b.updatePosition();
            if(b.belowPoint(garbageYPosition)){
                barriersToRemove.add(b);
            }
        }

        for(Barrier b: barriersToRemove){
            segments.remove(b);
        }
    }

    public static void updateSegments(){
        ArrayList<Segment> segmentsToRemove = new ArrayList<>();

        for(Segment s: segments){
            s.updatePosition();
            if(s.getUpper().y > garbageYPosition){
                segmentsToRemove.add(s);
            }
        }

        for(Segment s: segmentsToRemove){
            segments.remove(s);
        }

        for(Segment s: getLeadingSegments()){
            s.segmentCollisionCheck();
            s.barrierCollisionCheck();
        }
    }

    public static ArrayList<Segment> getSegments(){
        return segments;
    }

    public static ArrayList<Barrier> getBarriers(){
        return barriers;
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
