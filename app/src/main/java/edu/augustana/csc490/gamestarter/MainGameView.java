// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import java.util.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

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
   private Point playerPoint;
   private int screenWidth;
   private int screenHeight;
   private boolean dialogIsDisplayed = false;
   private Bitmap mazeImg;
   private Rect image = new Rect();
   private GestureDetector gesture;



   public MainGameView(Context context, AttributeSet attrs) {
       super(context, attrs);
       activity = (Activity) context;

       getHolder().addCallback(this);

       playerStart = new Point();
       playerChar = new Paint();
       playerPoint = new Point ();
       endGame = new Point();



   }
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        screenWidth = w;
        screenHeight = h;

        newGame();
    }
    //starts a new game with the player character at the starting point.
    public void newGame(){
        playerStart.x = 25;
        playerStart.y = 35;
        playerPoint.x = 25;
        playerPoint.y = 35;
        endGame.x = screenWidth;
        endGame.y = screenHeight;


        if (gameOver){
            gameOver = false;
            mazeThread = new MazeThread(getHolder());
            mazeThread.start();
        }
    }
    //This class is to update the positions of the player and determine where they are compared
    //to the end of the game.
    private void updatePositions(){
        this.setOnTouchListener(new MyGestureDetector(){
            public void onSwipeLeft(){
                playerPoint.x = playerPoint.x - 10;
            }
            public void onSwipeRight(){
                playerPoint.x = playerPoint.x + 10;
            }
            public void onSwipeUp(){
                playerPoint.y = playerPoint.y - 10;
            }
            public void onSwipeDown(){
                playerPoint.y = playerPoint.y + 10;
            }
        });
    }


    //This method is to draw the overall maze itself from a predetermined picture that is made
    //paint.
    private void drawMazeElements(Canvas canvas, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("maze1.png");
            mazeImg = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }catch(IOException e){

        }
        image.set(0,0,screenWidth,screenHeight);
        canvas.drawBitmap(mazeImg,null,image,null);
        canvas.drawCircle(playerStart.x, playerStart.y, 10, playerChar);
    }
   /*private void showGameOverDialog(final int messageId){
        final DialogFragment gameResult = new DialogFragment(){
            public Dialog onCreateDialog(Bundle bundle){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageId));
                builder.setPositiveButton("Reset Game", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialogIsDisplayed = false;
                        newGame();
                    }
                });
                activity.runOnUiThread(new Runnable(){
                    public void run(){
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false);
                        gameResult.show(activity.getFragmentManager());
                    }
                });
            }
        }
    }
    */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void surfaceCreated(SurfaceHolder holder){

        if(gameOver == false){
            mazeThread = new MazeThread(holder);
            mazeThread.setRunning(true);
            mazeThread.start();
        }
    }
    public void stopGame(){
        if(mazeThread != null){
            mazeThread.setRunning(false);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        mazeThread.setRunning(false);

        while(retry){
            try{
                mazeThread.join();
                retry =false;
            }catch(InterruptedException e){
                Log.e(TAG, "Thread Interrupted ", e);
            }
        }
    }
    private class MyGestureDetector implements OnTouchListener{
        private static final int SWIPE_MIN_DISTANCE = 100;

        public boolean onTouch(final View v, final MotionEvent event){
           gesture.onTouchEvent(event);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                final float xDistance = e1.getX() - e2.getX();
                final float yDistance = e1.getY() - e2.getY();
                if(xDistance > SWIPE_MIN_DISTANCE){
                    onSwipeLeft();
                    return true;
                }else if(xDistance < -SWIPE_MIN_DISTANCE){
                    onSwipeRight();
                    return true;
                }else if(yDistance > SWIPE_MIN_DISTANCE){
                    onSwipeDown();
                    return true;
                }else if(yDistance < -SWIPE_MIN_DISTANCE){
                    onSwipeUp();
                    return true;
                }
                return false;

            }
        protected void onSwipeLeft(){

        }


        protected void onSwipeRight(){

        }


        protected void onSwipeUp(){

        }


       protected void onSwipeDown(){

       }



    }
       //creates the thread for which the game runs until the game is over.
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

        public void run(){

            Canvas canvas = null;

            while(threadIsRunning){
                try{
                    canvas = surfaceHolder.lockCanvas(null);
                        synchronized(surfaceHolder){
                            updatePositions();
                            drawMazeElements(canvas, getContext());
                        }
                }finally{
                    if (canvas != null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }


}