package com.example.google_maps_serko;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class WaypointsManager {
    private List<LatLng> waypoints;

    public WaypointsManager() {
        waypoints = new ArrayList<>();
    }

    public void addWaypoint(double latitude, double longitude) {
        LatLng waypoint = new LatLng(latitude, longitude);
        waypoints.add(waypoint);
    }

    public void removeWaypoint(int index) {
        if (index >= 0 && index < waypoints.size()) {
            waypoints.remove(index);
        }
    }

    public void clearWaypoints() {
        waypoints.clear();
    }

    public List<LatLng> getWaypoints() {
        return waypoints;
    }
}
