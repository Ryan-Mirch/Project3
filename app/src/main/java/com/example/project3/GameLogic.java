package com.example.project3;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 400;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;

    public static Region clip = new Region();

    private static Random random = new Random();

    private static int speed = 1;
    private static int barrierSpawnFrequency = 4; // 1 every x seconds, on average.

    private static boolean gamePlaying = false;
    private static boolean startNewGame = false;
    private static String nextPress = "divide"; //either 'divide' or 'straighten


    private static ArrayList<Segment> segments = new ArrayList<>();
    private static ArrayList<Barrier> barriers = new ArrayList<>();

    private static double lastPressTime = 0;
    private static double lastBarrierSpawnTime = 0;

    public static int centerXPosition = 0;
    public static int leadingYPosition = 0;
    public static int garbageYPosition = 0;



    public static void initializeGame(Canvas c){

        if(!startNewGame)return;

        nextPress = "divide";
        segments.clear();

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

        gamePlaying = true;
        startNewGame = false;
    }


    public static void screenPressed(){

        boolean diagonal = false;
        for(Segment s: getLeadingSegments()){
            if(s.getDirection() != 0){
                diagonal = true;
                break;
            }
        }

        if(!diagonal)nextPress = "divide";

        if(nextPress.equals("divide")){
            addSegments();
            nextPress = "straighten";
            lastPressTime = System.currentTimeMillis();
        }

        //will only work if timeOfLastSplit + SPLIT_INTERVAL is less than current time
        else if( nextPress.equals("straighten") &&
                (lastPressTime + SPLIT_INTERVAL < System.currentTimeMillis())){

            straightenSegments();
            nextPress = "divide";
        }


    }

    public static void spawnObjects(){

        Barrier small = new Barrier(new Point(0,0), new Point(50,10), false); // type: 0
        Barrier large = new Barrier(new Point(0,0), new Point(100,10), false); // type: 1

        int barrierType = random.nextInt(2);
        int spawnChance = random.nextInt(barrierSpawnFrequency * 1000);

        if(spawnChance == 1 || lastBarrierSpawnTime + (1000 * barrierSpawnFrequency * 1.5) < System.currentTimeMillis()){
            lastBarrierSpawnTime = System.currentTimeMillis();
            switch (barrierType){
                case 0:
                    barriers.add(small);
                    break;
                case 1:
                    barriers.add(large);
                    break;
            }
        }
    }

    private static void straightenSegments(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();

        for(Segment s: getLeadingSegments()) {
            if(s.getDirection() != 0){
                Segment newSegment = new Segment(s.getUpper().x, s.getUpper().y, 0);
                segmentsToAdd.add(newSegment);
                s.setLeading(false);
            }
        }

        segments.addAll(segmentsToAdd);
    }

    private static void addSegments(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();

        for(Segment s: getLeadingSegments()){

            if(s.getDirection() != -1){
                Segment newSegment = new Segment(s.getUpper().x, s.getUpper().y, -1);
                segmentsToAdd.add(newSegment);
            }

            if(s.getDirection() != 1){
                Segment newSegment = new Segment(s.getUpper().x, s.getUpper().y, 1);
                segmentsToAdd.add(newSegment);
            }

            if(s.getDirection() == 0){
                s.setLeading(false);
            }
        }
        segments.addAll(segmentsToAdd);
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
            barriers.remove(b);
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

    public static boolean getGamePlaying(){
        return gamePlaying;
    }

    public static void setStartNewGame(boolean b){
        startNewGame = b;
    }
}
