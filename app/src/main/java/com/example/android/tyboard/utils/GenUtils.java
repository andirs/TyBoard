package com.example.android.tyboard.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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

    public int getColorRessource(ContextCompat comp, Context context, double value) {
        /*
        if (value < 20.0) {
            return comp.getColor(context, context.getResources().);
        } else if (value >= 20.0 && value < 40.0) {
            return comp.getColor(context, R.color.colorYellow);
        } else if (value >= 40.0 && value < 60.0) {
            return comp.getColor(context, R.color.colorOrange);
        } else if (value >= 60.0) {
            return comp.getColor(context, R.color.colorRed);
        }
        */

        return 0;

    }

}
