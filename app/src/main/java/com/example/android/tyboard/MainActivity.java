package com.example.android.tyboard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.tyboard.data.JsonDirectionsStore;
import com.example.android.tyboard.data.JsonWeatherStore;
import com.example.android.tyboard.utils.DataUtils;
import com.example.android.tyboard.utils.GenUtils;
import com.example.android.tyboard.utils.NetUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.example.android.tyboard.utils.DataUtils.round;

public class MainActivity extends AppCompatActivity {

    private static final int DIRECTIONS_LOADER_ID = 0;
    private static final int WEATHER_LOADER_ID = 23;
    private int apiCalls;

    private TextView mDirectionsTextView, mDirectionsDistanceTextView, mDirectionsDurationTrafficTextView, mDirectionsDurationMinTextView;
    private TextView mWeatherTextView, mWeatherIconTextView, mWeatherTemperatureTextView, mWeatherTempMinMaxTextView;
    private BarChart mDirectionsBarChart;
    private ImageView mDirectionsImageView;
    private ProgressBar mDataLoadingProgressBar;

    private class DirectionsCallback implements LoaderCallbacks<JsonDirectionsStore> {

        private SharedPreferences sharedPref;
        private String origin, destination, gDepartureTime;

        public DirectionsCallback() {
            sharedPref = getSharedPreferences(
                    getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);

            origin = sharedPref.getString("homeAddress", "248 Louise Ln, San Mateo, 94403 CA");
            destination = sharedPref.getString("workAddress", "Brightcove, San Francisco");
            gDepartureTime = "now";
        }

        @Override
        public Loader<JsonDirectionsStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonDirectionsStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonDirectionsStore mDirectionsData = null;
                String gDirectionsKey;


                @Override
                protected void onStartLoading() {
                    gDirectionsKey = getString(R.string.com_google_android_directions_API_KEY);

                    if (mDirectionsData != null) {
                        deliverResult(mDirectionsData);
                    } else {
                        forceLoad();
                    }

                }

