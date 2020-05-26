package com.liminal.arexploreadmin;

class RewardDetails {
    public String rid;
    public String category;
    public String title;
    public String description;
    public long cost;
    public long quantity;

    RewardDetails(String rid, String category, String title, String description, long cost, long quantity){
        this.rid = rid;
        this.category = category;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.quantity = quantity;
    }
}

