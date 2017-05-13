package com.example.android.tyboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.tyboard.utils.JsonUtils;
import com.example.android.tyboard.utils.NetUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String[]> {

    private static final int FORECAST_LOADER_ID = 0;

    private TextView mDirectionsTextView;

    private static final String ORIGIN = "248 Louise Ln, San Mateo, 94403 CA";
    private static final String DESTINATION = "Brightcove, San Francisco";
    private static final String[] VISUAL_DICT = {"Length: ", "Normal Duration: ", "Duration now: ", "Best Route: "};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
         * Hand out ID to Forecast Loader.
         * Ensuring a loader is initialized and active.
         */
        int loaderId = FORECAST_LOADER_ID;
        LoaderCallbacks<String[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        mDirectionsTextView = (TextView) findViewById(R.id.tv_directions);



    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            // Member variable to cache directions information in
            String[] mDirectionsData = null;

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
            public String[] loadInBackground() {

                /*
                 * Run first trial query:
                 */
                String origin = "248 Louise Ln, San Mateo, 94403 CA";
                String destination = "Brightcove, San Francisco";

                URL getURL = NetUtils.buildDirectionsUrl(origin, destination);
                try {
                    String jsonDirectionsResponse = NetUtils
                            .getResponseFromHttpUrl(getURL);

                    String[] simpleJsonDirectionsData = JsonUtils
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
            public void deliverResult(String[] data) {
                mDirectionsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mDirectionsTextView.setText("From: " + ORIGIN + "\n" + "To: " + DESTINATION + "\n\n");

        for (int i = 0; i < data.length; i++) {
            mDirectionsTextView.append(VISUAL_DICT[i] + data[i] + "\n");
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
