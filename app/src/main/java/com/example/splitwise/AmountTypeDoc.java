package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class AmountTypeDoc {


    private String name;
    private double amount;

    public AmountTypeDoc(){}

    public AmountTypeDoc( String name, double amount) {

        this.name = name;
        this.amount = amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }



    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }


}