                /**
                 * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                 * from Google in the background.
                 *
                 * @return Directions data from Google as one String.
                 *         null if an error occurs
                 */
                @Override
                public JsonDirectionsStore loadInBackground() {

                    // Hide all existing views to load data and add count to apiCalls
                    // TODO: Needs to be checked if thread-safe solution
                    increaseApiCalls();
                    hideAllViews();
                    URL getURL = NetUtils.buildDirectionsUrl(origin, destination, gDirectionsKey, gDepartureTime);
                    try {
                        String jsonDirectionsResponse = NetUtils
                                .getResponseFromHttpUrl(getURL);

                        JsonDirectionsStore simpleJsonDirectionsData = DataUtils
                                .getDirectionsStringsFromJson(jsonDirectionsResponse);



                        return simpleJsonDirectionsData;

                    } catch (Exception e) {

                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onStopLoading() {
                }

                /**
                 * Sends the result of the load to the registered listener.
                 *
                 * @param data The result of the load
                 */
                public void deliverResult(JsonDirectionsStore data) {
                    mDirectionsData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<JsonDirectionsStore> loader, JsonDirectionsStore data) {

            // Store in SharedPreferences
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("directionsDistance", data.getDistanceString());
            editor.putString("directionsFromAddress", origin);
            editor.putString("directionsToAddress", destination);
            editor.putString("directionsSummary", data.getSummaryString());
            editor.putString("directionsPercentageOver", String.valueOf(data.getPercentageOver()));
            editor.putString("directionsToString", data.toString());
            editor.putInt("directionsDurationInTraffic", data.getDurationInTraffic());
            editor.apply();

            mDirectionsTextView.setText("From: " + origin + "\n" + "To: " + destination + "\n\n");

            // Simple image processing for binary use in bay area
            // TODO: Come up with generic solution for all US (potentially analyzing legs and showing route images of max 3 longest legs)
            String summaryString = data.getSummaryString();
            if (summaryString.contains("101")) {
                mDirectionsImageView.setImageResource(R.drawable.ic_us_101_ca_svg);
            } else if (summaryString.contains("280")) {
                mDirectionsImageView.setImageResource(R.drawable.ic_2000px_i_280_svg);
            }

            // Set color of text according to travel time
            double percentageOver = data.getPercentageOver();


            if (percentageOver < 20.0) {
                mDirectionsDurationTrafficTextView.setTextColor(
                        ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
            } else if (percentageOver >= 20.0 && percentageOver < 40.0) {
                mDirectionsDurationTrafficTextView.setTextColor(
                        ContextCompat.getColor(MainActivity.this, R.color.colorYellow));
            } else if (percentageOver >= 40.0 && percentageOver < 60.0) {
                mDirectionsDurationTrafficTextView.setTextColor(
                        ContextCompat.getColor(MainActivity.this, R.color.colorOrange));
            } else if (percentageOver >= 60.0) {
                mDirectionsDurationTrafficTextView.setTextColor(
                        ContextCompat.getColor(MainActivity.this, R.color.colorRed));
            }

            mDirectionsDurationTrafficTextView.setText(
                    String.valueOf((int) (round(data.getDurationInTraffic() / (float) 60.0 ,0))));
            mDirectionsDistanceTextView.setText(data.getDistanceString());
            mDirectionsTextView.append(data.toString());

            joinCallbacks();
        }

        @Override
        public void onLoaderReset(Loader<JsonDirectionsStore> loader) {
            joinCallbacks();
        }

    }
    private class WeatherCallback implements LoaderCallbacks<JsonWeatherStore> {
        private SharedPreferences sharedPref;

        private String destinationLat;
        private String destinationLong;
        private static final String WEATHER_FORMAT = "imperial";

        public WeatherCallback() {
            sharedPref = getSharedPreferences(
                    getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);

            destinationLat = sharedPref.getString("workLatitude", "35");
            destinationLong = sharedPref.getString("workLongitude", "139");

        }

        @Override
        public Loader<JsonWeatherStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonWeatherStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonWeatherStore mWeatherData = null;
                String oWeatherKey;

                @Override
                protected void onStartLoading() {
                    oWeatherKey = getString(R.string.com_openweather_API_KEY);

                    if (mWeatherData != null) {
                        deliverResult(mWeatherData);
                    } else {
                        forceLoad();
                    }
                }

                /**
                 * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                 * from Google in the background.
                 *
                 * @return Directions data from Google as one String.
                 *         null if an error occurs
                 */
                @Override
                public JsonWeatherStore loadInBackground() {
                    increaseApiCalls();
                    hideAllViews();

                    URL getURL = NetUtils.buildWeatherUrl(destinationLat, destinationLong, WEATHER_FORMAT, oWeatherKey);
                    try {
                        String jsonWeatherResponse = NetUtils
                                .getResponseFromHttpUrl(getURL);

                        JsonWeatherStore simpleJsonWeatherData = DataUtils
                                .getWeatherStringsFromJson(jsonWeatherResponse);

                        return simpleJsonWeatherData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onStopLoading() {
                }

                /**
                 * Sends the result of the load to the registered listener.
                 *
                 * @param data The result of the load
                 */
                public void deliverResult(JsonWeatherStore data) {
                    mWeatherData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<JsonWeatherStore> loader, JsonWeatherStore data) {
            //mWeatherTextView.setText("Weather of Lat: " + destinationLat + " Long: " + destinationLong + "\n\n");

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("weatherCondition", data.getWeatherCondition());
            editor.putString("weatherSunrise", data.getSunriseTimeString());
            editor.putString("weatherSunset", data.getSunsetTimeString());
            editor.putString("weatherTempMax", Double.toString(data.getTempMaxDouble()));
            editor.putString("weatherTempMin", Double.toString(data.getTempMinDouble()));
            editor.putString("weatherTemp", Double.toString(data.getTempDouble()));
            editor.putInt("weatherId", data.getWeatherIdInt());
            editor.putString("weatherToString", data.toString());
            editor.apply();

            String weatherFontString = "wi_owm_" + data.getWeatherIdInt();
            int weatherTypeStringId = getResources().getIdentifier(weatherFontString, "string", getPackageName());

            // Set font to display weather icons
            Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
            mWeatherIconTextView.setTypeface(iconFont);
            mWeatherIconTextView.setText(getResources().getString(weatherTypeStringId));

            // Set temperature display
            mWeatherTemperatureTextView.setText(String.valueOf(data.getTempDouble()) + "\u2109");
            mWeatherTextView.setText(data.toString());

            mWeatherTempMinMaxTextView.setText(data.getTempMinDouble() + " / " + data.getTempMaxDouble());

            // Show all views after loading
            joinCallbacks();
        }

        @Override
        public void onLoaderReset(Loader<JsonWeatherStore> loader) {
            joinCallbacks();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_item_directions) {
            Intent startSettingsActivityIntent = new Intent(this, SettingsLocationsActivity.class);
            startActivity(startSettingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if this is the first run
        GenUtils.checkFirstRun(this);
        initiateViews();
        // paintDirectionsGraph();

        // Check if the destination has changed otherwise rely on alarm
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);
        Boolean changedPreferences = sharedPrefs.getBoolean("changedPreferences", false);
        if (changedPreferences) {
            // Load data
            loadData();
            setRecurringLoad();

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("changedPreferences", false);
            editor.apply();
        } else {
            loadSharedPreferences();
        }
    }

    public void paintDirectionsGraph() {
        List<BarEntry> entries = new ArrayList<BarEntry>();

        entries.add(new BarEntry(7f, 33f));
        entries.add(new BarEntry(8f, 58f));
        entries.add(new BarEntry(9f, 72f));
        entries.add(new BarEntry(10f, 38f));
        entries.add(new BarEntry(11f, 32f));

        BarDataSet set = new BarDataSet(entries, "Company 1");
        set.setColors(ContextCompat.getColor(this, R.color.colorPrimary));

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        Description desc = new Description();
        desc.setText("");

        // Disable highlighting and selection
        mDirectionsBarChart.setPinchZoom(false);
        mDirectionsBarChart.setScaleEnabled(false);
        mDirectionsBarChart.setSelected(false);
        mDirectionsBarChart.setHighlightPerTapEnabled(false);
        mDirectionsBarChart.setHighlightFullBarEnabled(false);

        mDirectionsBarChart.setDescription(desc);
        mDirectionsBarChart.setDrawGridBackground(false);
        mDirectionsBarChart.getAxisLeft().setDrawGridLines(false);
        mDirectionsBarChart.getXAxis().setDrawGridLines(false);
        mDirectionsBarChart.getXAxis().setEnabled(false);
        mDirectionsBarChart.getAxisLeft().setEnabled(false);
        mDirectionsBarChart.getAxisRight().setEnabled(false);

        mDirectionsBarChart.getAxisLeft().setDrawLabels(false);
        mDirectionsBarChart.getAxisRight().setDrawLabels(false);
        mDirectionsBarChart.getLegend().setEnabled(false);
        mDirectionsBarChart.setData(data);
        mDirectionsBarChart.setFitBars(true); // make the x-axis fit exactly all bars
        mDirectionsBarChart.invalidate(); // refresh
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);

        // Shared Preferences for WeatherView
        String weatherCondition = sharedPref.getString("weatherCondition", "");
        String weatherSunrise = sharedPref.getString("weatherSunrise", "");
        String weatherSunset = sharedPref.getString("weatherSunset", "");
        String weatherTempMax = sharedPref.getString("weatherTempMax", "");
        String weatherTempMin = sharedPref.getString("weatherTempMin", "");
        String weatherTemp = sharedPref.getString("weatherTemp", "");
        String weatherToString = sharedPref.getString("weatherToString", "");
        int weatherId = sharedPref.getInt("weatherId", 200);


        String weatherFontString = "wi_owm_" + weatherId;
        int weatherTypeStringId = getResources().getIdentifier(weatherFontString, "string", getPackageName());

        // Set font to display weather icons
        Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        mWeatherIconTextView.setTypeface(iconFont);
        mWeatherIconTextView.setText(getResources().getString(weatherTypeStringId));

        // Set temperature display
        mWeatherTemperatureTextView.setText(weatherTemp + "\u2109");
        mWeatherTextView.setText(weatherToString);

        mWeatherTempMinMaxTextView.setText(weatherTempMin + " / " + weatherTempMax);

        // Shared Preferences for DirectionsView
        // Store in SharedPreferences
        String directionsDistance = sharedPref.getString("directionsDistance", "");
        String directionsFromAddress = sharedPref.getString("directionsFromAddress", "");
        String directionsToAddress = sharedPref.getString("directionsToAddress", "");
        String directionsSummary = sharedPref.getString("directionsSummary", "");
        String directionsPercentageOver = sharedPref.getString("directionsPercentageOver", "0.0");
        int directionsDurationInTraffic = sharedPref.getInt("directionsDurationInTraffic", 0);
        String directionsToString = sharedPref.getString("directionsToString", "");


        mDirectionsTextView.setText("From: " + directionsFromAddress + "\n" + "To: " + directionsToAddress + "\n\n");

        // Simple image processing for binary use in bay area
        // TODO: Come up with generic solution for all US (potentially analyzing legs and showing route images of max 3 longest legs)
        String summaryString = directionsSummary;
        if (summaryString.contains("101")) {
            mDirectionsImageView.setImageResource(R.drawable.ic_us_101_ca_svg);
        } else if (summaryString.contains("280")) {
            mDirectionsImageView.setImageResource(R.drawable.ic_2000px_i_280_svg);
        }

        // Set color of text according to travel time
        double percentageOver = Double.valueOf(directionsPercentageOver);


        if (percentageOver < 20.0) {
            mDirectionsDurationTrafficTextView.setTextColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
        } else if (percentageOver >= 20.0 && percentageOver < 40.0) {
            mDirectionsDurationTrafficTextView.setTextColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorYellow));
        } else if (percentageOver >= 40.0 && percentageOver < 60.0) {
            mDirectionsDurationTrafficTextView.setTextColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorOrange));
        } else if (percentageOver >= 60.0) {
            mDirectionsDurationTrafficTextView.setTextColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorRed));
        }

