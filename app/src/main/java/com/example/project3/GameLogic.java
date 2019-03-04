package com.example.project3;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 400;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;

    public static Region clip = new Region();

    private static Random random = new Random();

    private static int ySpeed = 8;
    private static int barrierSpawnFrequency = 4; // 1 every x seconds, on average.
    private static int trapSpawnFrequency = 3; // 1 every x seconds, on average.

    private static boolean gamePlaying = false;
    private static boolean startNewGame = false;
    private static boolean screenWasPressed = false;
    private static String nextPress = "divide"; //either 'divide' or 'straighten


    private static List<Segment> segments = Collections.synchronizedList(new ArrayList<Segment>());
    private static List<Barrier> barriers = Collections.synchronizedList(new ArrayList<Barrier>());
    private static List<Trap> traps = Collections.synchronizedList(new ArrayList<Trap>());
    private static ArrayList<Segment> newSegments = new ArrayList<>();

    private static double lastPressTime = 0;
    private static double lastBarrierSpawnTime = 0;
    private static double lastTrapSpawnTime = 0;

    public static int centerXPosition = 0;
    public static int leadingYPosition = 0;
    public static int garbageYPosition = 0;



    public static void initializeGame(){
        if(!startNewGame)return;

        nextPress = "divide";
        segments.clear();
        barriers.clear();
        traps.clear();

        centerXPosition = getWidth()/2;
        leadingYPosition = getHeight() - 400;
        garbageYPosition = getHeight() + 50;
        clip.set(0,0, getWidth(), getHeight());


        Segment initialSegment = new Segment(centerXPosition, leadingYPosition,0);

        Barrier permanentLeftBarrier = new Barrier(new Point(100,0), new Point(110, getHeight()),true);
        Barrier permanentRightBarrier = new Barrier(new Point(getWidth() -110, 0), new Point(getWidth() - 100, getHeight()), true);

        segments.add(initialSegment);
        barriers.add(permanentLeftBarrier);
        barriers.add(permanentRightBarrier);

        gamePlaying = true;
        startNewGame = false;
    }

    public static void gameLoop(long frameTime){
        spawnBarriers();
        spawnTraps();
        screenPressed();
        for(Barrier barrier: barriers)barrier.update(frameTime);
        for(Segment segment: segments)segment.update(frameTime);
        for(Trap trap: traps)trap.update(frameTime);
        addNewSegments();
        removeOffscreenObjects();
    }

    private static void addNewSegments(){
        segments.addAll(newSegments);
        newSegments.clear();
    }

    public static void screenPressed(){
        if(!screenWasPressed)return;
        Log.d("Input", "screen pressed");
        //if every segment is straight, pressing will divide, even if the next press should straighten.
        boolean diagonal = false;
        for(Segment s: getLeadingSegments()){
            if(s.getDirection() != 0){
                diagonal = true;
                break;
            }
        }

        if(!diagonal)nextPress = "divide";

        if(nextPress.equals("divide")){
            divide();
            nextPress = "straighten";
            lastPressTime = System.currentTimeMillis();
        }

        //will only work if timeOfLastSplit + SPLIT_INTERVAL is less than current time
        else if( nextPress.equals("straighten") &&
                (lastPressTime + SPLIT_INTERVAL < System.currentTimeMillis())){

            straightenSegments();
            nextPress = "divide";
        }
        screenWasPressed = false;

    }

    public static void spawnBarriers(){
        int spawnChance = random.nextInt(barrierSpawnFrequency * 1000);
        if(spawnChance == 1 || lastBarrierSpawnTime + (1000 * barrierSpawnFrequency * 1.5) < System.currentTimeMillis()){

            lastBarrierSpawnTime = System.currentTimeMillis();
            int xPos = random.nextInt(getWidth());
            int barrierType = random.nextInt(2); //random number from 0 to 1;

            switch (barrierType){
                case 0:
                    Barrier small = new Barrier(new Point(0 + xPos,0), new Point(50 + xPos,10), false); // type: 0
                    barriers.add(small);
                    break;
                case 1:
                    Barrier large = new Barrier(new Point(0 + xPos,0), new Point(100 + xPos,10), false); // type: 1
                    barriers.add(large);
                    break;
            }
        }
    }

    public static void spawnTraps(){
        int spawnChance = random.nextInt(trapSpawnFrequency * 1000);

        if(spawnChance == 1 || lastTrapSpawnTime + (1000 * trapSpawnFrequency * 1.5) < System.currentTimeMillis()){

            lastTrapSpawnTime = System.currentTimeMillis();
            int trapType = random.nextInt(2); //random number from 0 to 1;
            int xPos = random.nextInt(getWidth());

            switch (trapType){
                case 0:
                    Trap small = new Trap(new Point(xPos,0),50); // type: 0
                    traps.add(small);
                    break;
                case 1:
                    Trap large = new Trap(new Point(xPos,0), 100); // type: 1
                    traps.add(large);
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

    private static void divide(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();

        for(Segment s: getLeadingSegments()){

            if(s.getDirection() != -1){
                Segment newSegment = new Segment(s.getUpper().x - 2, s.getUpper().y, -1);
                segmentsToAdd.add(newSegment);
                Log.d("Segment", "new segment created");
            }

            if(s.getDirection() != 1){
                Segment newSegment = new Segment(s.getUpper().x + 2, s.getUpper().y, 1);
                segmentsToAdd.add(newSegment);
                Log.d("Segment", "new segment created");
            }

            if(s.getDirection() == 0){
                s.setLeading(false);
            }
        }
        segments.addAll(segmentsToAdd);
    }

    public static void removeOffscreenObjects(){
        ArrayList<Segment> segmentsToRemove = new ArrayList<>();
        ArrayList<Barrier> barriersToRemove = new ArrayList<>();
        ArrayList<Trap> trapsToRemove = new ArrayList<>();

        for(Segment s: segments){
            if(s.getUpper().y > garbageYPosition){
                segmentsToRemove.add(s);
            }
        }

        for(Barrier b: barriers){
            if(b.belowPoint(garbageYPosition)){
                barriersToRemove.add(b);
            }
        }

        for(Trap t: traps){
            if(t.belowPoint(garbageYPosition)){
                trapsToRemove.add(t);
            }
        }

        segments.removeAll(segmentsToRemove);
        barriers.removeAll(barriersToRemove);
        traps.removeAll(trapsToRemove);
    }

    public static List<Segment> getSegments(){
        return segments;
    }

    public static List<Barrier> getBarriers(){
        return barriers;
    }

    public static List<Trap> getTraps(){
        return traps;
    }

    public static ArrayList<Segment> getNewSegments(){
        return newSegments;
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
        return ySpeed;
    }

    public static boolean getGamePlaying(){
        return gamePlaying;
    }

    public static void setStartNewGame(boolean b){
        startNewGame = b;
    }

    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static void setScreenWasPressed(boolean screenWasPressed) {
        GameLogic.screenWasPressed = screenWasPressed;
    }
}
