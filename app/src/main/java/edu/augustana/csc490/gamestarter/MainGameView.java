// CannonView.java
// Displays and controls the Maze Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
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
   private boolean wallTouch;
   private int moves;
   private Point endGame;
   private Paint playerChar;
   private Point playerPoint;
   private int screenWidth;
   private int screenHeight;
   private float imgWidth;
   private float imgHeight;
   private float fX;
   private float fY;
   private boolean dialogIsDisplayed = false;
   private Bitmap mazeImg;
   private Rect imageRect = new Rect();
   private GestureDetectorCompat gestureDetector;
   private float playerPointX;
   private float playerPointY;



   public MainGameView(Context context, AttributeSet attrs) {
       super(context, attrs);
       activity = (Activity) context;

       getHolder().addCallback(this);
       playerChar = new Paint();
       playerPoint = new Point ();
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
        endGame.x = screenWidth;
        endGame.y = screenHeight;
        fX = (float) playerPoint.x / screenWidth;
        fY = (float) playerPoint.y / screenHeight;

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
        playerPointX = fX * imgWidth;
        playerPointY = fY * imgHeight;
        imageRect.set(0, 0, screenWidth, screenHeight);
        canvas.drawBitmap(mazeImg,null, imageRect,null);
        canvas.drawCircle(playerPoint.x, playerPoint.y, 10, playerChar);


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
    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.w(TAG, "Touch Event: " + event);
        this.gestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    public boolean wallCheck(){
        return true;
    }

    private class SwipeListener extends SimpleOnGestureListener{
        private static final int SWIPE_MIN_DISTANCE = 100;
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                final float xDistance = e1.getX() - e2.getX();
                final float yDistance = e1.getY() - e2.getY();
            Log.w(TAG, "xDistance: " + xDistance);
            Log.w(TAG, "yDistance: " + yDistance);
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
            playerPoint.x = playerPoint.x - 20;
            moves++;
        }


        protected void onSwipeRight(){
            playerPoint.x = playerPoint.x + 20;
            moves++;
        }


        protected void onSwipeUp(){
            playerPoint.y = playerPoint.y+ 20;
            moves++;

        }

        protected void onSwipeDown(){
            playerPoint.y = playerPoint.y - 20;
            moves++;
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