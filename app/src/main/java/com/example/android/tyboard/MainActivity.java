package com.example.android.tyboard;

import android.content.Intent;
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

import java.net.URL;

import static com.example.android.tyboard.utils.DataUtils.round;

public class MainActivity extends AppCompatActivity {

    private static final int DIRECTIONS_LOADER_ID = 0;
    private static final int WEATHER_LOADER_ID = 23;
    private int apiCalls;

    private TextView mDirectionsTextView, mDirectionsDistanceTextView, mDirectionsDurationTrafficTextView, mDirectionsDurationMinTextView;
    private TextView mWeatherTextView, mWeatherIconTextView, mWeatherTemperatureTextView;
    private ImageView mDirectionsImageView;
    private ProgressBar mDataLoadingProgressBar;

    //private static final String ORIGIN = "248 Louise Ln, San Mateo, 94403 CA";
    //private static final String DESTINATZION_ZIP = "94103,us";
    //private static final String DESTINATION = "Brightcove, San Francisco";

    private class DirectionsCallback implements LoaderCallbacks<JsonDirectionsStore> {

        private SharedPreferences sharedPref;
        private String origin, destination;
        private int apiCalls;

        public DirectionsCallback(int apiCalls) {
            sharedPref = getSharedPreferences(
                    getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);
            this.apiCalls = apiCalls;

            origin = sharedPref.getString("homeAddress", "248 Louise Ln, San Mateo, 94403 CA");
            destination = sharedPref.getString("workAddress", "Brightcove, San Francisco");
        }

        @Override
        public Loader<JsonDirectionsStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonDirectionsStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonDirectionsStore mDirectionsData = null;
                String gDirectionsKey;
                //String[] mDirectionsData = null;


                @Override
                protected void onStartLoading() {

                    // Hide all existing views to load data and add count to apiCalls
                    // TODO: Needs to be checked if thread-safe solution
                    increaseApiCalls();
                    hideAllViews();
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

                    URL getURL = NetUtils.buildDirectionsUrl(origin, destination, gDirectionsKey);
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

        }

    }
    private class WeatherCallback implements LoaderCallbacks<JsonWeatherStore> {
        private SharedPreferences sharedPref;

        private String destinationLat;
        private String destinationLong;
        private static final String DESTINATION_ZIP = "94103,us";
        private static final String WEATHER_FORMAT = "imperial";
        private int apiCalls;

        public WeatherCallback(int apiCalls) {
            sharedPref = getSharedPreferences(
                    getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);
            this.apiCalls = apiCalls;

            destinationLat = sharedPref.getString("workLatitude", "35");
            destinationLong = sharedPref.getString("workLongitude", "139");

        }

        @Override
        public Loader<JsonWeatherStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonWeatherStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonWeatherStore mWeatherData = null;
                String oWeatherKey;
                //String[] mDirectionsData = null;

                @Override
                protected void onStartLoading() {

                    increaseApiCalls();
                    hideAllViews();
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

                /*
                 * Run first trial query:
                 */
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
            mWeatherTextView.setText("Weather of Lat: " + destinationLat + " Long: " + destinationLong + "\n\n");

            String weatherFontString = "wi_owm_" + data.getWeatherIdInt();
            int weatherTypeStringId = getResources().getIdentifier(weatherFontString, "string", getPackageName());

            // Set font to display weather icons
            Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
            mWeatherIconTextView.setTypeface(iconFont);
            mWeatherIconTextView.setText(getResources().getString(weatherTypeStringId));

            // Set temperature display
            mWeatherTemperatureTextView.setText(String.valueOf(data.getTempDouble()) + "\u2109");
            mWeatherTextView.append(data.toString());

            // Show all views after loading
            joinCallbacks();
        }

        @Override
        public void onLoaderReset(Loader<JsonWeatherStore> loader) {

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

        /*
         * Hand out ID to Forecast Loader.
         * Ensuring a loader is initialized and active.
         */
        int directionsLoaderId = DIRECTIONS_LOADER_ID;
        int weatherLoaderId = WEATHER_LOADER_ID;
        int apiCalls = 0;

        initiateViews();
        hideAllViews();

        apiCalls = 2;

        // After both loaders have loaded, the views will
        // be shown through joinCallbacks()
        Bundle bundleForLoader = null;
        Loader directionsLoader = getSupportLoaderManager()
                .initLoader(directionsLoaderId, bundleForLoader, new DirectionsCallback(apiCalls));
        Loader weatherLoader = getSupportLoaderManager()
                .initLoader(weatherLoaderId, bundleForLoader, new WeatherCallback(apiCalls));


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

        mWeatherIconTextView = (TextView) findViewById(R.id.tv_weather_icon);
        mWeatherTemperatureTextView = (TextView) findViewById(R.id.tv_weather_temperature);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather);
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
    }

    /**
     * Thread safe increase of API-Calls indicator.
     */
    public synchronized void increaseApiCalls() {
        this.apiCalls++;
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
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mWeatherIconTextView.setVisibility(View.INVISIBLE);
        mWeatherTemperatureTextView.setVisibility(View.INVISIBLE);
        mDirectionsImageView.setVisibility(View.INVISIBLE);
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
        mWeatherTextView.setVisibility(View.VISIBLE);
        mWeatherIconTextView.setVisibility(View.VISIBLE);
        mWeatherTemperatureTextView.setVisibility(View.VISIBLE);
        mDirectionsImageView.setVisibility(View.VISIBLE);
    }
}
