package com.example.android.tyboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.tyboard.data.JsonStore;
import com.example.android.tyboard.utils.JsonUtils;
import com.example.android.tyboard.utils.NetUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<JsonStore> {

    private static final int FORECAST_LOADER_ID = 0;

    private TextView mDirectionsTextView;

    private static final String ORIGIN = "248 Louise Ln, San Mateo, 94403 CA";
    private static final String DESTINATION = "Brightcove, San Francisco";
    private static final String[] VISUAL_DICT = {"distanceString", "durationString", "durationInTrafficString", "summary"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Hand out ID to Forecast Loader.
         * Ensuring a loader is initialized and active.
         */
        int loaderId = FORECAST_LOADER_ID;
        LoaderCallbacks<JsonStore> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        mDirectionsTextView = (TextView) findViewById(R.id.tv_directions);

    }

    @Override
    public Loader<JsonStore> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<JsonStore>(this) {
            // Member variable to cache directions information in
            JsonStore mDirectionsData = null;
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
            public JsonStore loadInBackground() {

                /*
                 * Run first trial query:
                 */
                String origin = "248 Louise Ln, San Mateo, 94403 CA";
                String destination = "Brightcove, San Francisco";

                URL getURL = NetUtils.buildDirectionsUrl(origin, destination);
                try {
                    String jsonDirectionsResponse = NetUtils
                            .getResponseFromHttpUrl(getURL);

                    JsonStore simpleJsonDirectionsData = JsonUtils
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
            public void deliverResult(JsonStore data) {
                mDirectionsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<JsonStore> loader, JsonStore data) {
        mDirectionsTextView.setText("From: " + ORIGIN + "\n" + "To: " + DESTINATION + "\n\n");

        mDirectionsTextView.append(data.toString());
    }

    @Override
    public void onLoaderReset(Loader<JsonStore> loader) {

    }
}
