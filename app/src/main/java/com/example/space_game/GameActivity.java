package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    //Layout
    private RelativeLayout relativeLayout;

    //Sensors
    private SensorManager sensorManager;
    private Sensor sensor;

    //Sounds
    private SoundPlayer sound;

    //vibration
    final private int VIBRATE_TIME = 500;

    //Screen sizes
    private int laneWidth, screenWidth, screenHeight;
    final private int NUM_OF_LANES = 5;

    //Game participants objects and their size
    private ImageView spaceship, meteor, star;
    final private int OBJECT_HEIGHT = 100;
    final private int OBJECT_WIDTH = 100;
    private ArrayList<ImageView> meteors = new ArrayList<>();
    private ArrayList<ImageView> stars = new ArrayList<>();

    //life
    final private int MAX_LIFE = 3;
    private int numOfLife;
    private ArrayList<ImageView> lifeArr = new ArrayList<>();
    private ImageView life1, life2, life3;

    //difficulty
    final private String SLOW_LEVEL = "Slow";
    final private String FAST_LEVEL= "Fast";
    final private String SENSOR = "Sensor";
    private String currentMode;
    final private int LOW_SPEED = 40;
    final private int HIGH_SPEED = 25;
    private int speedLevel;
    final private int SLOW_LAUNCH = 1450;
    final private int FAST_LAUNCH = 900;
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
        handleImagesEvents();
    }

    private void startGame(){
        Runnable gameOn = new Runnable() {
            @Override
            public void run() {
                startGame();
                initMeteor();
                initStar();
                increaseScore(1);
            }
        };
        if(isPlayed){
            mainHandler.postDelayed(gameOn, meteorCreation);
        }
    }

    private void handleImagesEvents() {
        Runnable posRun = new Runnable() {
            @Override
            public void run() {
                handleImagesEvents();
                handleMeteors();
                handleStars();
            }
        };
        if(isPlayed) {
            posHandler.postDelayed(posRun, speedLevel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isPlayed) {
            isPlayed = true;
            startGame();
            handleImagesEvents();
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlayed = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        int currentSpeed = speedLevel;
        if(currentMode.equals(SENSOR)){
            relativeLayout.removeView(left);
            relativeLayout.removeView(right);
            speedLevel = HIGH_SPEED;
            meteorCreation = FAST_LAUNCH;
            float x = event.values[0];
            float y = event.values[1];
            if(x < -5){
                Log.d("x when right", "is:" + x);
                handleRightMovement();
            }
            if(x > 5){
                Log.d("x when left", "is:" + x);
                handleLeftMovement();
            }
//        if(y < 0){
//            speedLevel-=10;
//        }
//        if(y > 0){
//            speedLevel+=10;
//        }
//        if (y > (-5) && y < (5)) {
//            speedLevel= currentSpeed;
//        }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void initVars(){
        relativeLayout = findViewById(R.id.relativeLayout);
        setScreenDimensions();
        setGameMode();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        isPlayed = true;
        numOfLife = MAX_LIFE;
        initLife();
        left = findViewById(R.id.leftButton);
        right = findViewById(R.id.rightButton);
        scoreLabel = findViewById(R.id.score_LBL);
        score = 0;
    }

    private void handleMeteors(){
        for (ImageView meteor: meteors){
            meteor.setY(meteor.getY() + 10);
            if(isImageOutOfScreen(meteor)) {
                removeImage(meteors, meteor);
                break;
            } else if (isCollided(meteor)) {
                sound.playHitSound();
                vibrate();
                reduceOneLife();
                removeImage(meteors, meteor);
                isDead();
                break;
            }
        }
    }

    private void handleStars(){
        for(ImageView star: stars){
            star.setY(star.getY() + 10);
            if(isImageOutOfScreen(star)){
                removeImage(stars, star);
                break;
            }else if(isCollided(star)){
                sound.playGainSound();
                increaseScore(3);
                removeImage(stars,star);
                break;
            }
        }
    }

    private void increaseScore(int points){
        score+= points;
        scoreLabel.setText("Score: " + score);
    }

    private void initSpaceship(){
        spaceship = new ImageView(this);
        spaceship.setImageResource(R.drawable.ic_spaceship);
        RelativeLayout.LayoutParams spaceshipParams = new RelativeLayout.LayoutParams(OBJECT_WIDTH, OBJECT_HEIGHT);
        spaceship.setX(screenWidth/2 - 52);
        spaceship.setY(screenHeight - 200);
        spaceship.setLayoutParams(spaceshipParams);
        relativeLayout.addView(spaceship);
    }

    private void initMeteor(){
        meteor = new ImageView(this);
        meteor.setImageResource(R.drawable.ic_meteor);
        int randPosition = (int)(Math.random() * NUM_OF_LANES);
        RelativeLayout.LayoutParams meteorParams = new RelativeLayout.LayoutParams(OBJECT_WIDTH, OBJECT_HEIGHT);
        initImagePosition(randPosition, meteorParams, meteor);
        relativeLayout.addView(meteor);
        meteors.add(meteor);
    }

    private void initStar(){
        star = new ImageView(this);
        star.setImageResource(R.drawable.ic_star);
        int randPosition = (int)(Math.random() * NUM_OF_LANES);
        RelativeLayout.LayoutParams meteorParams = new RelativeLayout.LayoutParams(OBJECT_WIDTH, OBJECT_HEIGHT);
        initImagePosition(randPosition, meteorParams, star);
        if(star.getX() != meteor.getX()){
            relativeLayout.addView(star);
            stars.add(star);
        }
    }

    private void initImagePosition(int randPosition, RelativeLayout.LayoutParams params, ImageView image){
        image.setX(randPosition*laneWidth + 20);
        image.setY(0);
        image.setLayoutParams(params);
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

    private boolean isImageOutOfScreen(ImageView image){
        return image.getY() > spaceship.getY() + spaceship.getHeight();
    }

    private void removeImage(ArrayList<ImageView> images, ImageView image){
        images.remove(image);
        relativeLayout.removeView(image);
    }

    private boolean isCollided(ImageView image){
        return getCollisionX(image) && getCollisionY(image);
    }

    private boolean getCollisionY(ImageView meteor){
        return spaceship.getY() <= (meteor.getY() + meteor.getHeight());
    }

    private boolean getCollisionX(ImageView meteor){
        return spaceship.getX() == meteor.getX();
    }

    private boolean isGameOver(){
        return numOfLife == 0;
    }

    private void setGameMode(){
        currentMode = getIntent().getStringExtra("CURRENT_LEVEL");
        if(currentMode != null) {
            switch (currentMode) {
                case SLOW_LEVEL:
                    speedLevel = LOW_SPEED;
                    meteorCreation = SLOW_LAUNCH;
                    break;
                case FAST_LEVEL:
                    speedLevel = HIGH_SPEED;
                    meteorCreation = FAST_LAUNCH;
                    break;
            }
        }
    }

    private void moveSpaceship(){
        moveLeftByButton();
        moveRightByButton();
    }

    private void moveLeftByButton(){
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLeftMovement();
            }
        });
    }

    private void handleLeftMovement(){
        float newPosition = spaceship.getX() - laneWidth;
        if(newPosition < 0){
            newPosition = spaceship.getX();
        }
        spaceship.setX(newPosition);
    }

    private void moveRightByButton(){
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRightMovement();
            }
        });
    }

    private void handleRightMovement(){
        float newPosition = spaceship.getX() + laneWidth;
        if(newPosition > screenWidth){
            newPosition = spaceship.getX();
        }
        spaceship.setX(newPosition);
    }

    private void setScreenDimensions(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
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
        finish();
    }
}
