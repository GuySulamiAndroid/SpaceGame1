package com.example.space_game;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collections;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class ResultFragment extends Fragment {

    private View view;
    private final String RESULTS_KEY = "HIGH_SCORES";
    private SharedPreferences resultPrefs;
    private RecyclerView list_LST_results;
    private ArrayList<Result> results;
    private Adapter_ResultModel adapter_resultModel;
    private Context context;
    private Result newResult;
    private double currentLat;
    private double currentLng;


    public ResultFragment(Context context) {
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_result, container, false);
        }
        list_LST_results = view.findViewById(R.id.list_LST_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list_LST_results.setLayoutManager(layoutManager);
        resultPrefs = context.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        createListOfResults();
        if(FinishGameActivity.playerName != null){
            if(!isResultExists()){
                setCurrentLocation();
            }
        }
        initList();
        return view;
    }

    private boolean isResultExists(){
        for(int i=0; i < results.size(); i++){
            if(results.get(i).getPlayerName().equals(FinishGameActivity.playerName) && results.get(i).getScore() == FinishGameActivity.finalScore) {
                return true;
            }
        }
        return false;
    }

    private void createListOfResults() {
        results = getResultsFromSharedPrefs();
        if (results == null) {
            results = new ArrayList<>();
        }
        adapter_resultModel = new Adapter_ResultModel(getActivity(), results);
    }


    private void initList() {
        list_LST_results.setHasFixedSize(true);
        list_LST_results.setAdapter(adapter_resultModel);
        adapter_resultModel.setOnItemClickListener(new Adapter_ResultModel.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Result result) {
                openResult(result);
            }
        });
    }

    private void openResult(Result result) {
        ((ResultsActivity) context).updateMap(result);
    }

    private void saveResultsToSharedPrefs() {
        SharedPreferences.Editor editor;
        editor = resultPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(results);
        editor.putString(RESULTS_KEY, json);
        editor.commit();
    }

    private void updateResults() {
        final int MAX_RESULTS_SIZE = 10;
        if (results.size() < MAX_RESULTS_SIZE) {
            results.add(newResult);
            adapter_resultModel.updateList(results);
        } else {
            Result lastResult = results.get(results.size() - 1);
            if (newResult.compareTo(lastResult) < 0) {
                adapter_resultModel.removeAt(results.indexOf(lastResult));
                results.remove(lastResult);
                results.add(newResult);
                adapter_resultModel.updateList(results);
            }
        }
        Collections.sort(results);
    }

    private ArrayList<Result> getResultsFromSharedPrefs() {
        Gson gson = new Gson();
        String json = resultPrefs.getString(RESULTS_KEY, "");
        results = gson.fromJson(json, new TypeToken<ArrayList<Result>>() {
        }.getType());
        return results;
    }

    private void setCurrentLocation(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getActivity());
        requestPermission();
        if(ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLng = location.getLongitude();
                    currentLat = location.getLatitude();
                    newResult = new Result(FinishGameActivity.playerName, FinishGameActivity.finalScore, currentLat, currentLng);
                    updateResults();
                    saveResultsToSharedPrefs();
                }
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
