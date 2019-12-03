package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishGameActivity extends AppCompatActivity {

    private int finalScore, highScore;
    private TextView highScoreLabel, finalScoreLBL;
    private SharedPreferences settings;
    private Button playAgainBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        initVars();
        displayFinalScore();
        saveHighestScore();
        displayHighestScore();
        playAgain();
    }

    private void initVars(){
        highScoreLabel = findViewById(R.id.high_score_LBL);
        finalScoreLBL = findViewById(R.id.final_score_LBL);
        playAgainBTN = findViewById(R.id.play_again_BTN);
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
    }

    private void playAgain(){
        playAgainBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeBackToMain();
            }
        });
    }

    private void displayFinalScore(){
        finalScore = getIntent().getIntExtra("FINAL_SCORE", 0);
        finalScoreLBL.setText("Your Score: " + finalScore);
    }

    private void saveHighestScore(){
        if (finalScore > highScore){
            highScore = finalScore;
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }

    private void displayHighestScore(){
        highScoreLabel.setText("High Score: " + highScore);
    }

    private void comeBackToMain(){
        Intent intent = new Intent(FinishGameActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
