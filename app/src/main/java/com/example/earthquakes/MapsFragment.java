package com.example.earthquakes;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.earthquakes.Shared.MAP_VIEW_BUNDLE_KEY;
import static com.example.earthquakes.Shared.LONGITUDE;
import static com.example.earthquakes.Shared.LATITUDE;
import static com.example.earthquakes.Shared.MAGNITUDE_VAL;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Toolbar toolbar = ((MainActivity)getActivity()).findViewById(R.id.toolbar);

        //Enable back button in toolbar
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        //If back button in toolbar clicked go to DataFragment
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MapsFragment.this).navigate(R.id.action_MapsFragment_to_DataFragment);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(7);

        LatLng location = new LatLng(sharedPreferences.getFloat(LATITUDE, 0), sharedPreferences.getFloat(LONGITUDE, 0));
        Double circle_radius = (Math.exp(sharedPreferences.getFloat(MAGNITUDE_VAL, 0)/1.01-0.13))*100/32;

        for(int i=0; i<5; i++) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(location);
            //Scale magnitude to size of circle
            circleOptions.radius(circle_radius);
            //Translucent fill on circle
            circleOptions.fillColor(0x220000FF);
            //Red outline on circle
            circleOptions.strokeColor(Color.rgb(255, 102, 102));
            circleOptions.strokeWidth(4);
            //Add the circle and marker
            gmap.addCircle(circleOptions);
            circle_radius *= 2;
        }

        gmap.addMarker(new MarkerOptions().position(location));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}