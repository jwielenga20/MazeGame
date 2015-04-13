// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
   private static final String TAG = "MainGameView";
   private MazeThread mazeThread;
   private Activity activity;
   private boolean gameOver;
   private boolean wallTouch;
   private Point playerStart;
   private Point endGame;
   private Paint playerChar;
   private Paint minoChar;
   private Paint backgroundPaint;
   private int screenWidth;
   private int screenHeight;



   public MainGameView(Context context, AttributeSet attrs){
       super(context, attrs);
       activity = (Activity) context;

       getHolder().addCallback(this);

       playerStart = new Point();
       playerChar = new Paint();
       minoChar = new Paint();
       backgroundPaint = new Paint();
   }

    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        screenWidth = w;
        screenHeight = h;
        backgroundPaint.setColor(Color.BLACK);

        newGame();
    }

    public void newGame(){
        playerStart.x = 25;
        playerStart.y = 25;

        if (gameOver){
            gameOver = false;
            mazeThread = new MazeThread(getHolder());
            mazeThread.start();
        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void surfaceCreated(SurfaceHolder holder){

        if(gameOver == false){
            mazeThread = new MazeThread(holder);
            mazeThread.setRunning(true);
            mazeThread.start();
        }
    }
    private class MazeThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public MazeThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("MazeThread");

        }

        public void setRunning(boolean running){
            threadIsRunning = running;
        }
    }
}