package com.example.google_maps_serko;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.location.Address;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.places.Place;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import java.io.IOException;

import android.graphics.Color;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import android.location.Location;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private static final long INACTIVITY_DELAY_MS = 5000;
    //google map object
    private GoogleMap mMap;
    private List<Place> places;
    private List<com.example.google_maps_serko.Route> routes = new ArrayList<com.example.google_maps_serko.Route>();
    private RecyclerView recyclerViewPlaces;
    private  boolean isBeingServed = false;
    private PlaceListDialogFragment dialogFragment; // Declare dialogFragment here
    private RouteListDialogFragment routeListDialogFragment; // Declare dialogFragment here

    private GeoApiContext mGeoApiContext=null;

    private Timer inactivityTimer;
    //current and destination location objects
    Location myLocation = null;
    Location endlocation = null;
    private boolean isAddingRoute = false;
    LatLng placeMarker = null;


    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;
    private EditText origin_place;
    private EditText destination_place;

    private TextView directionsTextView;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private RouteAdapter routeAdapter;

    private List<CustomPlace> placeList;
    List<LatLng> waypoints;
    WaypointsManager waypointsManager;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    //polyline object
    private PolylineOptions polylineOptions;

    private List<Polyline> polylines = null;
    private List<PolylineOptions> customPolylines = null;

    public interface OnItemClickListener {
        void onItemClick(CustomPlace place);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        directionsTextView = findViewById(R.id.directionsTextView);
        waypointsManager = new WaypointsManager();

        inactivityTimer = new Timer();
        recyclerView = findViewById(R.id.places_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize place list and adapter
        placeList = new ArrayList<>();
        placeAdapter = new PlaceAdapter(placeList, new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomPlace place) {
                // Navigate to the selected place
                addMarkerWithInfo(place.getName(),place.getSnippet(),place.getLatLng());
                navigateToPlace(place);
            }
        });

        routeAdapter = new RouteAdapter(routes, new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.google_maps_serko.Route route) {
                displayRoutesOnMap(route.getWaypoints());
                Toast.makeText(MainActivity.this, "Display route"+route.getRouteName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(placeAdapter);

        findViewById(R.id.panelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceListDialog();
            }
        });
        findViewById(R.id.routeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRouteListDialog();
            }
        });


        //request location permission.
        requestPermision();


        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // new FetchDirectionsTask().execute();
    }
    private void showPlaceListDialog() {
        // Populate placeList with sample places (replace with your actual places)


        // Add more places as needed

        // Notify the adapter of data change
        placeAdapter.notifyDataSetChanged();

        // Create and show the dialog fragment

        dialogFragment = new PlaceListDialogFragment(placeList, new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomPlace place) {
                // Dismiss the dialog fragment
                getSupportFragmentManager().beginTransaction().remove(dialogFragment).commit();
                // Navigate to the selected place
                navigateToPlace(place);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        dialogFragment.show(transaction, "place_list_dialog");
    }
    private void showRouteListDialog() {

        routeAdapter.notifyDataSetChanged();


        routeListDialogFragment = new RouteListDialogFragment(routes, new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.google_maps_serko.Route route) {
                getSupportFragmentManager().beginTransaction().remove(routeListDialogFragment).commit();
                displayRoutesOnMap(route.getWaypoints());
// Navigate to the selected place
            }


        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        routeListDialogFragment.show(transaction, "route_list_dialog");
    }


    private void navigateToPlace(Place place) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatLng().latitude,place.getLatLng().longitude)));
        LatLng navigatePlace = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(navigatePlace, 16f);
      mMap.animateCamera(cameraUpdate);
    }
    private void showRouteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_route, null);
        dialogBuilder.setView(dialogView);
        EditText editTextRouteTitle = dialogView.findViewById(R.id.editRouteTitle);
        dialogBuilder.setTitle("Route Information");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextRouteTitle.getText().toString();
                saveRoute(title);

            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true;

                } else {

                }
                return;
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getMyLocation();

    }
    private void addMarkerWithInfo(String title, String snippet, LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        mMap.addMarker(markerOptions);



    }
    private void addPlace(String id, String title, String snippet, LatLng latlng) {
        placeList.add(new CustomPlace(id, title,snippet, latlng));
    }

    private void showMarkerInputDialog(LatLng latlng) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_marker_info, null);
        dialogBuilder.setView(dialogView);
        EditText editTextId = dialogView.findViewById(R.id.editTextId);


        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextSnippet = dialogView.findViewById(R.id.editTextSnippet);

        dialogBuilder.setTitle("Marker Information");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = editTextId.getText().toString();
                String title = editTextTitle.getText().toString();
                String snippet = editTextSnippet.getText().toString();
                // Add marker with the retrieved information
                addMarkerWithInfo(title, snippet,latlng);
                addPlace(id,title,snippet,latlng);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void toggleRoute(View view) {
     isAddingRoute=!isAddingRoute;
     if (!isAddingRoute){
         showRouteDialog();

     }

    }
    public void shareCurrentLocation(View view) {
        Location currentLocation = getCurrentLocation();
        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            String mapUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mapUrl);
            startActivity(Intent.createChooser(shareIntent, "Share Location"));
        } else {
            Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (!isBeingServed){
                    myLocation=location;
                    LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                }
                else{
                    endlocation = location;
                    start=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                    end = new LatLng(endlocation.getLatitude(),endlocation.getLongitude());
                    DirectionsFetcher.fetchDirections(
                            start.latitude + "," + start.longitude,
                            end.latitude + "," + end.longitude,
                            "AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE",
                            new DirectionsFetcher.DirectionsListener() {
                                @Override
                                public void onDirectionsReceived(String directions, String duration, String distance) {

                                    directionsTextView.setText(directions);
                                    directionsTextView.append("\nDuration:"+duration);
                                    directionsTextView.append("\nDistance:"+distance);

                                }

                                @Override
                                public void onDirectionsFetchFailed(String errorMessage) {
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                    Findroutes(end,start);






                }
            }});
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {


                    showMarkerInputDialog(latLng);




            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                    if (isAddingRoute) {
                        waypoints = waypointsManager.getWaypoints();
                        waypoints.add(latLng);
                        for (int i = 1; i < waypoints.size(); i++) {
                            Findroutes(waypoints.get(i - 1),waypoints.get(i) );
                        }

                    }
                    else{

                        end=latLng;

                        mMap.clear();

                        start=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                        DirectionsFetcher.fetchDirections(
                                start.latitude + "," + start.longitude,
                                end.latitude + "," + end.longitude,
                                "AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE",
                                new DirectionsFetcher.DirectionsListener() {
                                    @Override
                                    public void onDirectionsReceived(String directions, String duration, String distance) {

                                        directionsTextView.setText(directions);
                                        directionsTextView.append("\nDuration:"+duration);
                                        directionsTextView.append("\nDistance:"+distance);
                                    }

                                    @Override
                                    public void onDirectionsFetchFailed(String errorMessage) {
                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        Findroutes(start,end);


                    }




            }
        });

    }



    private void startInactivityTimer() {
        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                    showRouteDialog();
            }}, INACTIVITY_DELAY_MS);
    };

    private void displayRoutesOnMap(List<LatLng> wpoints) {
        ;

            for (int i=0;i<wpoints.size();i++){
                mMap.addMarker(new MarkerOptions().position(wpoints.get(i)).title(""+wpoints.get(i).latitude + wpoints.get(i).longitude));
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(wpoints);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                mMap.addPolyline(lineOptions);
            }


    }
    public void onClickClear(View view){
        mMap.clear();
    }
