package com.liminal.arexploreadmin;

public class LocationBasedActivity {
    public long markerID;
    public String activityID;
    public String activityName;
    public double latitude;
    public double longitude;

    LocationBasedActivity(long markerID, String activityID, String activityName, double latitude, double longitude){
        this.markerID = markerID;
        this.activityName = activityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activityID = activityID;
    }

    public LocationBasedActivity(String activityID, String activityName) {
        this.activityID = activityID;
        this.activityName = activityName;
    }
}
