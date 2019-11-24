package com.example.splitwise;

import java.util.Date;

public class ActivityTypeDoc {

    private String groupId;
    private String description;
    private double amount;
    private String tag;
    private Date date;

    public ActivityTypeDoc(){}

    public ActivityTypeDoc(String groupId, String description, double amount, String tag, Date date) {
        if(groupId==null)
        {
            this.groupId="Non Group";
        }
        else
        {
            this.groupId= groupId;
        }
        this.description = description;
        this.amount = amount;
        this.tag = tag;
        this.date= date;
    }


    public String getGroupId() {
        return groupId;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getTag() {
        return tag;
    }
}

