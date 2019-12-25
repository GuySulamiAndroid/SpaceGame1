package com.example.space_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button slow;
    private Button fast;
    private Button sensor;
    private String currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        slow = findViewById(R.id.lowLevel);
        fast = findViewById(R.id.mediumLevel);
        sensor = findViewById(R.id.highLevel);

        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMode = slow.getText().toString();
                goToGameActivity();
            }
        });

        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMode = fast.getText().toString();
                goToGameActivity();
            }
        });

        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMode = sensor.getText().toString();
                goToGameActivity();
            }
        });
    }

    private void goToGameActivity() {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("CURRENT_LEVEL", currentMode);
        startActivity(intent);
    }
}
