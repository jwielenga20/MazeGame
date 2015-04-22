// GameStarter.java
// MainActivity displays the MainGameFragment
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends Activity
{
   Button monsterButton;
   Button mazeStartButton;
   @Override
   public void onCreate(final Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      monsterButton = (Button)findViewById(R.id.monsterButton);
       monsterButton.setOnClickListener(new OnClickListener()
       {   public void onClick(View v)
           {
               Intent intent = new Intent(MainActivity.this, MonsterScreen.class);
               startActivity(intent);

           }
       });
       mazeStartButton = (Button)findViewById(R.id.mazeStartButton);
       mazeStartButton.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, MazeStartScreen.class);
               startActivity(intent);
           }
       });
       Log.w("Main Activity", "create");
   }
}

