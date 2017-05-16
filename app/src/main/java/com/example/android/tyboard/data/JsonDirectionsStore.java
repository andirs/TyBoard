package com.example.android.tyboard.data;

import static com.example.android.tyboard.utils.DataUtils.round;

/**
 * Created by andirs on 5/12/17.
 */

public class JsonDirectionsStore {
    private String distanceString, durationString, durationInTrafficString,
            summaryString, endLatString, endLongString;
    private int durationInt, durationInTrafficInt;
    private double percentageOver;

    public JsonDirectionsStore(String distanceString,
                               String durationString,
                               String durationInTrafficString,
                               String summaryString,
                               int durationInt,
                               int durationInTrafficInt) {
        this.distanceString = distanceString;
        this.durationString = durationString;
        this.durationInTrafficString = durationInTrafficString;
        this.summaryString = summaryString;
        this.durationInt = durationInt;
        this.durationInTrafficInt = durationInTrafficInt;
        this.percentageOver = round((((durationInTrafficInt / (float) durationInt) - 1) * 100), 2);
    }

    public String getDistanceString() {
        return distanceString;
    }

    public String getDurationString() {
        return durationString;
    }

    public String getDurationInTrafficString() {
        return durationInTrafficString;
    }

    public String getSummaryString() {
        return summaryString;
    }

    public int getDuration() {
        return durationInt;
    }

    public int getDurationInTraffic() {
        return durationInTrafficInt;
    }

    public double getPercentageOver() {
        return percentageOver;
    }

    public String getEndLatString() { return endLatString; }

    public String getEndLongString() { return endLongString; }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public void setDurationInTrafficString(String durationInTrafficString) {
        this.durationInTrafficString = durationInTrafficString;
    }

    public void setSummaryString(String summaryString) {
        this.summaryString = summaryString;
    }

    public void setDuration(int durationInt) {
        this.durationInt = durationInt;
    }

    public void setDurationInTraffic(int durationInTrafficInt) {
        this.durationInTrafficInt = durationInTrafficInt;
    }

    public String toString() {

        String output = "";

        output += "Distance: " + distanceString + "\n"
                + "Current Duration: " + durationInTrafficString
                + " (" + String.valueOf(percentageOver) + "%)" + "\n"
                + "Duration without traffic: " + durationString + "\n"
                + "Route Recommendation: " + summaryString;

        return output;
    }

}
