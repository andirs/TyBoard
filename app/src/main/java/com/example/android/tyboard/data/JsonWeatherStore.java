package com.example.android.tyboard.data;

import com.example.android.tyboard.utils.GenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andirs on 5/13/17.
 */

public class JsonWeatherStore {
    private String descriptionString, iconString, pressureString;

    private double tempDouble, tempMinDouble, tempMaxDouble, windSpeedDouble;
    private int weatherIdInt, humidityInt;
    private Date sunriseDate;
    private Date sunsetDate;

    // Mock-Up JsonWeather String
    public JsonWeatherStore(String dummy) {
        tempDouble = 22.0;
        descriptionString = "Sunny";
        sunriseDate = new Date(Long.parseLong("1494695703") * 1000);
        sunsetDate = new Date(Long.parseLong("1494742503") * 1000);
    }

    public JsonWeatherStore(String weatherIdString, String descriptionString,
                            String iconString, String tempString,
                            String pressureString, String humidityString,
                            String tempMinString, String tempMaxString,
                            String windSpeedString, String sunriseString,
                            String sunsetString) {

        this.weatherIdInt = Integer.valueOf(weatherIdString);
        this.descriptionString = descriptionString;
        this.iconString = iconString;

        // Parse Strings into correct formats
        this.pressureString = pressureString;
        this.humidityInt = Integer.parseInt(humidityString);
        this.tempDouble = Double.parseDouble(tempString);
        this.tempMinDouble = Double.parseDouble(tempMinString);
        this.tempMaxDouble = Double.parseDouble(tempMaxString);
        this.windSpeedDouble = Double.parseDouble(windSpeedString);

        // Turn sunrise and sunset strings into Date Objects
        this.sunriseDate = new Date(Long.parseLong(sunriseString) * 1000);
        this.sunsetDate = new Date(Long.parseLong(sunsetString) * 1000);
    }

    public String getIconString() {
        return iconString;
    }

    public int getWeatherIdInt() {
        return weatherIdInt;
    }

    public double getTempDouble() {
        return tempDouble;
    }

    public double getTempMinDouble() {
        return tempMinDouble;
    }

    public double getTempMaxDouble() {
        return tempMaxDouble;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        return "Weather Condition: " + GenUtils.capitalizeSentence(descriptionString) + "\n"
                + "Sunrise: " + sdf.format(sunriseDate) + "\n"
                + "Sunset: " + sdf.format(sunsetDate);

    }

    public String getWeatherCondition() {
        return descriptionString;
    }

    public String getSunriseTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(sunriseDate);
    }

    public String getSunsetTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(sunsetDate);
    }
}
