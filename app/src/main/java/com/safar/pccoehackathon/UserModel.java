package com.safar.pccoehackathon;

import com.google.firebase.firestore.GeoPoint;

public class UserModel {
    private String id, name, messname, ownerphone, upi, email, location, totalCustomer, remainingPayment, monthlyPrice, geohash;

    double avg_review;

    int customer_count;

    double lat, lang;

    GeoPoint geoPointLocation;
    public UserModel(String name, String messname, String ownerphone) {
        this.name = name;
        this.messname = messname;
        this.ownerphone = ownerphone;
        this.location = "NA";
    }

    public String getTotalCustomer() {
        return totalCustomer;
    }

    public void setTotalCustomer(String totalCustomer) {
        this.totalCustomer = totalCustomer;
    }

    public String getRemainingPayment() {
        return remainingPayment;
    }

    public void setRemainingPayment(String remainingPayment) {
        this.remainingPayment = remainingPayment;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAvg_review() {
        return avg_review;
    }

    public void setAvg_review(double avg_review) {
        this.avg_review = avg_review;
    }

    public int getCustomer_count() {
        return customer_count;
    }

    public void setCustomer_count(int customer_count) {
        this.customer_count = customer_count;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public GeoPoint getGeoPointLocation() {
        return geoPointLocation;
    }

    public void setGeoPointLocation(GeoPoint geoPointLocation) {
        this.geoPointLocation = geoPointLocation;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public UserModel(String id, String name, String messname, String ownerphone, String upi, String email, String monthlyPrice, String location, double lat, double lang, GeoPoint geoPointLocation, String geohash) {
        this.name = name;
        this.id = id;
        this.messname = messname;
        this.ownerphone = ownerphone;
        this.upi = upi;
        this.email = email;
        this.totalCustomer = "0";
        this.remainingPayment = "0";
        this.monthlyPrice = monthlyPrice;
        this.location = location;
        this.geoPointLocation = geoPointLocation;
        this.lat = lat;
        this.lang = lang;
        this.geohash = geohash;
        this.avg_review = 0.0;
        this.customer_count = 0;
    }

    public String getLocation() {
        return location;
    }


    public String getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(String monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessname() {
        return messname;
    }

    public void setMessname(String messname) {
        this.messname = messname;
    }

    public String getOwnerphone() {
        return ownerphone;
    }

    public void setOwnerphone(String ownerphone) {
        this.ownerphone = ownerphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }
}

