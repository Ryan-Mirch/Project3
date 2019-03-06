package com.example.Divide.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.Divide.Game.GameObjects.Barrier;
import com.example.Divide.Game.GameObjects.Pickup;
import com.example.Divide.Game.GameObjects.Segment;
import com.example.Divide.Game.GameObjects.Trap;

public class GameLayout extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;

    public GameLayout(Context context){
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();

        GameLogic.initializeGame();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry){
            try {
                thread.setRunning(false);
                thread.join();
            } catch (Exception e) {e.printStackTrace();}

            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                GameLogic.setScreenWasPressed(true);
                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        canvas.drawColor(Color.BLACK);

        for(Segment segment: GameLogic.getSegments())segment.draw(canvas);
        for(Trap trap: GameLogic.getTraps())trap.draw(canvas);
        for(Barrier barrier: GameLogic.getBarriers())barrier.draw(canvas);
        for(Pickup pickup: GameLogic.getPickups())pickup.draw(canvas);
    }
}
