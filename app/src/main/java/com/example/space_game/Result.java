package com.example.space_game;

public class Result implements Comparable<Result>{

    private String playerName;
    private int score;
    private double lat = 0;
    private double lng = 0;

    public Result(String playerName, int score, double lat, double lng){
        this.playerName = playerName;
        this.score = score;
        setLat(lat);
        setLng(lng);
    }

    public String getPlayerName(){
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public int compareTo(Result result) {
        return result.getScore() > score ? 1 : result.getScore() < score ? -1: 0;
    }

    @Override
    public String toString() {
        return playerName + " - " + score;
    }
}

