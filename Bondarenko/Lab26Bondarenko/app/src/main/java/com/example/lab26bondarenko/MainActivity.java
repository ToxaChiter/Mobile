package com.example.lab26bondarenko;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

// Дополнительные импорты
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.PolyUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private FrameLayout map;
    private FusedLocationProviderClient locationClient;
    Switch switch_3d;
    EditText search_edit_text;

    // Глобальная переменная для хранения меток
    private ArrayList<Marker> markers = new ArrayList<>();
    private Polyline currentPolyline;
    private Marker lastSearchedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.id_map);
        search_edit_text = (EditText) findViewById(R.id.search_edit_text);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        switch_3d = (Switch) findViewById(R.id.switch_3d);
        switch_3d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("getLocation()", location.toString());
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        addMarker(userLocation, "Ваше местоположение");
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    }
                }
            });
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;


        // Слушатель для нажатия на название метки
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Создание AlertDialog.Builder для подтверждения удаления
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Удаление метки (Разработал Бондаренко)");
                builder.setMessage("Вы действительно хотите удалить метку " + marker.getTitle().toString() + "?");

                // Установка кнопок
                builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove(); // Удаление метки
                        markers.remove(marker);
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Создание и отображение AlertDialog
                AlertDialog deleteDialog = builder.create();
                deleteDialog.show();
            }
        });

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                // Создание AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Установка заголовка и сообщения
                builder.setTitle("Создание метки (Разработал Бондаренко)");
                builder.setMessage("Введите название для метки:");

                // Добавление EditText в диалоговое окно
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);

                // Установка кнопок
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String markerTitle = input.getText().toString();
                        if (!markerTitle.equals("")) {
                            addMarker(latLng, markerTitle); // Используйте метод addMarker
                        }
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Создание и отображение AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Метод вызывается при нажатии на кнопку
    public void onRouteButtonClick(View view) {
        // Проверка, есть ли хотя бы две метки на карте
        if (markers.size() >= 2) {
            // Создание списка названий меток для отображения в диалоговом окне
            String[] items = new String[markers.size()];
            for (int i = 0; i < markers.size(); i++) {
                items[i] = markers.get(i).getTitle();
            }

            // Флаги для отслеживания выбранных меток
            final boolean[] checkedItems = new boolean[markers.size()];

            // Создание AlertDialog для выбора меток
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Выберите две метки для маршрута")
                    .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            // Обновление флагов выбранных меток
                            checkedItems[which] = isChecked;
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Список выбранных меток
                            ArrayList<Marker> selectedMarkers = new ArrayList<>();
                            for (int i = 0; i < checkedItems.length; i++) {
                                if (checkedItems[i]) {
                                    selectedMarkers.add(markers.get(i));
                                }
                            }

                            // Проверка, выбраны ли ровно две метки
                            if (selectedMarkers.size() == 2) {
                                // Здесь вызовите метод для построения маршрута между метками
                                buildRouteBetweenMarkers(selectedMarkers.get(0), selectedMarkers.get(1));
                            } else {
                                Toast.makeText(MainActivity.this, "Выберите ровно две метки", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Отмена", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(this, "Добавьте на карту больше меток", Toast.LENGTH_LONG).show();
        }
    }

    // Метод для добавления метки на карту и в список
    private void addMarker(LatLng latLng, String title) {
        Marker marker = gMap.addMarker(new MarkerOptions().position(latLng).title(title));
        markers.add(marker);
    }

    // Метод для построения маршрута между двумя метками
    private void buildRouteBetweenMarkers(Marker startMarker, Marker endMarker) {
        // Получение координат меток
        LatLng startLatLng = startMarker.getPosition();
        LatLng endLatLng = endMarker.getPosition();

        // Формирование URL для запроса к Google Directions API
        String url = getDirectionsUrl(startLatLng, endLatLng);
        //Log.d("buildRouteBetweenMarkers", "url" + url);

        // Запрос к Google Directions API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Обработка ответа
                        parseDirectionsResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Обработка ошибки
                error.printStackTrace();
            }
        });

        // Добавление запроса в очередь запросов
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    // Метод для формирования URL запроса к Google Directions API
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Конструирование URI для запроса
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyByntb7GcHMs8X-8HCNgUdwiAAd7m6S65M";
    }

    // Метод для обработки ответа от Google Directions API
    private void parseDirectionsResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            if ("OK".equals(status)) {
                JSONArray routes = response.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                List<LatLng> list = PolyUtil.decode(polyline);

                // Удаление предыдущего маршрута, если он существует
                if (currentPolyline != null) {
                    currentPolyline.remove();
                }

                // Отображение маршрута на карте
                PolylineOptions options = new PolylineOptions().width(10).color(ContextCompat.getColor(this, R.color.google_blue)).geodesic(true);
                for (LatLng point : list) {
                    options.add(point);
                }
                currentPolyline = gMap.addPolyline(options);
            } else {
                // Логирование статуса ответа
                Log.e("parseDirectionsResponse", "Статус ответа: " + status);
            }
            //Log.d("response", response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSearchButtonClick(View view) {
        String searchText = search_edit_text.getText().toString();
        if (searchText.matches("[-+]?\\d{1,3}\\.\\d+,\\s*[-+]?\\d{1,3}\\.\\d+")) {
            if (lastSearchedMarker != null) {
                lastSearchedMarker.remove();
            }

            String[] LatLong = searchText.split(",");
            LatLng position = new LatLng(Double.parseDouble(LatLong[0]), Double.parseDouble(LatLong[1]));
            MarkerOptions searchMarker = new MarkerOptions().position(position).title("");
            lastSearchedMarker = gMap.addMarker(searchMarker);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
        else {
            for (Marker marker : markers) {
                if (searchText.equals(marker.getTitle().toString())) {
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                    return;
                }
            }
            Toast.makeText(MainActivity.this, "Метка не найдена", Toast.LENGTH_SHORT).show();
        }
    }
}
