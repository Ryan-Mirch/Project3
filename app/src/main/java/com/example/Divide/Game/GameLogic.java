package com.example.Divide.Game;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Region;
import android.util.Log;

import com.example.Divide.Game.GameObjects.Barrier;
import com.example.Divide.Game.GameObjects.Pickup;
import com.example.Divide.Game.GameObjects.Segment;
import com.example.Divide.Game.GameObjects.Trap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameLogic {
    public static final int SPLIT_INTERVAL = 100;  //in milliseconds
    public static final int SEGMENT_WIDTH = 20;
    public static final int SCORE_UPDATE_INTERVAL = 100; //in milliseconds

    public static Region clip = new Region();

    private static Random random = new Random();

    private static BorderManager borderManager;

    private static int score;
    private static int lives;

    private static int ySpeed = 5;
    private static int barrierMaxSpawnDelay = 7; // 1 every x seconds at least.
    private static int trapMaxSpawnDelay = 4; // 1 every x seconds, at least.
    private static int pickupMaxSpawnDelay = 4; // 1 every x seconds, at least.

    private static int currentID = 0;

    private static boolean scorePickupGrabbed = false;
    private static boolean gamePlaying = false;
    private static boolean startNewGame = false;
    private static boolean screenWasPressed = false;
    private static String nextPress = "divide"; //either 'divide' or 'straighten

    private static List<Segment> segments = Collections.synchronizedList(new ArrayList<Segment>());
    private static List<Barrier> barriers = Collections.synchronizedList(new ArrayList<Barrier>());
    private static List<Trap> traps = Collections.synchronizedList(new ArrayList<Trap>());
    private static List<Pickup> pickups = Collections.synchronizedList(new ArrayList<Pickup>());
    private static ArrayList<Segment> newSegments = new ArrayList<>();

    private static double lastScoreUpdate = 0;
    private static double lastPressTime = 0;
    private static double lastScorePickupTime = 0;

    private static double nextBarrierSpawnTime = 0;
    private static double nextTrapSpawnTime = 0;
    private static double nextPickupSpawnTime = 0;

    public static int centerXPosition = 0;
    public static int leadingYPosition = 0;
    public static int garbageYPosition = 0;

    public static void initializeGame(){
        if(!startNewGame)return;

        borderManager = new BorderManager(getWidth(), getHeight());

        lives = 3;
        score = 0;
        nextPress = "divide";

        segments.clear();
        barriers.clear();
        traps.clear();
        pickups.clear();

        centerXPosition = getWidth()/2;
        leadingYPosition = getHeight() - 400;
        garbageYPosition = getHeight() + 50;
        clip.set(0,0, getWidth(), getHeight());

        Segment initialSegment = new Segment(centerXPosition, leadingYPosition,0, 0);
        Trap initialTrap = new Trap(new Point(getWidth()/2, getHeight()/2 - 120), 50);
        Pickup initialPickup = new Pickup(new Point(getWidth()/2, getHeight()/2), 10, "score");

        segments.add(initialSegment);
        traps.add(initialTrap);
        pickups.add(initialPickup);

        borderManager.createNewGameBorders();
        borderManager.manageBorders();

        gamePlaying = true;
        startNewGame = false;
    }

    public static void gameLoop(long frameTime){
        if(gameOverCheck())return;
        spawnBarriers();
        spawnTraps();
        spawnPickups();
        screenPressed();
        borderManager.manageBorders();
        for(Barrier barrier: barriers)barrier.update(frameTime);
        for(Segment segment: segments)segment.update(frameTime);
        for(Trap trap: traps)trap.update(frameTime);
        for(Pickup pickup: pickups)pickup.update(frameTime);
        addNewSegments();
        removeOffscreenObjects();
        updateScore();

    }

    private static boolean gameOverCheck(){
        if(lives > 0) return false;
        if(getLeadingSegments().size() > 0) return false;
        return true;
    }

    private static void addNewSegments(){
        segments.addAll(newSegments);
        newSegments.clear();
    }

    private static void updateScore(){
        if(lastScoreUpdate + SCORE_UPDATE_INTERVAL <= System.currentTimeMillis()){
            lastScoreUpdate = System.currentTimeMillis();
            int scoreIncrease = getLeadingSegments().size() * 10;
            score += scoreIncrease;
        }


        if(lastScorePickupTime + 7 <= System.currentTimeMillis() && scorePickupGrabbed){
            scorePickupGrabbed = false;
        }



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
        if(nextBarrierSpawnTime > System.currentTimeMillis())return;

        int randomSpawnTime = random.nextInt(1000*barrierMaxSpawnDelay);
        nextBarrierSpawnTime = System.currentTimeMillis() + randomSpawnTime;

        int boundaryWidth = borderManager.getCurrentRightBorderX() - borderManager.getCurrentLeftBorderX();
        int width = random.nextInt(60) + 50;
        int xPos = random.nextInt(boundaryWidth - width) + borderManager.getCurrentLeftBorderX() + width/2;

        Barrier newBarrier = new Barrier(new Point(xPos,0), new Point(xPos,20), width); // type: 0
        barriers.add(newBarrier);
    }

    public static void spawnTraps(){
        if(nextTrapSpawnTime > System.currentTimeMillis())return;

        int boundaryWidth = borderManager.getCurrentRightBorderX() - borderManager.getCurrentLeftBorderX();
        int width = random.nextInt(60) + 20;
        int xPos = random.nextInt(boundaryWidth - width) + borderManager.getCurrentLeftBorderX() + width/2;

        for(Barrier check: GameLogic.getBarriers()){
            if(check.getRegion().contains(xPos, 0)){
                nextPickupSpawnTime += 10;
                return;
            }
        }

        for(Pickup check: GameLogic.getPickups()){
            if(check.getRegion().contains(xPos, 0)){
                nextPickupSpawnTime += 10;
                return;
            }
        }

        int randomSpawnTime = random.nextInt(1000*trapMaxSpawnDelay);
        nextTrapSpawnTime = System.currentTimeMillis() + randomSpawnTime;

        Trap newTrap = new Trap(new Point(xPos,0),width);
        traps.add(newTrap);
    }

    public static void spawnPickups(){
        if(nextPickupSpawnTime > System.currentTimeMillis())return;

        int boundaryWidth = borderManager.getCurrentRightBorderX() - borderManager.getCurrentLeftBorderX();
        int width = 20;
        int xPos = random.nextInt(boundaryWidth - width) + borderManager.getCurrentLeftBorderX() + width/2;

        for(Barrier check: GameLogic.getBarriers()){
            if(check.getRegion().contains(xPos, 0)){
                nextPickupSpawnTime += 10;
                return;
            }
        }

        for(Pickup check: GameLogic.getPickups()){
            if(check.getRegion().contains(xPos, 0)){
                nextPickupSpawnTime += 10;
                return;
            }
        }

        int randomSpawnTime = random.nextInt(1000*pickupMaxSpawnDelay);
        nextPickupSpawnTime = System.currentTimeMillis() + randomSpawnTime;

        Pickup newPickup = new Pickup(new Point(xPos,0),10,"score");
        pickups.add(newPickup);
    }

    private static void straightenSegments(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();

        for(Segment s: getLeadingSegments()) {
            if(s.getDirection() != 0){
                Segment newSegment = new Segment(s.getUpper().x, s.getUpper().y, 0, 0);
                segmentsToAdd.add(newSegment);
                s.setLeading(false);
            }
        }

        segments.addAll(segmentsToAdd);
    }

    private static void divide(){
        ArrayList<Segment> segmentsToAdd = new ArrayList<>();

        for(Segment s: getLeadingSegments()){
            int id = getCurrentID();
            if(s.getDirection() != -1){
                Segment newSegment = new Segment(s.getUpper().x - 2, s.getUpper().y, -1, id);
                segmentsToAdd.add(newSegment);
                Log.d("Segment", "new segment created");
            }

            if(s.getDirection() != 1){
                Segment newSegment = new Segment(s.getUpper().x + 2, s.getUpper().y, 1, id);
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
        ArrayList<Pickup> pickupsToRemove = new ArrayList<>();

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

        for(Pickup p: pickups){
            if(p.belowPoint(garbageYPosition) || p.isPickedUp()){
                pickupsToRemove.add(p);
            }
        }

        segments.removeAll(segmentsToRemove);
        barriers.removeAll(barriersToRemove);
        traps.removeAll(trapsToRemove);
        pickups.removeAll(pickupsToRemove);
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

    public static List<Pickup> getPickups(){
        return pickups;
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

    public static int getCurrentID(){
        currentID++;
        return currentID;
    }

    public static void increaseScore(int increase){
        scorePickupGrabbed = true;
        lastScorePickupTime = System.currentTimeMillis();
        score += increase;
    }

    public static int getScore(){
        return score;
    }

    public static int getLives(){return lives;}

    public static void increaseLives(){lives++;}

    public static void decreaseLives(){lives--;}

    public static boolean getScorePickupGrabbed(){
        return scorePickupGrabbed;
    }
}
