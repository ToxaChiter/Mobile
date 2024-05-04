package com.example.google_maps_serko;

import android.net.Uri;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomPlace implements Place {

    private String id;
    private String name;
    private String snippet;

    private LatLng latLng;

    public CustomPlace(String id, String name, String snippet,  LatLng latLng) {
        this.id = id;
        this.name = name;
        this.snippet = snippet;
        this.latLng = latLng;
    }

    @Override
    public String getId() {
        return id;
    }
    public String getSnippet() {
        return snippet;
    }

    @Override
    public List<Integer> getPlaceTypes() {
        return new ArrayList<>();
    }

    @Override
    public CharSequence getAddress() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public LatLngBounds getViewport() {
        return null;
    }

    @Override
    public Uri getWebsiteUri() {
        return null;
    }

    @Override
    public CharSequence getPhoneNumber() {
        return null;
    }

    @Override
    public float getRating() {
        return 0;
    }

    @Override
    public int getPriceLevel() {
        return 0;
    }

    @Override
    public CharSequence getAttributions() {
        return null;
    }

    @Override
    public Place freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