        mDirectionsDurationTrafficTextView.setText(
                String.valueOf((int) (round(directionsDurationInTraffic / (float) 60.0 ,0))));
        mDirectionsDistanceTextView.setText(directionsDistance);
        mDirectionsTextView.append(directionsToString);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("MAIN", "onStart Call");
    }


    /**
     * Update method that sets timer to 7.30am in the morning.
     * TODO: Needs a link to settings and automated TimeZone detection
     */
    private void setRecurringLoad() {
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        updateTime.set(Calendar.HOUR_OF_DAY, 7);
        updateTime.set(Calendar.MINUTE, 30);
        updateTime.add(Calendar.DAY_OF_MONTH, 1);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadData();
                context.unregisterReceiver(this);
            }
        };

        this.registerReceiver(receiver, new IntentFilter("com.example.android.tyboard.RELOAD"));
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.example.android.tyboard.RELOAD"), 0);
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));


        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pIntent);

        /**



        PendingIntent recurringUpdate = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarms = (AlarmManager) this.getSystemService(
                Context.ALARM_SERVICE);

        alarms.setRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringUpdate);
         */
    }

    private void loadData() {
        hideAllViews();
        int directionsLoaderId = DIRECTIONS_LOADER_ID;
        int weatherLoaderId = WEATHER_LOADER_ID;
        // After both loaders have loaded, the views will
        // be shown through joinCallbacks()
        Bundle bundleForLoader = null;
        Loader directionsLoader = getSupportLoaderManager()
                .restartLoader(directionsLoaderId, bundleForLoader, new DirectionsCallback());
        Loader weatherLoader = getSupportLoaderManager()
                .restartLoader(weatherLoaderId, bundleForLoader, new WeatherCallback());
    }

    /**
     * Helper method to load all views that
     * will be shown in main activity.
     */
    private void initiateViews() {
        mDataLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_data_loading);

        mDirectionsDurationTrafficTextView = (TextView) findViewById(R.id.tv_duration_with_traffic);
        mDirectionsDistanceTextView = (TextView) findViewById(R.id.tv_distance);
        mDirectionsImageView = (ImageView) findViewById(R.id.iv_route);
        mDirectionsTextView = (TextView) findViewById(R.id.tv_directions);
        mDirectionsDurationMinTextView = (TextView) findViewById(R.id.tv_duration_with_traffic_min);
        mDirectionsBarChart = (BarChart) findViewById(R.id.chart);

        mWeatherIconTextView = (TextView) findViewById(R.id.tv_weather_icon);
        mWeatherTemperatureTextView = (TextView) findViewById(R.id.tv_weather_temperature);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather);
        mWeatherTempMinMaxTextView = (TextView) findViewById(R.id.tv_weather_min_max);

    }

    public void joinCallbacks() {
        decreaseApiCalls();
        Log.v("MAIN", "Api Calls: " + getApiCalls());
        if (getApiCalls() == 0) showAllViews();
    }

    /**
     * Thread safe way, to determine how many API-Calls are running
     * at the same time
     * @return int value corresponding to running API-Calls
     */
    public synchronized int getApiCalls() {
        return this.apiCalls;
    }

    /**
     * Thread safe decrease of API-Calls indicator.
     */
    public synchronized void decreaseApiCalls() {
        this.apiCalls--;
        Log.v("decreaseApiCalls()", "Api Calls: " + getApiCalls());
    }

    /**
     * Thread safe increase of API-Calls indicator.
     */
    public synchronized void increaseApiCalls() {
        this.apiCalls++;
        Log.v("increaseApiCalls()", "Api Calls: " + getApiCalls());
    }

    /**
     * Hides all views and shows progress bar instead.
     */
    public void hideAllViews() {
        mDataLoadingProgressBar.setVisibility(View.VISIBLE);
        mDirectionsDurationMinTextView.setVisibility(View.INVISIBLE);
        mDirectionsTextView.setVisibility(View.INVISIBLE);
        mDirectionsDistanceTextView.setVisibility(View.INVISIBLE);
        mDirectionsDurationTrafficTextView.setVisibility(View.INVISIBLE);
        mDirectionsImageView.setVisibility(View.INVISIBLE);
        mDirectionsBarChart.setVisibility(View.INVISIBLE);

        mWeatherTextView.setVisibility(View.INVISIBLE);
        mWeatherIconTextView.setVisibility(View.INVISIBLE);
        mWeatherTemperatureTextView.setVisibility(View.INVISIBLE);
        mWeatherTempMinMaxTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows all views and hides progress bar.
     */
    public void showAllViews() {

        mDataLoadingProgressBar.setVisibility(View.INVISIBLE);

        mDirectionsDurationMinTextView.setVisibility(View.VISIBLE);
        mDirectionsTextView.setVisibility(View.VISIBLE);
        mDirectionsDistanceTextView.setVisibility(View.VISIBLE);
        mDirectionsDurationTrafficTextView.setVisibility(View.VISIBLE);
        mDirectionsImageView.setVisibility(View.VISIBLE);
        mDirectionsBarChart.setVisibility(View.VISIBLE);

        mWeatherTextView.setVisibility(View.VISIBLE);
        mWeatherIconTextView.setVisibility(View.VISIBLE);
        mWeatherTemperatureTextView.setVisibility(View.VISIBLE);
        mWeatherTempMinMaxTextView.setVisibility(View.VISIBLE);
    }
}
