package com.safar.pccoehackathon;

import com.google.firebase.firestore.GeoPoint;

public class UserModel {
    private String id, name, messname, ownerphone, upi, email, location, totalCustomer, remainingPayment, monthlyPrice;

    GeoPoint geo_pointLocation;

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

    public UserModel(String id, String name, String messname, String ownerphone, String upi, String email, String monthlyPrice, String location, GeoPoint geo_pointLocation) {
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
        this.geo_pointLocation = geo_pointLocation;
    }

    public GeoPoint getGeo_pointLocation() {
        return geo_pointLocation;
    }

    public void setGeo_pointLocation(GeoPoint geo_pointLocation) {
        this.geo_pointLocation = geo_pointLocation;
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

