package com.example.android.tyboard.data;

/**
 * Created by andirs on 5/12/17.
 */

public class JsonStore {
    private String distance, durationString, durationInTrafficString, summaryString;
    private int duration, durationInTraffic;

    public JsonStore(String distance,
                     String durationString,
                     String durationInTrafficString,
                     String summaryString,
                     int duration,
                     int durationInTraffic) {
        this.distance = distance;
        this.durationString = durationString;
        this.durationInTrafficString = durationInTrafficString;
        this.summaryString = summaryString;
        this.duration = duration;
        this.durationInTraffic = durationInTraffic;
    }

    public String toString() {

        float percentageOver = (((durationInTraffic / (float) duration) - 1) * 100);
        String output = "";

        output += "Distance: " + distance + "\n"
                + "Current Duration: " + durationInTrafficString
                + " (" + String.valueOf(percentageOver) + "% )" + "\n"
                + "Duration without traffic: " + durationString + "\n"
                + "Route Recommendation: " + summaryString;

        return output;
    }

}
