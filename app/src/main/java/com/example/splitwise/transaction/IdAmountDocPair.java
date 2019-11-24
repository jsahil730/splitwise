package com.example.splitwise.transaction;

import com.example.splitwise.AmountTypeDoc;

public class IdAmountDocPair {

    private String id;
    private AmountTypeDoc amountTypeDoc;

    public IdAmountDocPair(){}

    public IdAmountDocPair(String id, AmountTypeDoc amountTypeDoc)
    {
        this.id =id;
        this.amountTypeDoc=amountTypeDoc;
    }

    public IdAmountDocPair(String id, String name, double amount) {
        this.id = id;
        amountTypeDoc = new AmountTypeDoc(name,amount);
    }

    public String getId() {
        return id;
    }

    public AmountTypeDoc getAmountTypeDoc() {
        return amountTypeDoc;
    }

    public void setAmount(double amount)
    {
        amountTypeDoc.setAmount(amount);
    }

    public double getAmount()
    {
        return  amountTypeDoc.getAmount();

    }

    public String getName()
    {
        return amountTypeDoc.getName();
    }


}
