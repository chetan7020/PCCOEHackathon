package com.safar.pccoehackathon;

import com.google.firebase.firestore.GeoPoint;

public class LocationModel {
    double lat, lang;
    GeoPoint geoPointLocation;

    public LocationModel(double lat, double lang, GeoPoint geoPointLocation) {
        this.lat = lat;
        this.lang = lang;
        this.geoPointLocation = geoPointLocation;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
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
}
