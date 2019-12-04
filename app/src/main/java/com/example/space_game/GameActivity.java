package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    //Layout
    private RelativeLayout relativeLayout;

    //Sounds
    private SoundPlayer sound;

    //vibration
    final private int VIBRATE_TIME = 500;

    //Screen sizes
    private int laneWidth, screenWidth;
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

    //difficulty
    final private String LOW_LEVEL = "Low";
    final private String MED_LEVEL= "Medium";
    final private String HIGH_LEVEL = "High";
    private String currentLevel;
    final private int LOW_SPEED = 40;
    final private int MED_SPEED = 25;
    final private int HIGH_SPEED = 10;
    private int speedLevel;
    final private int SLOW_LAUNCH = 1450;
    final private int MED_LAUNCH = 900;
    final private int FAST_LAUNCH = 380;
    private int meteorCreation;

    // Score
    private TextView scoreLabel;
    private int score;

    // Movement buttons
    private Button left;
    private Button right;

    private boolean isPlayed;

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
        sound = new SoundPlayer(this);
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
                score++;
                scoreLabel.setText("Score: " + score);
            }
        };
        if(isPlayed){
            mainHandler.postDelayed(gameOn, meteorCreation);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isPlayed) {
            isPlayed = true;
            startGame();
            changeMeteorPosition();
        }
    }

    private void initVars(){
        relativeLayout = findViewById(R.id.relativeLayout);
        setScreenDimensions();
        setGameSpeed();
        isPlayed = true;
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
                    if(isMeteorOutOfScreen(meteor)) {
                            removeMeteor(meteor);
                            break;
                    } else if (isCrashed(meteor)) {
                        sound.playHitSound();
                        vibrate();
                        reduceOneLife();
                        removeMeteor(meteor);
                        isDead();
                        break;
                    }
                }
            }
        };
        if(isPlayed) {
            posHandler.postDelayed(posRun, speedLevel);
        }
    }

    private void initLife(){
        life1 = findViewById(R.id.life1);
        lifeArr.add(life1);
        life2 = findViewById(R.id.life2);
        lifeArr.add(life2);
        life3 = findViewById(R.id.life3);
        lifeArr.add(life3);
    }

    private void reduceOneLife() {
        numOfLife--;
        if (!lifeArr.isEmpty()) {
            lifeArr.get(lifeArr.size() - 1).setVisibility(View.INVISIBLE);
            lifeArr.remove(lifeArr.size() - 1);
        }
    }

    private void isDead(){
        if(isGameOver()){
            mainHandler.removeCallbacksAndMessages(null);
            posHandler.removeCallbacksAndMessages(null);
            goToFinishActivity();
        }
    }

    private boolean isMeteorOutOfScreen(ImageView meteor){
        return meteor.getY() > spaceship.getY() + spaceship.getHeight();
    }

    private void removeMeteor(ImageView meteor){
        meteors.remove(meteor);
        relativeLayout.removeView(meteor);
    }

    private boolean isCrashed(ImageView meteor){
        return getCrashedX(meteor) && getCrashedY(meteor);
    }

    private boolean getCrashedY(ImageView meteor){
        return spaceship.getY() <= (meteor.getY() + meteor.getHeight());
    }

    private boolean getCrashedX(ImageView meteor){
        return spaceship.getX() == meteor.getX();
    }

    private boolean isGameOver(){
        return numOfLife == 0;
    }

    private void setGameSpeed(){
        currentLevel = getIntent().getStringExtra("CURRENT_LEVEL");
        if(currentLevel != null) {
            switch (currentLevel) {
                case LOW_LEVEL:
                    speedLevel = LOW_SPEED;
                    meteorCreation = SLOW_LAUNCH;
                    break;
                case MED_LEVEL:
                    speedLevel = MED_SPEED;
                    meteorCreation = MED_LAUNCH;
                    break;
                case HIGH_LEVEL:
                    speedLevel = HIGH_SPEED;
                    meteorCreation = FAST_LAUNCH;
                    break;
            }
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
        screenWidth = displayMetrics.widthPixels;
        laneWidth = screenWidth/NUM_OF_LANES;
    }

    private void vibrate(){
        Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        if(v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(VIBRATE_TIME);
            }
        }
    }
    private void goToFinishActivity() {
        Intent intent = new Intent(GameActivity.this, FinishGameActivity.class);
        intent.putExtra("FINAL_SCORE", score);
        startActivity(intent);
    }
}
