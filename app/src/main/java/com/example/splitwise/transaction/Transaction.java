package com.example.splitwise.transaction;

import java.util.Date;
import java.util.List;

public class Transaction {

    private String groupID;
    private List<UserTransact> exchanges;
    private String description;
    private double totalAmount;
    private String tag;
    private Date date;

    public Transaction(){

    }


    public Transaction(String groupID, List<UserTransact> exchanges, String description, double totalAmount, String tag, Date date) {
        this.groupID = groupID;
        this.exchanges = exchanges;
        this.description = description;
        this.totalAmount = totalAmount;
        this.tag = tag;
        this.date = date;
    }


    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public List<UserTransact> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<UserTransact> exchanges) {
        this.exchanges = exchanges;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
