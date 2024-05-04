package com.example.google_maps_serko;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class DirectionManager {

    private List<String> directions;
    private LatLng currentLocation;
    private List<LatLng> waypoints;
    private int currentWaypointIndex;

    public DirectionManager(List<String> directions, List<LatLng> waypoints) {
        this.directions = directions;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
    }

    public String getCurrentDirection(LatLng currentLocation) {
        this.currentLocation = currentLocation;

        LatLng nextWaypoint = getNextWaypoint();

        String currentDirection = calculateCurrentDirection(nextWaypoint);
        return currentDirection;
    }

    private LatLng getNextWaypoint() {
        if (currentWaypointIndex < waypoints.size()) {
            return waypoints.get(currentWaypointIndex);
        } else {
            return null;
        }
    }

    private String calculateCurrentDirection(LatLng nextWaypoint) {
        if (currentLocation != null && nextWaypoint != null) {
            double bearing = calculateBearing(currentLocation, nextWaypoint);
            double direction = calculateDirection(bearing);
            return findClosestDirection(direction);
        } else {
            return "No current location or next waypoint available";
        }
    }

    private double calculateBearing(LatLng start, LatLng end) {
        Location locationA = new Location("point A");
        locationA.setLatitude(start.latitude);
        locationA.setLongitude(start.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(end.latitude);
        locationB.setLongitude(end.longitude);
        return locationA.bearingTo(locationB);
    }

    private double calculateDirection(double bearing) {
        double sector = 360.0 / directions.size();
        double direction = bearing + (sector / 2);
        if (direction < 0) {
            direction += 360;
        }
        return direction;
    }

    private String findClosestDirection(double direction) {

        int index = (int) Math.round(direction / (360.0 / directions.size())) % directions.size();
        return directions.get(index);
    }

    public void updateCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;

        if (isWaypointReached()) {
            currentWaypointIndex++;
        }
    }

    private boolean isWaypointReached() {

        LatLng nextWaypoint = getNextWaypoint();
        if (nextWaypoint != null && currentLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                    nextWaypoint.latitude, nextWaypoint.longitude, results);
            return results[0] < 10;
        } else {
            return false;
        }
    }
}
