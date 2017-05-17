package com.example.android.tyboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

public class SettingsLocationsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SettingsLocationsActivity.class.getSimpleName();

    private final static int PLACE_HOME_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final static int PLACE_WORK_AUTOCOMPLETE_REQUEST_CODE = 2;
    //PlaceAutocompleteFragment mAutocompleteHomeFragment;
    //PlaceAutocompleteFragment mAutocompleteWorkFragment;
    SharedPreferences sharedPref;
    EditText mHomeAutoCompleteEditText;
    EditText mWorkAutoCompleteEditText;
    String tmpEditTextStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_locations);

        // Set on click listener for auto complete
        mHomeAutoCompleteEditText = (EditText) findViewById(R.id.et_activity_settings_home);
        mWorkAutoCompleteEditText = (EditText) findViewById(R.id.et_activity_settings_work);
        mHomeAutoCompleteEditText.setOnClickListener(this);
        mWorkAutoCompleteEditText.setOnClickListener(this);

        sharedPref = getSharedPreferences(
                getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);
        mHomeAutoCompleteEditText
                .setText(sharedPref.getString("homeAddress", "Enter home address"));
        mWorkAutoCompleteEditText
                .setText(sharedPref.getString("workAddress", "Enter work address"));


    }


    @Override
    public void onClick(View v) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            switch(v.getId()) {
                case R.id.et_activity_settings_home:
                    tmpEditTextStorage = mHomeAutoCompleteEditText.getText().toString();
                    mHomeAutoCompleteEditText.setText("");
                    startActivityForResult(intent, PLACE_HOME_AUTOCOMPLETE_REQUEST_CODE);
                    break;
                case R.id.et_activity_settings_work:
                    tmpEditTextStorage = mWorkAutoCompleteEditText.getText().toString();
                    mWorkAutoCompleteEditText.setText("");
                    startActivityForResult(intent, PLACE_WORK_AUTOCOMPLETE_REQUEST_CODE);
                    break;
            }
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Place place = PlaceAutocomplete.getPlace(this, data);
        getSharedPreferences(
                getString(R.string.shared_preferences_settings_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case PLACE_HOME_AUTOCOMPLETE_REQUEST_CODE:
                    mHomeAutoCompleteEditText.setText(place.getAddress());
                    editor.putString("homeAddress", place.getAddress().toString());
                    Log.i(TAG, "Home Address set to: " + place.getName());
                    break;
                case PLACE_WORK_AUTOCOMPLETE_REQUEST_CODE:
                    mWorkAutoCompleteEditText.setText(place.getAddress());
                    editor.putString("workAddress", place.getAddress().toString());
                    LatLng location = place.getLatLng();
                    editor.putString("workLatitude", String.valueOf(location.latitude));
                    editor.putString("workLongitude", String.valueOf(location.longitude));
                    Log.i(TAG, "Work Address set to: " + place.getName());
                    break;
            }
            // Commit changes to shared preferences
            editor.apply();
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.i(TAG, status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
            switch(requestCode) {
                case PLACE_HOME_AUTOCOMPLETE_REQUEST_CODE:
                    mHomeAutoCompleteEditText.setText(tmpEditTextStorage);
                    break;
                case PLACE_WORK_AUTOCOMPLETE_REQUEST_CODE:
                    mWorkAutoCompleteEditText.setText(tmpEditTextStorage);
                    break;
            }
            Log.i(TAG, "User has cancelled the operation.");
        }
    }


}
