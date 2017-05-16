package com.example.android.tyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class SettingsLocationsActivity extends AppCompatActivity {

    private static final String TAG = SettingsLocationsActivity.class.getSimpleName();

    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    PlaceAutocompleteFragment mAutocompleteHomeFragment;
    PlaceAutocompleteFragment mAutocompleteWorkFragment;
    //EditText mHomeAddressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_locations);

        mAutocompleteHomeFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_home);
        // Set standard text
        mAutocompleteHomeFragment.setHint("Enter your home address");


        mAutocompleteHomeFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());
                mAutocompleteHomeFragment.setText(place.getAddress());
                //mHomeAddressEditText.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        mAutocompleteWorkFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_work);
        // Set standard text
        mAutocompleteWorkFragment.setHint("Enter your home address");


        mAutocompleteWorkFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());
                mAutocompleteWorkFragment.setText(place.getAddress());
                //mHomeAddressEditText.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
}
