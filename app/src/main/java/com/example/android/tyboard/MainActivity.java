package com.example.android.tyboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.tyboard.data.JsonDirectionsStore;
import com.example.android.tyboard.data.JsonWeatherStore;
import com.example.android.tyboard.utils.DataUtils;
import com.example.android.tyboard.utils.NetUtils;

import java.net.URL;

import static com.example.android.tyboard.utils.DataUtils.round;

public class MainActivity extends AppCompatActivity {

    private static final int DIRECTIONS_LOADER_ID = 0;
    private static final int WEATHER_LOADER_ID = 23;

    private TextView mDirectionsTextView, mDirectionsDistanceTextView, mDirectionsDurationTrafficTextView;
    private TextView mWeatherTextView, mWeatherIconTextView;
    private ImageView mDirectionsImageView;

    //private static final String ORIGIN = "248 Louise Ln, San Mateo, 94403 CA";
    //private static final String DESTINATZION_ZIP = "94103,us";
    //private static final String DESTINATION = "Brightcove, San Francisco";

    private class DirectionsCallback implements LoaderCallbacks<JsonDirectionsStore> {
        private static final String ORIGIN = "248 Louise Ln, San Mateo, 94403 CA";
        private static final String DESTINATION = "Brightcove, San Francisco";

        @Override
        public Loader<JsonDirectionsStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonDirectionsStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonDirectionsStore mDirectionsData = null;
                //String[] mDirectionsData = null;

                @Override
                protected void onStartLoading() {
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

                /*
                 * Run first trial query:
                 */
                    String origin = "248 Louise Ln, San Mateo, 94403 CA";
                    String destination = "Brightcove, San Francisco";

                    URL getURL = NetUtils.buildDirectionsUrl(origin, destination);
                    try {
                        String jsonDirectionsResponse = NetUtils
                                .getResponseFromHttpUrl(getURL);

                        JsonDirectionsStore simpleJsonDirectionsData = DataUtils
                                .getDirectionsStringsFromJson(MainActivity.this, jsonDirectionsResponse);

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
            mDirectionsTextView.setText("From: " + ORIGIN + "\n" + "To: " + DESTINATION + "\n\n");

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
                mDirectionsDurationTrafficTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
            } else if (percentageOver >= 20.0 && percentageOver < 40.0) {
                mDirectionsDurationTrafficTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorYellow));
            } else if (percentageOver >= 40.0 && percentageOver < 60.0) {
                mDirectionsDurationTrafficTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorOrange));
            } else if (percentageOver >= 60.0) {
                mDirectionsDurationTrafficTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorRed));
            }

            mDirectionsDurationTrafficTextView.setText(String.valueOf((int) (round(data.getDurationInTraffic() / (float) 60.0 ,0))));
            mDirectionsDistanceTextView.setText(data.getDistanceString());
            mDirectionsTextView.append(data.toString());
        }

        @Override
        public void onLoaderReset(Loader<JsonDirectionsStore> loader) {

        }

    }

    private class WeatherCallback implements LoaderCallbacks<JsonWeatherStore> {
        private static final String DESTINATION_ZIP = "94103,us";
        private static final String WEATHER_FORMAT = "imperial";

        @Override
        public Loader<JsonWeatherStore> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<JsonWeatherStore>(MainActivity.this) {
                // Member variable to cache directions information in
                JsonWeatherStore mWeatherData = null;
                //String[] mDirectionsData = null;

                @Override
                protected void onStartLoading() {
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
                    URL getURL = NetUtils.buildWeatherUrl(DESTINATION_ZIP, WEATHER_FORMAT);
                    try {
                        String jsonWeatherResponse = NetUtils
                                .getResponseFromHttpUrl(getURL);

                        JsonWeatherStore simpleJsonWeatherData = DataUtils
                                .getWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

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
            mWeatherTextView.setText("Weather of: " + DESTINATION_ZIP + "\n\n");

            String weatherFontString = "wi_owm_" + data.getWeatherIdInt();
            int weatherTypeStringId = getResources().getIdentifier(weatherFontString, "string", getPackageName());

            // Set font to display weather icons
            Typeface iconFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
            mWeatherIconTextView.setTypeface(iconFont);
            mWeatherIconTextView.setText(getResources().getString(weatherTypeStringId));
            mWeatherTextView.append(data.toString());
        }

        @Override
        public void onLoaderReset(Loader<JsonWeatherStore> loader) {

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Hand out ID to Forecast Loader.
         * Ensuring a loader is initialized and active.
         */
        int directionsLoaderId = DIRECTIONS_LOADER_ID;
        int weatherLoaderId = WEATHER_LOADER_ID;

        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(directionsLoaderId, bundleForLoader, new DirectionsCallback());
        getSupportLoaderManager().initLoader(weatherLoaderId, bundleForLoader, new WeatherCallback());

        mDirectionsDurationTrafficTextView = (TextView) findViewById(R.id.tv_duration_with_traffic);
        mDirectionsDistanceTextView = (TextView) findViewById(R.id.tv_distance);
        mDirectionsImageView = (ImageView) findViewById(R.id.iv_route);
        mDirectionsTextView = (TextView) findViewById(R.id.tv_directions);

        mWeatherIconTextView = (TextView) findViewById(R.id.tv_weather_icon);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather);

    }
}
