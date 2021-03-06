package com.example.Divide.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.Divide.Game.GameObjects.Barrier;
import com.example.Divide.Game.GameObjects.Pickup;
import com.example.Divide.Game.GameObjects.Segment;
import com.example.Divide.Game.GameObjects.Trap;

public class GameLayout extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;

    private int scoreTextSize = 50;

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
                int x = (int) event.getX();
                int y = (int) event.getY();
                GameLogic.setScreenWasPressed(new Point(x,y));
                Log.d("screen", "screen was pressed");
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

        if(!GameLogic.getGameOver())drawScore(canvas);
        if(!GameLogic.getGameOver())drawLives(canvas);
        if(!GameLogic.getGameOver())drawBranchAmount(canvas);

        if(GameLogic.getGameOver())drawGameOverMenu(canvas);
    }

    private void drawGameOverMenu(Canvas canvas){
        canvas.drawRect(100,100, canvas.getWidth() - 100, canvas.getHeight() - 100, paint("green fill"));
        canvas.drawRect(110,110, canvas.getWidth()-110, canvas.getHeight() - 110, paint("black fill"));
        canvas.drawText("Game Over",(float) (canvas.getWidth()*0.3), 200, paint("white text"));
        canvas.drawText("Press screen to try again", (float) (canvas.getWidth()*0.15), 500, paint("white text"));

    }

    private void drawScore(Canvas canvas){
        updateScoreTextSize();
        canvas.drawRect(0,0, canvas.getWidth(), 100, paint("black fill"));
        canvas.drawRect(0,100, canvas.getWidth(), 120, paint("green fill"));
        canvas.drawText(Integer.toString(GameLogic.getScore()), (float) (canvas.getWidth() * 0.04), 70, paint("score text") );
    }

    private void drawLives(Canvas canvas){
        canvas.drawText("Lives: " + Integer.toString(GameLogic.getLives()),(float) (canvas.getWidth() * 0.75), 70, paint("white text"));
    }

    private void drawBranchAmount(Canvas canvas){
        canvas.drawText("x" + Integer.toString(GameLogic.getLeadingSegments().size()),(float) (canvas.getWidth() * 0.35), 70, paint("white text"));
    }

    private Paint paint(String type){
        if(type.equals("black fill")){
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            return paint;
        }

        else if(type.equals("green fill")){
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
            return paint;
        }

        else if(type.equals("score text")){
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            paint.setTextSize(scoreTextSize);
            return paint;
        }

        else if(type.equals("white text")){
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            paint.setTextSize(70);
            return paint;
        }
        else return null;
    }

    private void updateScoreTextSize(){
        if(GameLogic.getScorePickupGrabbed()){
            scoreTextSize = 70;
        }
        else{
            scoreTextSize = 60;
        }
    }
}
