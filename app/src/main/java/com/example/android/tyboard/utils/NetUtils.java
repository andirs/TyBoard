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
    private static final String alternatives = "false";
    private static final String departure_time = "now";
    private static final String traffic_model = "best_guess";
    private static final String key = "AIzaSyDWdsDqlar0n9iV2tIB7Gb_G9iNV1P4K8E";


    /**
     * Builds the URL used to talk to the Google Directions API.
     *
     * @param origin our home location
     * @param destination our work location to track in the morning
     * @return The URL to use to query the Google API
     */
    public static URL buildDirectionsUrl(String origin, String destination) {

        // If origin or destination aren't set, return null
        if (origin.isEmpty() || destination.isEmpty())
            return null;

        Uri builtUri = Uri.parse(GOOGLE_DIRECTIONS_BASE_URL).buildUpon()
                .appendQueryParameter(GOOGLE_DIRECTIONS_ORIGIN_PARAM, origin)
                .appendQueryParameter(GOOGLE_DIRECTIONS_DESTINATION_PARAM, destination)
                .appendQueryParameter(GOOGLE_DIRECTIONS_DEPARTURE_TIME_PARAM, departure_time)
                .appendQueryParameter(GOOGLE_DIRECTIONS_TRAFFIC_MODEL_PARAM, traffic_model)
                .appendQueryParameter(GOOGLE_DIRECTIONS_SHOW_ALTERNATIVES_PARAM, alternatives)
                .appendQueryParameter(GOOGLE_DIRECTIONS_KEY_PARAM, key).build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Build URI " + url);

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
