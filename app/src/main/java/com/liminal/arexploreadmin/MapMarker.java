package com.liminal.arexploreadmin;

public class MapMarker {
    private double latitude;
    private double longitude;
    private String highscorer;
    private long highscore;

    MapMarker(double latitude, double longitude, String highscorer, long highscore) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.highscorer = highscorer;
        this.highscore = highscore;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getHighscorer() {
        return highscorer;
    }

    public void setHighscorer(String highscorer) {
        this.highscorer = highscorer;
    }

    public long getHighscore() {
        return highscore;
    }

    public void setHighscore(long highscore) {
        this.highscore = highscore;
    }
}
