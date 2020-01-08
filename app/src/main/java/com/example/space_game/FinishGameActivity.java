package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FinishGameActivity extends AppCompatActivity {

    public static int finalScore;
    public static String playerName;
    private TextView finalScoreLBL;
    private EditText inputName;
    private Button playAgainBTN, showResultsBTN, saveBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        initVars();
        displayFinalScore();
        saveResult();
        viewTopTenResults();
        playAgain();
    }

    protected void onDestroy(){
        saveResult();
        super.onDestroy();
    }

    private void initVars(){
        finalScore = getIntent().getIntExtra("FINAL_SCORE", 0);
        finalScoreLBL = findViewById(R.id.final_score_LBL);
        inputName = findViewById(R.id.name_INPUT);
        saveBTN = findViewById(R.id.save_BTN);
        showResultsBTN = findViewById(R.id.show_results_BTN);
        playAgainBTN = findViewById(R.id.play_again_BTN);

    }

    private void saveResult() {
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = inputName.getText().toString();
                moveToResultsScreen();
            }
        });
    }

    private void viewTopTenResults(){
        showResultsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToResultsScreen();
            }
        });
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
        finalScoreLBL.setText("Your Score: " + finalScore);
    }

    private void comeBackToMain(){
        Intent intent = new Intent(FinishGameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToResultsScreen(){
        Intent intent = new Intent(FinishGameActivity.this, ResultsActivity.class);
        startActivity(intent);
        finish();
    }
}
