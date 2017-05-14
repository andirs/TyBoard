package com.example.android.tyboard.data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by andirs on 5/14/17.
 */

public class LocationPreferences {
    SharedPreferences preferences;

    public LocationPreferences(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getHomeLocation() {
        return preferences.getString("homeLocation", "248 Louise Ln, San Mateo, 94403");
    }

    public void setHomeLocation(String homeLocation) {
        preferences.edit().putString("homeLocation", homeLocation);
    }

    public String getWorkLocation() {
        return preferences.getString("homeLocation", "Brightcove, San Francisco");
    }

    public void setWorkLocation(String workLocation) {
        preferences.edit().putString("workLocation", workLocation);
    }

}
