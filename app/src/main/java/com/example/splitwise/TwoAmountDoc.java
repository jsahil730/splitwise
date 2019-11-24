package com.example.splitwise;

public class TwoAmountDoc {

    private String name;
    private double amount;
    private double sec_amount;

    public TwoAmountDoc()
    {

    }

    public TwoAmountDoc(String name, double amount, double sec_amount) {
        this.name = name;
        this.amount = amount;
        this.sec_amount = sec_amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSec_amount() {
        return sec_amount;
    }

    public void setSec_amount(double sec_amount) {
        this.sec_amount = sec_amount;
    }
}
