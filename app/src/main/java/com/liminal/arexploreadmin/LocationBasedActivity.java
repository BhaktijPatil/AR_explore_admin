package com.liminal.arexploreadmin;

class LocationBasedActivity {
    long markerID;
    String activityID;
    String activityName;
    double latitude;
    double longitude;

    LocationBasedActivity(long markerID, String activityID, String activityName, double latitude, double longitude){
        this.markerID = markerID;
        this.activityName = activityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.activityID = activityID;
    }

    LocationBasedActivity(String activityID, String activityName) {
        this.activityID = activityID;
        this.activityName = activityName;
    }
}
