package com.example.simov_project_1180874_1191455__1200606;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.simov_project_1180874_1191455__1200606.databinding.ActivityLocationBinding;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int LOCATION_SELECT_REQUEST_CODE = 10496;
    private final String INTENT_LOCATION_EXTRA_NAME = "location";

    private GoogleMap mMap;
    private ActivityLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       setupMap();
       setupEventHandler();
    }

    private void setupMap() {
        float zoom = 6.0f;
        LatLng portugal = new LatLng(38.736946, -9.142685);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portugal, zoom));
    }

    private void setupEventHandler() {
        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions().position(latLng));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Event - Location");
            builder.setMessage("Are you sure this is the event location?");

            builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.putExtra(INTENT_LOCATION_EXTRA_NAME, latLng);
                setResult(RESULT_OK, intent);
                dialogInterface.dismiss();
                finish();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                mMap.clear();
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}