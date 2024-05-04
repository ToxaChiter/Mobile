package com.example.google_maps_serko;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Route {
    private List<LatLng> waypoints;
    private  String routeName;

    public Route(List<LatLng> waypoints, String routeName) {
        this.waypoints = waypoints;
        this.routeName = routeName;
    }

    public List<LatLng> getWaypoints() {
        return waypoints;
    }


    public String getRouteName() {
        return routeName;
    }
    public void setRouteName(String name) {
         routeName = name;
    }


}
