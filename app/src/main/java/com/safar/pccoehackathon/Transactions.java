package com.safar.pccoehackathon;

public class Transactions {

    String date;
    String amount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Transactions(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }
}
