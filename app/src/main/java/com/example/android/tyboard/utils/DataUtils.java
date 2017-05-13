package com.example.android.tyboard.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.util.Log;

import com.example.android.tyboard.data.JsonDirectionsStore;
import com.example.android.tyboard.data.JsonWeatherStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.android.tyboard.utils.GenUtils.hasApiLevel;

/**
 * Created by andirs on 5/12/17.
 */

public final class DataUtils {

    private static final String TAG = DataUtils.class.getSimpleName();

    public static JsonWeatherStore getWeatherStringsFromJson(Context context,
                                                             String weatherJsonStr)
            throws JSONException {

        JsonWeatherStore weatherData;

        final String OW_WEATHER = "weather";
        final String OW_MAIN = "main";
        final String OW_WIND = "wind";
        final String OW_SYS = "sys";

        JSONObject weatherJson = new JSONObject(weatherJsonStr);

        // Get weatherArray
        JSONArray weatherArray = weatherJson.getJSONArray(OW_WEATHER);

        // Get mainObject
        JSONObject mainObject = weatherJson.getJSONObject(OW_MAIN);

        // Get windObject
        JSONObject windObject = weatherJson.getJSONObject(OW_WIND);

        // Get sysObject
        JSONObject sysObject = weatherJson.getJSONObject(OW_SYS);

        /*
                            String descriptionString,
                            String iconString, String tempString,
                            String pressureString, String humidityString,
                            String tempMinString, String tempMaxString,
                            String windSpeedString, String sunriseString,
                            String sunsetString
         */

        // weatherObject: Stores description and icon information
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        String descriptionString = weatherObject.getString("description");
        String iconString = weatherObject.getString("icon");

        // mainObject: Stores temperature, pressure, humidity, tempMin and temp Max information
        String tempString = mainObject.getString("temp");
        String pressureString = mainObject.getString("pressure");
        String humidityString = mainObject.getString("humidity");
        String tempMinString = mainObject.getString("temp_min");
        String tempMaxString = mainObject.getString("temp_max");

        // windObject: Stores wind related information
        String windSpeedString = windObject.getString("speed");

        // sysObject: Stores system and time related information
        String sunriseString = sysObject.getString("sunrise");
        String sunsetString = sysObject.getString("sunset");

        weatherData = new JsonWeatherStore(descriptionString, iconString, tempString,
                pressureString, humidityString, tempMinString,
                tempMaxString, windSpeedString, sunriseString,
                sunsetString);

        return weatherData;
    }

    public static JsonDirectionsStore getDirectionsStringsFromJson(Context context,
                                                                   String directionsJsonStr)
            throws JSONException {

        //HashMap<String, Object> directionData = new HashMap<String, Object>();
        JsonDirectionsStore directionData;

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

        /*
        directionData.put("distanceString", distanceObject.getString("text"));
        directionData.put("durationString", durationObject.getString("text"));
        directionData.put("durationInTrafficString", durationInTrafficObject.getString("text"));
        directionData.put("distance", Integer.valueOf(distanceObject.getString("value")));
        directionData.put("duration", Integer.valueOf(durationObject.getString("value")));
        directionData.put("durationInTraffic", Integer.valueOf(durationInTrafficObject.getString("value")));
        directionData.put("summary", summary);
        */

        directionData = new JsonDirectionsStore(
                distanceObject.getString("text"),
                durationObject.getString("text"),
                durationInTrafficObject.getString("text"),
                summary,
                Integer.valueOf(durationObject.getString("value")),
                Integer.valueOf(durationInTrafficObject.getString("value")));

        return directionData;
    }

    @TargetApi(24)
    public static double round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        if (hasApiLevel(24)) {
            BigDecimal temp = new BigDecimal(value);

            // 5: Rounding-mode HALF_UP
            temp = temp.setScale(places, 5);
            return temp.doubleValue();
        }

        else {
            Log.v(TAG, "Rounding Approximation: Value " + value + " rounded up to two decimals.");
            return Math.round(value * 100.0) / 100.0;
        }
    }

}
