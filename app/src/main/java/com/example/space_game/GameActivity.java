package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.LoginFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    //Layouts
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;

    //Screen sizes
    private int laneWidth, screenHeight, screenWidth;
    final private int NUM_OF_LANES = 3;
    final private float SCREEN_ARRANGE = 310;

    //Game participants objects and their size
    private ImageView spaceship, meteor;
    final private int OBJECT_HEIGHT = 100;
    final private int OBJECT_WIDTH = 100;
    private ArrayList<ImageView> meteors = new ArrayList<>();

    //life
    final private int MAX_LIFE = 3;
    private int numOfLife;
    private ArrayList<ImageView> lifeArr = new ArrayList<>();
    private ImageView life1, life2, life3;
    private boolean isGameOver;

    //difficulty
    private String currentLevel;
    private int speedLevel;
    private int meteorCreation;

    // Score
    private TextView scoreLabel;
    private int score;

    // Movement buttons
    private Button left;
    private Button right;

    //Handlers
    final private Handler mainHandler = new Handler();
    final private Handler posHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        initVars();
        initSpaceship();
        moveSpaceship();
        startGame();
        changeMeteorPosition();
    }

    private void startGame(){
        Runnable gameOn = new Runnable() {
            @Override
            public void run() {
                startGame();
                initMeteor();
                isGameOver = isGameOver();
                if(isGameOver){

                }else{
                    score++;
                    scoreLabel.setText("Score: " + score);
                    for (ImageView meteor : meteors) {
                        if (isCrashed(meteor)) {
                            Log.d("Crashed", "yesssss!");
                            reduceOneLife();
                            removeMeteorAfterCrash(meteor);
                            break;
                        }
                    }
                }
            }
        };
        mainHandler.postDelayed(gameOn, meteorCreation);
    }

    private void initVars(){
        relativeLayout = findViewById(R.id.relativeLayout);
        linearLayout = findViewById(R.id.linearLayout);
        setScreenDimensions();
        setGameSpeed();
        numOfLife = MAX_LIFE;
        initLife();
        left = findViewById(R.id.leftButton);
        right = findViewById(R.id.rightButton);
        scoreLabel = findViewById(R.id.score_LBL);
        score = 0;
    }

    private void initSpaceship(){
        spaceship = new ImageView(this);
        spaceship.setImageResource(R.drawable.ic_spaceship);
        RelativeLayout.LayoutParams spaceshipParams = new RelativeLayout.LayoutParams(OBJECT_WIDTH, OBJECT_HEIGHT);
        spaceshipParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        spaceshipParams.addRule(RelativeLayout.ABOVE, left.getId()); //can use right.getId() as well
        spaceship.setLayoutParams(spaceshipParams);
        relativeLayout.addView(spaceship);
    }

    private void initMeteor(){
        meteor = new ImageView(this);
        meteor.setImageResource(R.drawable.ic_meteor);
        int randPosition = (int)(Math.random() * NUM_OF_LANES);
        RelativeLayout.LayoutParams meteorParams = new RelativeLayout.LayoutParams(OBJECT_WIDTH, OBJECT_HEIGHT);
        initMeteorPosition(randPosition, meteorParams);
        relativeLayout.addView(meteor);
        meteors.add(meteor);
    }

    private void initMeteorPosition(int randPosition, RelativeLayout.LayoutParams params){
        meteor.setX(SCREEN_ARRANGE);
//        meteor.setLayoutParams(params);
        switch(randPosition){
            case 0:
                meteor.setX(meteor.getX()- laneWidth);
                break;
            case 1:
                meteor.setX(meteor.getX());
                break;
            case 2:
                meteor.setX(meteor.getX() + laneWidth);
                break;
        }
        meteor.setY(0);
        meteor.setLayoutParams(params);
    }

    private void changeMeteorPosition() {
        Runnable posRun = new Runnable() {
            @Override
            public void run() {
                changeMeteorPosition();
                for (ImageView meteor: meteors){
                    meteor.setY(meteor.getY() + 10);
                }
            }
        };
        posHandler.postDelayed(posRun, speedLevel);
    }

    private void initLife(){
        life1 = findViewById(R.id.life1);
        lifeArr.add(life1);
        life2 = findViewById(R.id.life2);
        lifeArr.add(life2);
        life3 = findViewById(R.id.life3);
        lifeArr.add(life3);
    }

    private void reduceOneLife(){
        numOfLife--;
        if(!lifeArr.isEmpty()) {
            lifeArr.get(lifeArr.size()-1).setVisibility(View.INVISIBLE);
            lifeArr.remove(lifeArr.size() - 1);
        }
    }

    private void removeMeteorAfterCrash(ImageView meteor){
        relativeLayout.removeView(meteor);
        meteors.remove(meteor);
    }

    private boolean isCrashed(ImageView meteor){
//        Rect R1 = new Rect();
//        spaceship.getHitRect(R1);
//        Rect R2 = new Rect();
//        meteor.getHitRect(R2);
        Log.d("IsCrashedX", "is:" + getCrashedX(meteor));
        Log.d("IsCrashedY", "is:" + getCrashedY(meteor));
//        return Rect.intersects(R1, R2);
        return getCrashedX(meteor) && getCrashedY(meteor);
    }

    private boolean getCrashedY(ImageView meteor){
        return spaceship.getY() - 10 < (meteor.getY() + meteor.getHeight());
    }

    private boolean getCrashedX(ImageView meteor){
        return spaceship.getX() == meteor.getX();
    }

    private boolean isGameOver(){
        return numOfLife == 0;
    }

    private void setGameSpeed(){
        currentLevel = getIntent().getStringExtra("CURRENT_LEVEL");
        switch (currentLevel){
            case "Low":
                speedLevel = 60;
                meteorCreation = 1700;
                break;
            case "Medium":
                speedLevel = 40;
                meteorCreation = 1200;
                break;
            case "High:":
                speedLevel = 20;
                meteorCreation = 1200;
                break;
        }
    }

    private void moveSpaceship(){
        moveLeft();
        moveRight();
    }

    private void moveLeft(){
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float newPosition = spaceship.getX() - laneWidth;
                if(newPosition < 0){
                    newPosition = spaceship.getX();
                }
                spaceship.setX(newPosition);
            }
        });
    }

    private void moveRight(){
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float newPosition = spaceship.getX() + laneWidth;
                if(newPosition > screenWidth){
                    newPosition = spaceship.getX();
                }
                spaceship.setX(newPosition);
            }
        });
    }

    private void setScreenDimensions(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        laneWidth = screenWidth/NUM_OF_LANES;
    }

    private void goToFinishActivity() {
        Intent intent = new Intent(GameActivity.this, FinishGameActivity.class);
        intent.putExtra("FINAL_SCORE", score);
        startActivity(intent);
    }
}
