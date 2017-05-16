package com.example.android.tyboard.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by andirs on 5/15/17.
 */

public class CustomPlaceAutoCompleteFragment extends PlaceAutocompleteFragment {
    Place place;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                CustomPlaceAutoCompleteFragment.this.place = place;
            }

            @Override
            public void onError(Status status) {

            }
        });
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        super.onActivityResult(i, i1, intent);
        ((EditText) getView().findViewById(
                com.example.android.tyboard.R.id.place_autocomplete_fragment_home))
                .setText(place.getAddress());
    }
}
