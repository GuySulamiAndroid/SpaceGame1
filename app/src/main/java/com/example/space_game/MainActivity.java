package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button lowLevel;
    private Button mediumLevel;
    private Button highLevel;
    private String currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        lowLevel = findViewById(R.id.lowLevel);
        mediumLevel = findViewById(R.id.mediumLevel);
        highLevel = findViewById(R.id.highLevel);

        lowLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLevel = lowLevel.getText().toString();
                goToGameActivity();
            }
        });

        mediumLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLevel = mediumLevel.getText().toString();
                goToGameActivity();
            }
        });

        highLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLevel = highLevel.getText().toString();
                goToGameActivity();
            }
        });
    }

    private void goToGameActivity() {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("CURRENT_LEVEL", currentLevel);
        startActivity(intent);
    }
}
