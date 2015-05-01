// CannonView.java
// Displays and controls the Maze Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector.SimpleOnGestureListener;

import java.io.IOException;
import java.io.InputStream;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback{
   private static final String TAG = "MainGameView";
   private MazeThread mazeThread;
   private Activity activity;
   private boolean gameOver;
   private int moves;
   private Point endGame;
   private Paint playerChar;
   private Point playerPoint;
    private Point playerStart;
   private int screenWidth;
   private int screenHeight;
   private float imgWidth;
   private float imgHeight;
   private boolean dialogIsDisplayed = false;
   private Bitmap mazeImg;
   private Rect imageRect = new Rect();
   private GestureDetectorCompat gestureDetector;


   public MainGameView(Context context, AttributeSet attrs) {
       super(context, attrs);
       activity = (Activity) context;

       getHolder().addCallback(this);
       playerChar = new Paint();
       playerPoint = new Point ();
       playerStart = new Point();
       endGame = new Point();
       gestureDetector = new GestureDetectorCompat(context, new SwipeListener());
        Log.w(TAG, "Created!");


   }
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        screenWidth = w;
        screenHeight = h;
        Log.w(TAG, "Screen width and Height:  " + screenWidth  + " " + screenHeight);
        newGame();
    }
    //starts a new game with the player character at the starting point.
    public void newGame(){
        playerPoint.x = 25;
        playerPoint.y = 30;
        playerStart.x = 25;
        playerStart.y = 30;
        endGame.x = screenWidth - 20;
        moves = 0;


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

    //This method is to draw the overall maze itself from a predetermined picture that is made from
    //paint.
    private void drawMazeElements(Canvas canvas, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("maze1.png");
            mazeImg = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }catch(IOException e){

        }
        imgHeight = mazeImg.getHeight();
        imgWidth = mazeImg.getWidth();
        imageRect.set(0, 0, screenWidth, screenHeight);
        canvas.drawBitmap(mazeImg, null, imageRect, null);
        canvas.drawCircle(playerPoint.x, playerPoint.y, 10, playerChar);
        if(playerPoint.x == endGame.x){
            gameOver = true;
            mazeThread.setRunning(false);
            showGameOverDialog(R.string.game_over);
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
    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.w(TAG, "Touch Event: " + event);
        this.gestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }
    //returns true if the destination coordinate is a wall
    public boolean wallCheck(float destinationX, float destinationY){
        float fX =  destinationX / screenWidth;
        float fY =  destinationY / screenHeight;
        float playerPointImgX = fX * imgWidth;
        float playerPointImgY = fY * imgHeight;
        int pixelColor = mazeImg.getPixel((int) playerPointImgX, (int) playerPointImgY);
        return (pixelColor == Color.BLACK);


    }

    private class SwipeListener extends SimpleOnGestureListener{
        private static final int SWIPE_MIN_DISTANCE = 100;
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                final float xDistance = e1.getX() - e2.getX();
                final float yDistance = e1.getY() - e2.getY();
            Log.w(TAG, "xDistance: " + xDistance);
            Log.w(TAG, "yDistance: " + yDistance);
            Log.w(TAG, "moves " + moves);
                if(xDistance > SWIPE_MIN_DISTANCE){
                    onSwipeLeft();
                    return true;
                }else if(xDistance < -SWIPE_MIN_DISTANCE){
                    onSwipeRight();
                    return true;
                }else if(yDistance > SWIPE_MIN_DISTANCE){
                    onSwipeUp();
                    return true;
                }else if(yDistance < -SWIPE_MIN_DISTANCE){
                    onSwipeDown();
                    return true;
                }
                return false;

            }
        protected void onSwipeLeft(){
            if(!wallCheck(playerPoint.x - 20, playerPoint.y) && playerPoint.x - 20 > playerStart.x) {
                playerPoint.x = playerPoint.x - 20;
            }
            moves++;
        }


        protected void onSwipeRight(){
            if(!wallCheck(playerPoint.x + 20, playerPoint.y) && playerPoint.x + 20 < screenWidth) {
                playerPoint.x = playerPoint.x + 20;
            }
            moves++;
        }


        protected void onSwipeUp(){
            if(!wallCheck(playerPoint.x, playerPoint.y - 20) && playerPoint.y - 20 > 0) {
                playerPoint.y = playerPoint.y - 20;
            }
            moves++;

        }

        protected void onSwipeDown(){
            if(!wallCheck(playerPoint.x, playerPoint.y + 20) && playerPoint.y + 20 < screenHeight) {
                playerPoint.y = playerPoint.y + 20;
            }
            moves++;
        }





    }
    private void showGameOverDialog(final int messageId){
        final DialogFragment gameResult =
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));
                        builder.setMessage(getResources().getString(R.string.moves_made, moves));
                        builder.setPositiveButton(R.string.reset_game,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogIsDisplayed = false;
                                        newGame();
                                    }

                                }
                        );
                        return builder.create();
                    }
                };
        activity.runOnUiThread(
                new Runnable(){
                    public void run(){
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false);
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                }
        );
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