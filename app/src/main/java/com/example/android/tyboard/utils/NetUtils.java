package com.example.android.tyboard.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by andirs on 5/12/17.
 */

public final class NetUtils {

    private static final String TAG = NetUtils.class.getSimpleName();

    // Example URL
    // https://maps.googleapis.com/maps/api/directions/json?origin=248+Louise+Lane+San+Mateo+CA&destination=Brightcove+San+Francisco&departure_time=now&traffic_model=best_guess&key=AIzaSyDWdsDqlar0n9iV2tIB7Gb_G9iNV1P4K8E

    private static final String GOOGLE_DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_DIRECTIONS_ORIGIN_PARAM = "origin";
    private static final String GOOGLE_DIRECTIONS_DESTINATION_PARAM = "destination";
    private static final String GOOGLE_DIRECTIONS_DEPARTURE_TIME_PARAM = "departure_time";
    private static final String GOOGLE_DIRECTIONS_TRAFFIC_MODEL_PARAM = "traffic_model";
    private static final String GOOGLE_DIRECTIONS_SHOW_ALTERNATIVES_PARAM = "alternatives";
    private static final String GOOGLE_DIRECTIONS_KEY_PARAM = "key";
    private static final String gDirectionsDepartureTime = "now";
    private static final String gDirectionsTrafficModel = "best_guess";
    private static final String gDirectionsAlternatives = "false";


    //private static final String OWEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?zip=94040,us";
    private static final String OWEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String OWEATHER_ZIP_PARAM = "zip";
    private static final String OWEATHER_LAT_PARAM = "lat";
    private static final String OWEATHER_LON_PARAM = "lon";
    private static final String OWEATHER_UNITS_PARAM = "units";
    private static final String OWEATHER_KEY_PARAM = "APPID";


    public static URL buildWeatherUrl(String latCode, String lonCode, String unitString, String oWeatherKey) {
        if (latCode.isEmpty() || lonCode.isEmpty() || oWeatherKey.isEmpty())
            return null;

        Uri builtUri = Uri.parse(OWEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(OWEATHER_LAT_PARAM, latCode)
                .appendQueryParameter(OWEATHER_LON_PARAM, lonCode)
                .appendQueryParameter(OWEATHER_UNITS_PARAM, unitString)
                .appendQueryParameter(OWEATHER_KEY_PARAM, oWeatherKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built OpenWeather URI " + url);

        return url;

    }

    /**
     * Builds the URL used to talk to the Google Directions API.
     *
     * @param origin our home location
     * @param destination our work location to track in the morning
     * @return The URL to use to query the Google API
     */
    public static URL buildDirectionsUrl(String origin, String destination, String gDirectionsKey, String gDirectionsDepartureTime) {

        // If origin or destination aren't set, return null
        if (origin.isEmpty() || destination.isEmpty())
            return null;

        Uri builtUri = Uri.parse(GOOGLE_DIRECTIONS_BASE_URL).buildUpon()
                .appendQueryParameter(GOOGLE_DIRECTIONS_ORIGIN_PARAM, origin)
                .appendQueryParameter(GOOGLE_DIRECTIONS_DESTINATION_PARAM, destination)
                .appendQueryParameter(GOOGLE_DIRECTIONS_DEPARTURE_TIME_PARAM, gDirectionsDepartureTime)
                .appendQueryParameter(GOOGLE_DIRECTIONS_TRAFFIC_MODEL_PARAM, gDirectionsTrafficModel)
                .appendQueryParameter(GOOGLE_DIRECTIONS_SHOW_ALTERNATIVES_PARAM, gDirectionsAlternatives)
                .appendQueryParameter(GOOGLE_DIRECTIONS_KEY_PARAM, gDirectionsKey).build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Google Directions URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            // Using this will grab everything we get back from our request
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) return scanner.next();
            else return null;

        } finally {
            urlConnection.disconnect();
        }
    }





}
