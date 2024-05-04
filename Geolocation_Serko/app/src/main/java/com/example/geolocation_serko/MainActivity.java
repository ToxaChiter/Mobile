package com.example.geolocation_serko;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import java.util.Timer;
import java.util.TimerTask;
import android.location.GnssStatus;
import android.os.Build;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private GnssStatus.Callback gnssCallback;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final long MIN_TIME_INTERVAL = 1000;
    private static final float MIN_DISTANCE = 0;
    private SensorManager sensorManager;
    private static final float SHAKE_THRESHOLD = 10f;
    private float lastX, lastY, lastZ;
    private long lastUpdate = 0;

    private Sensor accelerometer;
    private  Location previousLocation;
    private static final int FIX_THRESHOLD = 50;
    private static final int TIME_THRESHOLD = 5000;
    private RelativeLayout mainLayout;
    private Handler handler;
    private Runnable fixRunnable;
    ImageView arrow;
    private TextView locationTextView, shakingDirectionTextView, speedTextView, altitudeTextView, satelliteCountTextView;
    private LocationManager locationManager;
    private Timer locationUpdateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mainLayout = findViewById(R.id.mainLayout);
        arrow = findViewById(R.id.arrow);
        handler = new Handler();
        speedTextView = findViewById(R.id.speedTextView);
        altitudeTextView = findViewById(R.id.altitudeTextView);
        shakingDirectionTextView = findViewById(R.id.shakingDirectionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        satelliteCountTextView = findViewById(R.id.satelliteCountTextView); // Initialize satelliteCountTextView
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            requestLocationUpdates();

          //  startLocationUpdateTimer();
            displaySatelliteInfo();
        }
    }

    private void displaySatelliteInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        gnssCallback = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                int count = status.getSatelliteCount();
                updateSatelliteCount(count);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.registerGnssStatusCallback(gnssCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssCallback);
        }
    }

    private void updateSatelliteCount(int count) {

        satelliteCountTextView.setText("Number of satellites: " + count);
    }

    private void startLocationUpdateTimer() {
        locationUpdateTimer = new Timer();
        locationUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        double latitude = lastLocation.getLatitude();
                        double longitude = lastLocation.getLongitude();
                        double speed = calculateSpeed(lastLocation);
                        double altitude = lastLocation.getAltitude();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                locationTextView.setText("🌍 Latitude: " + latitude + "\n🌐 Longitude: " + longitude);
                                speedTextView.setText("🚀 Speed: " + String.format("%.2f", speed) + " m/s");
                                altitudeTextView.setText("⛰ Altitude: " + altitude + " meters");
                                locationTextView.setTextColor(getResources().getColor(R.color.accent_blue)); // Set your desired text color
                                speedTextView.setTextColor(getResources().getColor(R.color.accent_blue)); // Set your desired text color
                                altitudeTextView.setTextColor(getResources().getColor(R.color.accent_blue));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                locationTextView.setText("Location not available");
                            }
                        });
                    }
                }
            }
        }, 0,  1000); // Update every minute
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, this);
        } else {
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastUpdate;
            if (timeDifference > 100) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float deltaX = Math.abs(x - lastX);
                float deltaY = Math.abs(y - lastY);
                float deltaZ = Math.abs(z - lastZ);
                String shakingDirection = "";
                if (deltaX > SHAKE_THRESHOLD || deltaY > SHAKE_THRESHOLD || deltaZ > SHAKE_THRESHOLD) {
                    if (deltaX > deltaY && deltaX > deltaZ) {
                        shakingDirection = "Shaking along X-axis";
                        if (x>0){
                            shakingDirection+=" to the Left";
                            arrow.setImageResource(R.drawable.left );
                        }
                        else{
                            shakingDirection+=" to the Right";
                            arrow.setImageResource(R.drawable.right );
                        }
                    } else if (deltaY > deltaX && deltaY > deltaZ) {
                        shakingDirection = "Shaking along Y-axis";

                        if (y>0){
                            shakingDirection+=" to the Up and Right";
                            arrow.setImageResource(R.drawable.upright );

                        }
                        else{
                            shakingDirection+=" to the Down and Left";
                            arrow.setImageResource(R.drawable.downleft );
                        }
                    } else {
                        shakingDirection = "Shaking along Z-axis";
                        if (z>0){
                            shakingDirection+=" Forward";
                            arrow.setImageResource(R.drawable.up );
                        }
                        else{
                            shakingDirection+=" Back";
                            arrow.setImageResource(R.drawable.back );


                        }
                    }
                    arrow.setVisibility(View.VISIBLE);
                    shakingDirectionTextView.setTextColor(Color.RED);

                } else {
                    shakingDirection = "Not shaking";
                    arrow.setVisibility(View.GONE);
                    shakingDirectionTextView.setTextColor(Color.GREEN);
                }

                shakingDirectionTextView.setText(shakingDirection);

                lastX = x;
                lastY = y;
                lastZ = z;
                lastUpdate = currentTime;
            }
        }
    }
    private double calculateSpeed(Location location) {
        double speed = 0.0;
        if (location != null) {

            if (previousLocation == null) {
                previousLocation = location;
            }
            else {

                double distance = previousLocation.distanceTo(location);
                long timeDifference = location.getTime() - previousLocation.getTime();
                if (timeDifference > 0) {
                double timeDifferenceSeconds = timeDifference / 1000.0;
                speed = 3.28084 * (distance / timeDifferenceSeconds);
                previousLocation = location;
            }
            }

        }
        return speed;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double speed = calculateSpeed(location);
            double altitude = location.getAltitude();

            locationTextView.setText("🌍 Latitude: " + latitude + "\n🌐 Longitude: " + longitude);
            speedTextView.setText("🚀 Speed: " + String.format("%.2f", speed) + " m/s");
            altitudeTextView.setText("⛰ Altitude: " + altitude + " meters");

            locationTextView.setTextColor(getResources().getColor(R.color.black)); // Set your desired text color
            speedTextView.setTextColor(getResources().getColor(R.color.black)); // Set your desired text color
            altitudeTextView.setTextColor(getResources().getColor(R.color.black));
        } else {
            Toast.makeText(MainActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        if (locationUpdateTimer != null) {
            locationUpdateTimer.cancel();
            locationUpdateTimer.purge();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE, this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.registerGnssStatusCallback(gnssCallback);
        }
    }
}

