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
   private boolean up;
   private boolean right;
   private boolean down;
   private boolean left;
   private Point playerStart;
   private Point endGame;
   private Paint playerChar;
   private Point playerPoint;
   private Paint minoChar;
   private int screenWidth;
   private int screenHeight;
   private int moves;
   private boolean dialogIsDisplayed = false;
   private Bitmap mazeImg;
   private Rect image = new Rect();
   private ArrayList<String> fileNameList;



   public MainGameView(Context context, AttributeSet attrs) {
       super(context, attrs);
       activity = (Activity) context;

       getHolder().addCallback(this);

       playerStart = new Point();
       playerChar = new Paint();
       minoChar = new Paint();


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


        if (gameOver){
            gameOver = false;
            mazeThread = new MazeThread(getHolder());
            mazeThread.start();
        }
    }
    //This class is to update the positions of the player and determine where they are compared
    //to the end of the game.
    private void updatePositions(){

    }

    //This method is to draw the overall maze itself from a predetermined picture that is made
    //paint.
    private void drawMazeElements(Canvas canvas, Context context) {
        try {
            Random rand = new Random();
            ArrayList<Integer> randomMaze = new ArrayList<Integer>();
            AssetManager assetManager = context.getAssets();
            String [] listMaze;
            
            for(int i = 0; i < fileNameList.size(); i++){
                randomMaze.add(i);
            }
            int n = rand.nextInt(fileNameList.size());
            String fileName = fileNameList.get(n);
            InputStream inputStream = assetManager.open(fileName);
            mazeImg = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }catch(IOException e){

        }
        image.set(0,0,screenWidth,screenHeight);
        canvas.drawBitmap(mazeImg,null,image,null);
        canvas.drawCircle(playerStart.x, playerStart.y, 10, playerChar);
    }
   /* private void showGameOverDialog(final int messageId){
        final DialogFragment gameResult = new DialogFragment(){
            public Dialog onCreateDialog(Bundle bundle){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageId));

                builder.setMessage("You made it in: " + moves);
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
    private class MyGestureListener extends SimpleOnGestureListener{
        private static final int SWIPE_MIN_DISTANCE = 120;

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