package com.example.android.tyboard.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andirs on 5/12/17.
 */

public final class JsonUtils {

    public static String[] getDirectionsStringsFromJson(Context context,
                                                        String directionsJsonStr)
            throws JSONException {


        /* All routes are stored in routes object */
        final String GDA_ROUTES = "routes";

        /* All legs are stored in legs object */
        final String GDA_LEGS = "legs";

        /* Distance indicator from origin to destination */
        final String GDA_DISTANCE = "distance";

        /* Optimal duration without traffic */
        final String GDA_DURATION = "duration";

        /* Indicator for duration including traffic */
        final String GDA_DURATION_IN_TRAFFIC = "duration_in_traffic";

        /* Indicator for track summary */
        final String GDA_SUMMARY = "summary";

        String[] parsedDirectionData = new String[4];

        JSONObject directionsJson = new JSONObject(directionsJsonStr);

        // TODO: Check for errors with JSON.

        JSONArray routeArray = directionsJson.getJSONArray(GDA_ROUTES);

        // Grab first route
        JSONObject route = routeArray.getJSONObject(0);

        // Get all legs from this route
        JSONArray legs = route.getJSONArray(GDA_LEGS);

        // Get summary title for this route
        String summary = route.getString(GDA_SUMMARY);

        // Open leg
        JSONObject leg = legs.getJSONObject(0);

        JSONObject distanceObject = leg.getJSONObject(GDA_DISTANCE);
        JSONObject durationObject = leg.getJSONObject(GDA_DURATION);
        JSONObject durationInTrafficObject = leg.getJSONObject(GDA_DURATION_IN_TRAFFIC);

        parsedDirectionData[0] = distanceObject.getString("text");
        parsedDirectionData[1] = durationObject.getString("text");
        parsedDirectionData[2] = durationInTrafficObject.getString("text");
        parsedDirectionData[3] = summary;

        return parsedDirectionData;
    }

}
