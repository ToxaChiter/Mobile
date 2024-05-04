package com.example.google_maps_maptypes_serko;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.PopupMenu;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button menuButton;
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    Location myLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermision();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(menuButton);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }


    private void setMapType(int mapType) {
        if (mMap != null) {
            mMap.setMapType(mapType);
        } else {
            Toast.makeText(this, "Map is not available", Toast.LENGTH_SHORT).show();
        }
    }
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                myLocation=location;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                mMap.animateCamera(cameraUpdate);
            }
        });


    }


    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.menu_main); // Inflate your menu resource here
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item click here
                int itemId = item.getItemId();
                if (itemId == R.id.menu_satellite) {
                    setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                } else if (itemId == R.id.menu_terrain) {
                    setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                } else if (itemId == R.id.menu_none) {
                    setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                } else if (itemId == R.id.menu_normal) {
                    setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                } else if (itemId == R.id.menu_hybrid) {
                    setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();

        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        } else {
            Toast.makeText(this, "Map is not available", Toast.LENGTH_SHORT).show();
        }
    }
}
