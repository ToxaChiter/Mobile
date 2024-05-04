package com.example.google_maps_serko;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectionsFetcher {

    public interface DirectionsListener {
        void onDirectionsReceived(String directions,  String duration,String distance);
        void onDirectionsFetchFailed(String errorMessage);
    }

    public static void fetchDirections(String origin, String destination, String apiKey, DirectionsListener listener) {
        new FetchDirectionsTask(listener).execute(origin, destination, apiKey);
    }

    private static class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        private DirectionsListener listener;

        FetchDirectionsTask(DirectionsListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            String origin = params[0];
            String destination = params[1];
            String apiKey = params[2];

            String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                    origin + "&destination=" + destination + "&key=" + apiKey;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    JSONObject route = routesArray.getJSONObject(0);
                    JSONArray legsArray = route.getJSONArray("legs");
                    JSONObject leg = legsArray.getJSONObject(0);
                    JSONArray stepsArray = leg.getJSONArray("steps");
                    JSONObject durationObject = leg.getJSONObject("duration");
                    String duration = durationObject.getString("text");
                    JSONObject distanceObject = leg.getJSONObject("distance");
                    String distance = distanceObject.getString("text");

                    StringBuilder directions = new StringBuilder();
                    for (int i = 0; i < stepsArray.length(); i++) {
                        JSONObject step = stepsArray.getJSONObject(i);
                        String instruction = step.getString("html_instructions");

                        directions.append(instruction).append("\n");
                    }

                    listener.onDirectionsReceived(directions.toString().replaceAll("\\<.*?>", ""), duration, distance );
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onDirectionsFetchFailed("Error parsing directions JSON");
                }
            } else {
                listener.onDirectionsFetchFailed("Failed to fetch directions");
            }
        }
    }
}
