package com.example.android.tyboard.data;

import java.util.Date;

/**
 * Created by andirs on 5/13/17.
 */

public class JsonWeatherStore {
    private String descriptionString,
            iconString, tempString,
            pressureString, humidityString,
            tempMinString, tempMaxString,
            windStringSpeed, sunriseString,
            sunsetString;

    private double tempDouble, tempMinDouble, tempMaxDouble, windSpeedDouble;
    private int pressureInt, humidityInt;
    private Date sunriseDate;
    private Date sunsetDate;

    // Mock-Up JsonWeather String
    public JsonWeatherStore(String dummy) {
        tempDouble = 22.0;
        descriptionString = "Sunny";
        sunriseDate = new Date(Long.parseLong("1494695703") * 1000);
        sunsetDate = new Date(Long.parseLong("1494742503") * 1000);
    }

    public JsonWeatherStore(String descriptionString,
                            String iconString, String tempString,
                            String pressureString, String humidityString,
                            String tempMinString, String tempMaxString,
                            String windSpeedString, String sunriseString,
                            String sunsetString) {

        this.descriptionString = descriptionString;
        this.iconString = iconString;

        // Parse Strings into correct formats
        this.pressureInt = Integer.parseInt(pressureString);
        this.humidityInt = Integer.parseInt(humidityString);
        this.tempDouble = Double.parseDouble(tempString);
        this.tempMinDouble = Double.parseDouble(tempMinString);
        this.tempMaxDouble = Double.parseDouble(tempMaxString);
        this.windSpeedDouble = Double.parseDouble(windSpeedString);

        // Turn sunrise and sunset strings into Date Objects
        this.sunriseDate = new Date(Long.parseLong(sunriseString) * 1000);
        this.sunsetDate = new Date(Long.parseLong(sunsetString) * 1000);
    }

    public String toString() {
        return "Temperature: " + tempDouble + "F \n"
                + "Description: " + descriptionString + "\n"
                + "Sunrise: " + sunriseDate.toString() + "\n"
                + "Sunset: " + sunsetDate.toString();

    }

}
