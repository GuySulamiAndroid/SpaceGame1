package com.example.space_game;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;


public class ResultsActivity extends FragmentActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        showResultFragment();
        showMapFragment();
    }

    private void showResultFragment(){
        ResultFragment resFragment = new ResultFragment(this);
        FragmentManager fManager = getSupportFragmentManager();
        fManager.beginTransaction().replace(R.id.result_layout, resFragment, resFragment.getTag()).commit();
    }

    private void showMapFragment(){
        mapFragment = new MapFragment();
        FragmentManager fManager = getSupportFragmentManager();
        fManager.beginTransaction().replace(R.id.map_layout, mapFragment, mapFragment.getTag()).commit();
    }

    public void updateMap(Result result){
        mapFragment.updateMarker(result.getPlayerName(),result.getLat(),result.getLng());
    }
}
