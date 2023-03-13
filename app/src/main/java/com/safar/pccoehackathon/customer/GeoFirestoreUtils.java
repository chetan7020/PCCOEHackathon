package com.safar.pccoehackathon.customer;

import com.google.firebase.firestore.GeoPoint;

public class GeoFirestoreUtils {
    public static GeoPoint getGeoPointAtLocation(GeoPoint center, double radius) {
        double distance = radius / 6371.0;
        double lat1 = Math.toRadians(center.getLatitude());
        double lng1 = Math.toRadians(center.getLongitude());
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance) +
                Math.cos(lat1) * Math.sin(distance) * Math.cos(0));
        double lng2 = lng1 + Math.atan2(Math.sin(0) * Math.sin(distance) * Math.cos(lat1),
                Math.cos(distance) - Math.sin(lat1) * Math.sin(lat2));
        return new GeoPoint(Math.toDegrees(lat2), Math.toDegrees(lng2));
    }
}