public void saveRoute(String routename){
        if (waypoints!=null){

            routes.add(new com.example.google_maps_serko.Route(waypoints,routename));
            Toast.makeText(this, "Route Saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

        }


}

    private void resetInactivityTimer() {
        inactivityTimer.cancel();
        inactivityTimer = new Timer();
        startInactivityTimer();
    }
    private void drawRoutes(List<LatLng> points) {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.RED);

        mMap.addPolyline(lineOptions);
    }

    public  void getDirections( String origin, String destination) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/directions/json").newBuilder();
        urlBuilder.addQueryParameter("origin", origin);
        urlBuilder.addQueryParameter("destination", destination);
        urlBuilder.addQueryParameter("key", "AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    JSONArray routes = json.getJSONArray("routes");
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject polyline = route.getJSONObject("overview_polyline");
                    String encodedPoints = polyline.getString("points");

                    List<LatLng> points = decodePoly(encodedPoints);
                    drawRoutes(points);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }
        return poly;
    }

    public LatLng Geocoding(String locationName){
        Geocoder geocoder  = new Geocoder(MainActivity.this, Locale.getDefault());
        try{
            List<Address>listAddress = geocoder.getFromLocationName(locationName,1);
            if (listAddress.size()>0){
                LatLng latlng = new LatLng(listAddress.get(0).getLatitude(),listAddress.get(0).getLongitude());
                return latlng;
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
        return null;

    }



    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(MainActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE")
                    .build();
            routing.execute();

        }
    }

    private void drawRoute(DirectionsResult result) {
        if (result.routes != null && result.routes.length > 0) {
            com.google.maps.model.LatLng startLocation = result.routes[0].legs[0].startLocation;
            com.google.maps.model.LatLng endLocation = result.routes[0].legs[0].endLocation;
            List<com.google.maps.model.LatLng> decodedPath = result.routes[0].overviewPolyline.decodePath();

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(convertLatLngList(decodedPath))
                    .width(10)
                    .color(Color.BLUE);

            mMap.addPolyline(polylineOptions);

            mMap.addMarker(new MarkerOptions().position(new LatLng(startLocation.lat, startLocation.lng)).title("Start"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(endLocation.lat, endLocation.lng)).title("End"));
        } else {
            Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private java.util.List<LatLng> convertLatLngList(List<com.google.maps.model.LatLng> list) {
        java.util.List<LatLng> newList = new java.util.ArrayList<>();
        for (com.google.maps.model.LatLng latLng : list) {
            newList.add(new LatLng(latLng.lat, latLng.lng));
        }
        return newList;
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MainActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            if (!isAddingRoute){
                polylines.clear();

            }
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }


        if (isBeingServed==false){
            MarkerOptions endMarker = new MarkerOptions();
            endMarker.position(polylineEndLatLng);
            endMarker.title("Destination" + polylineEndLatLng);
            mMap.addMarker(endMarker);
        }

    }
    public void ServeRoute(View view) {
        if (!isBeingServed) {
            mMap.clear();
            isBeingServed = true;
           LatLng latlng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latlng).title("start"));


            Toast.makeText(this, ""+isBeingServed, Toast.LENGTH_SHORT).show();

        } else {
            isBeingServed = false;
            if (end!=null){
                mMap.addMarker(new MarkerOptions().position(end).title("end"));

            }
            Toast.makeText(this, ""+isBeingServed, Toast.LENGTH_SHORT).show();


        }
        getMyLocation();

    }





    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);

    }
    public static String extractTextFromHtml(String htmlInstructions) {
        StringBuilder textBuilder = new StringBuilder();

        // Parse HTML using Jsoup
        Document doc = Jsoup.parse(htmlInstructions);

        // Select all elements containing text
        Elements elements = doc.select("*");

        // Extract text from each element
        for (Element element : elements) {
            String text = element.ownText();
            if (!text.isEmpty()) {
                textBuilder.append(text).append(" ");
            }
        }

        // Return the extracted text
        return textBuilder.toString().trim();
    }
    public void SearchRoutes(View view) {
        origin_place = findViewById(R.id.origin_place);
        String origin = origin_place.getText().toString();
        destination_place = findViewById(R.id.destination_place);
        String destination = destination_place.getText().toString();

//        if (origin.length()==0 && destination.length()>0){
//            LatLng end = Geocoding(destination);
//            LatLng v = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
//
//
//
//                Findroutes(v, end);
//                DirectionsFetcher.fetchDirections(
//                        myLocation.getLatitude()+ "," + myLocation.getLongitude(),
//                        end.latitude + "," + end.longitude,
//                        "AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE",
//                        new DirectionsFetcher.DirectionsListener() {
//                            @Override
//                            public void onDirectionsReceived(String directions) {
//
//                                directionsTextView.setText(directions);
//
//                            }
//
//                            @Override
//                            public void onDirectionsFetchFailed(String errorMessage) {
//                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//                            }
//                        }
//                );
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                        start, 16f);
//                mMap.animateCamera(cameraUpdate);
//
//
//
//        }
        if (origin!="" && destination!="") {

            LatLng start = Geocoding(origin);
            LatLng end = Geocoding(destination);

            if (start != null && end != null) {
                mMap.clear();
                Findroutes(start, end);
                DirectionsFetcher.fetchDirections(
                        start.latitude + "," + start.longitude,
                        end.latitude + "," + end.longitude,
                        "AIzaSyCQOmPlvzblQX4r2z6yPCFfxLzOGiSjjdE",
                        new DirectionsFetcher.DirectionsListener() {
                            @Override
                            public void onDirectionsReceived(String directions, String duration, String distance) {

                                directionsTextView.setText(directions);
                                directionsTextView.append("\nDuration:"+duration);
                                directionsTextView.append("\nDistance:"+distance);



                            }

                            @Override
                            public void onDirectionsFetchFailed(String errorMessage) {
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                );
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        start, 16f);
                mMap.animateCamera(cameraUpdate);

            } else {
                Toast.makeText(MainActivity.this, "Unable to get start or end", Toast.LENGTH_LONG).show();

            }


        }
        else{
            Toast.makeText(MainActivity.this, "Unable", Toast.LENGTH_LONG).show();

        }


    }
}
