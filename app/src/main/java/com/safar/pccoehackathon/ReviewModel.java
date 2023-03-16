package com.safar.pccoehackathon;

public class ReviewModel {
    String text;
    double star;

    public ReviewModel(String text, double star) {
        this.text = text;
        this.star = star;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }
}
