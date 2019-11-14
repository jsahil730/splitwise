package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class AmountTypeDoc {

    private String amountTypeId;
    private String name;
    private double amount;

    public AmountTypeDoc(){}

    public AmountTypeDoc(String amountTypeId, String name, double amount) {
        this.amountTypeId = amountTypeId;
        this.name = name;
        this.amount = amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Exclude
    public String getAmountTypeId() {
        return amountTypeId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }


}
