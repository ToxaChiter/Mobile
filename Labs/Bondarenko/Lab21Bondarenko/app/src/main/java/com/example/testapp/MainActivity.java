package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.GnssStatus;
import android.os.Bundle;

import android.os.Environment;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import android.content.Intent;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // task1
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView xValue, yValue, zValue, speedTextView, accelerationTextView;
    private float lastX, lastY, lastZ;
    private float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    private float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 600;
    private TextView shakeInfoTextView;

    private enum State {STATIONARY, MOVING, SHAKEN}
    private State currentState = State.STATIONARY;

    // task2
    private TextView latitudeText, longitudeText, altitudeText;

    TextView IsGPSEnabled;

    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    Timer timer;
    private TextView satellitesInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = findViewById(R.id.xValue);
        yValue = findViewById(R.id.yValue);
        zValue = findViewById(R.id.zValue);
        shakeInfoTextView = findViewById(R.id.shakeInfoTextView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        speedTextView = findViewById(R.id.speedTextView);
        accelerationTextView = findViewById(R.id.accelerationTextView);

        // task2

        IsGPSEnabled = findViewById(R.id.IsGPSEnabled);

        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        altitudeText = findViewById(R.id.altitudeText);
        satellitesInfoText = findViewById(R.id.satellitesInfoText);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkEnabled();

        // Запустим таймер, который будет сохранять координаты каждые 5 минут
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveCoordinates();
            }
        }, 30 * 1000, 1 * 60 * 1000); // 5 минут (в миллисекундах)
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastUpdate) > 100) {
            long timeDiff = (currentTime - lastUpdate);
            lastUpdate = currentTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);

            float acceleration = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 1000;
            float speed = (float) Math.sqrt(x * x + y * y + z * z);
            float speedDiff = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 10000;

            if (speedDiff > SHAKE_THRESHOLD) {
                currentState = State.SHAKEN;
            } else if (speedDiff > 0) {
                currentState = State.MOVING;
            } else {
                currentState = State.STATIONARY;
            }

            updateUI(event, speed, acceleration);

            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Доступ запрещен!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1, locationListener);
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,0,0, locationListener);
        // Регистрация обратного вызова GnssStatus
        locationManager.registerGnssStatusCallback(gnssStatusCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            // Отмена регистрации обратного вызова GnssStatus
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateUI(SensorEvent event, float speed, float acceleration) {
        switch (currentState) {
            case STATIONARY:
                shakeInfoTextView.setText("Устройство неподвижно!");
                break;
            case MOVING:
                shakeInfoTextView.setText("Устройство в движении!");
                break;
            case SHAKEN:
                shakeInfoTextView.setText("Устройство встряхнули!");
                break;
        }

        xValue.setText("X: " + String.format("%.3f", event.values[0]) + "  Min X: " + String.format("%.3f", minX) + "  Max X: " + String.format("%.3f", maxX));
        yValue.setText("Y: " + String.format("%.3f", event.values[1]) + "  Min Y: " + String.format("%.3f", minY) + "  Max Y: " + String.format("%.3f", maxY));
        zValue.setText("Z: " + String.format("%.3f", event.values[2]) + "  Min Z: " + String.format("%.3f", minZ) + " Max Z: " + String.format("%.3f", maxZ));

        speedTextView.setText("Скорость: " + speed + "м/с");
        accelerationTextView.setText("Ускорение: " + acceleration + "м/с\u00B2");
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Доступ запрещен!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }

            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            checkEnabled();
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;

        latitudeText.setText("Широта: "+ location.getLatitude() + "\u00b0");
        longitudeText.setText("Долгота: " + location.getLongitude() + "\u00b0");
//        altitudeText.setText("Высота: " + location.getAltitude() + "м");
        altitudeText.setText(String.format("Высота: %.3fм", location.getAltitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void checkEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            IsGPSEnabled.setText("GPS доступен");
        }
        else {
            IsGPSEnabled.setText("GPS не доступен");
        }
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void saveCoordinates() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Доступ запрещен!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                }

                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                long GPSLocationTime = 0;
                if (locationGPS != null) {
                    GPSLocationTime = locationGPS.getTime();
                }

                long NetLocationTime = 0;

                if (locationNet != null) {
                    NetLocationTime = locationNet.getTime();
                }

                Location location;
                if (0 < GPSLocationTime - NetLocationTime) {
                    location = locationGPS;
                } else {
                    location = locationNet;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("Широта: ").append(String.valueOf(location.getLatitude())).append(", Долгота: ").append(String.valueOf(location.getLongitude())).append("\n");
                try {
                    FileOutputStream fileOutputStream = openFileOutput("coordinates.txt", MODE_PRIVATE);
                    fileOutputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.close();
                    Toast.makeText(MainActivity.this, "Координаты записаны!\n" + builder.toString(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateSatellitesInfo(GnssStatus gnssStatus) {
        StringBuilder satellitesInfo = new StringBuilder();

        int satelliteCount = gnssStatus.getSatelliteCount();
        satellitesInfo.append("Количество спутников: ").append(satelliteCount).append("\n");

        // Перебираем каждый спутник и получаем информацию о нем
        for (int i = 0; i < satelliteCount; i++) {
            satellitesInfo.append("Спутник ").append(i + 1).append(": ");
            satellitesInfo.append("ID=").append(gnssStatus.getSvid(i)).append(", ");
            satellitesInfo.append("Сила сигнала=").append(gnssStatus.getCn0DbHz(i)).append("dBHz, ");
            satellitesInfo.append("Азимут=").append(gnssStatus.getAzimuthDegrees(i)).append("\u00b0, ");
            satellitesInfo.append("Высота=").append(gnssStatus.getElevationDegrees(i)).append("\u00b0\n");
        }

        // Обновляем TextView с информацией о спутниках
        satellitesInfoText.setText(satellitesInfo.toString());
    }

    private final GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            updateSatellitesInfo(status);
        }
    };


}