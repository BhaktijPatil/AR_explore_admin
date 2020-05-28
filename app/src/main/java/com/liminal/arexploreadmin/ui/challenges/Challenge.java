package com.liminal.arexploreadmin.ui.challenges;

public class Challenge {
    String activityID;
    long challengePosition;
    String description;
    long rewardPoints;
    String rewardType;
    String stat;
    long target;
    long timestampStart;
    long timestampEnd;

    Challenge(String activityID, long challengePosition, String description,
              long rewardPoints, String rewardType, String stat, long target, long timestampStart,
              long timestampEnd){
        this.activityID = activityID;
        this.challengePosition = challengePosition;
        this.description = description;
        this.rewardPoints = rewardPoints;
        this.rewardType = rewardType;
        this.stat = stat;
        this.target = target;
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public long getChallengePosition() {
        return challengePosition;
    }

    public void setChallengePosition(long challengePosition) {
        this.challengePosition = challengePosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(long rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    public long getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(long timestampStart) {
        this.timestampStart = timestampStart;
    }

    public long getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(long timestampEnd) {
        this.timestampEnd = timestampEnd;
    }
}
