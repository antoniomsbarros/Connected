package com.example.simov_project_1180874_1191455__1200606;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.simov_project_1180874_1191455__1200606.Entity.Event;
import com.example.simov_project_1180874_1191455__1200606.Entity.MapCategory;
import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.example.simov_project_1180874_1191455__1200606.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final MapCategory DEFAULT_CATEGORY = MapCategory.POST;

    private GoogleMap mMap;
    private ActivityMapBinding binding;

    // Properties
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private Button eventButton;
    private Button postButton;
    private Button userButton;

    private String categoryName;
    private MapCategory category;
    private ValueEventListener eventListener;

    private String uuidUser;

    // Private Methods
    private void initialize() {
        Log.i(TAG, "Initializing Database and Storage...");
        databaseRef = FirebaseDatabase
                .getInstance()
                .getReference();
        storageRef = FirebaseStorage
                .getInstance()
                .getReference();

        eventButton = findViewById(R.id.viewEventsButton);
        postButton = findViewById(R.id.viewPostsButton);
        userButton = findViewById(R.id.viewUsersButton);
    }

    private void authorize() {
        Log.i(TAG, "Authorizing User...");
        FirebaseUser user = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        if (user == null) {
            Log.w(TAG, "User is not authorized to access the application. Redirecting...");
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        Log.i(TAG, "User is authorized to access the application.");
        uuidUser = user.getUid();
    }

    private void setupButtons() {
        eventButton.setOnClickListener(v -> changeCategory(MapCategory.EVENT));
        postButton.setOnClickListener(v -> changeCategory(MapCategory.POST));
        userButton.setOnClickListener(v -> changeCategory(MapCategory.USER));
    }

    private void removeEventListener() {
        if (eventListener == null) return;
        databaseRef
                .child(categoryName)
                .removeEventListener(eventListener);
    }

    private void addEventListener() {
        Log.i(TAG, "Setting up Event Listener for: " + categoryName);
        eventListener = databaseRef
                .child(categoryName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mMap.clear();
                        for (DataSnapshot entitySnapshot : snapshot.getChildren()) {
                            mMap.addMarker(transform(entitySnapshot));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "A database error has occurred: " + error.getMessage());
                        mMap.clear();
                    }
                });
    }

    private MarkerOptions transform(DataSnapshot snapshot) {
        switch (category) {
            case EVENT:
                return transformEvent(snapshot);
            case POST:
                return transformPost(snapshot);
            case USER:
                return transformUser(snapshot);
            default:
                throw new IllegalArgumentException("Unknown entity name.");
        }
    }

    private MarkerOptions transformPost(DataSnapshot postSnapshot) {
        Post post = postSnapshot.getValue(Post.class);
        String title = post.getFullname();
        double latitude = Double.parseDouble(post.getLatitude());
        double longitude = Double.parseDouble(post.getLongitude());

        return buildMarker(title, latitude, longitude);
    }

    private MarkerOptions transformEvent(DataSnapshot eventSnapshot) {
        Event event = eventSnapshot.getValue(Event.class);
        String title = event.getName();
        double latitude = event.getLatitude();
        double longitude = event.getLongitude();

        return buildMarker(title, latitude, longitude);
    }

    private MarkerOptions transformUser(DataSnapshot userSnapshot) {
        User user = userSnapshot.getValue(User.class);
        String title = user.getFullname();
        double latitude = 0;
        double longitude = 0;

        return buildMarker(title, latitude, longitude);
    }

    private MarkerOptions buildMarker(String title, double latitude, double longitude) {
        LatLng coordinates = new LatLng(latitude, longitude);
        return new MarkerOptions()
                .position(coordinates)
                .title(title);
    }

    private void changeCategory(MapCategory category) {
        removeEventListener();
        this.category = category;
        this.categoryName = category
                .name()
                .toLowerCase();
        addEventListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate - Lifecycle method");
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authorize();
        initialize();
        setupButtons();

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.330421199999996, -8.4737648), 14.0f));
        changeCategory(DEFAULT_CATEGORY);
    }
}