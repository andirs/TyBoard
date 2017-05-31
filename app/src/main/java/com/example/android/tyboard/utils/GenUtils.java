package com.example.android.tyboard.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.android.tyboard.BuildConfig;
import com.example.android.tyboard.R;
import com.example.android.tyboard.SettingsLocationsActivity;

/**
 * Created by andirs on 5/13/17.
 */

public class GenUtils {
    private static final String TAG = GenUtils.class.getSimpleName();

    /**
     * Checks if running runtime environment hits a
     * specific target standard Android API level.
     * @param level the API level to check for
     * @return true, if running device has at least the searched API level
     */
    public static boolean hasApiLevel(int level) {
        int deviceApiLevel = Build.VERSION.SDK_INT;
        if (deviceApiLevel < level)
        {
            Log.v(TAG, "Android API Version of device is low (Current device: "
                    + deviceApiLevel + " | Needed: " + level + ")");
            return false;
        }

        return true;
    }

    /**
     * Capitalizes the first letter of every String in a sentence.
     * @param input String that needs transformation
     * @return String with capitalized letters in beginning
     */
    public static String capitalizeSentence(String input) {
        String[] stringArray = input.split("\\s+");
        String output = "";
        for (String s : stringArray) {
            output += Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        return output;
    }

    /**
     * Determines, if app is running for the first time
     * or after an upgrade.
     * @param context the activity context, the app is running in
     */
    public static void checkFirstRun(Context context) {
        final String PREFS_NAME = context.getString(R.string.shared_preferences_settings_key);
        final String PREFS_KEY = context.getString(R.string.shared_preferences_settings_version);
        final int DOESNT_EXIST = -1;

        int currentVersion = BuildConfig.VERSION_CODE;

        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREFS_NAME, context.MODE_PRIVATE);

        int savedVersion = sharedPrefs.getInt(PREFS_KEY, DOESNT_EXIST);

        if (currentVersion == savedVersion) {
            return;
        } else if (savedVersion == DOESNT_EXIST) {
            // New install or user cleared shared preferences
            Intent startSettingsActivity = new Intent(context, SettingsLocationsActivity.class);
            context.startActivity(startSettingsActivity);
        } else if (currentVersion > savedVersion) {
            // TODO: Deal with updates in the future
        }

        // Update to current version
        sharedPrefs.edit().putInt(PREFS_KEY, currentVersion).apply();
    }
}
