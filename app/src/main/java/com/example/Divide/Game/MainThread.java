package com.example.Divide.Game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.Divide.Game.GameLayout;
import com.example.Divide.Game.GameLogic;

public class MainThread extends Thread{
    public static final int MAX_FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GameLayout gameLayout;
    private boolean running;

    public static Canvas canvas;

    public void setRunning(boolean running){
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, GameLayout gameLayout){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameLayout = gameLayout;
    }

    @Override
    public void run(){
        long startTime;
        long timeMillis = 1000/MAX_FPS;
        long waitTime = 0;
        long frameTime = 0;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while(running){
            startTime = System.nanoTime();
            canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    GameLogic.gameLoop(frameTime);
                    this.gameLayout.draw(canvas);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime)/1000000;
            waitTime = targetTime - timeMillis;
            try{
                if(waitTime > 0){
                    this.sleep(waitTime);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            frameTime = (System.nanoTime() - startTime)/1000000;

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == MAX_FPS){
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
                Log.d("fps", Double.toString(averageFPS));
            }
        }
    }
}
