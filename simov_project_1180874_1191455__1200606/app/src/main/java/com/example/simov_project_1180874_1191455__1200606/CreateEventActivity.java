package com.example.simov_project_1180874_1191455__1200606;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.simov_project_1180874_1191455__1200606.Entity.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    private final static String TAG = "CreateEventActivity";

    // Constants
    private final String EVENT = "event";
    private final String DATE_FORMAT = "%04d/%02d/%02d";
    private final String PARSE_FORMAT = "yyyy/mm/dd";
    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 10492;
    private final int STORAGE_PERMISSION_REQUEST_CODE = 10493;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 10494;
    private final int CAMERA_PICTURE_REQUEST_CODE = 10495;
    private final int LOCATION_SELECT_REQUEST_CODE = 10496;
    private final String INTENT_LOCATION_EXTRA_NAME = "location";

    // Properties
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private FusedLocationProviderClient locationClient;

    private ProgressBar progressBar;
    private ImageView imageView;
    private EditText nameInput;
    private EditText descriptionInput;
    private EditText dateInput;
    private TextView latitudeView;
    private TextView longitudeView;
    private Button currentLocationButton;
    private Button customLocationButton;
    private Button uploadButton;
    private Button createButton;

    private Uri imagePath;
    private double latitude;
    private double longitude;
    private String uuidUser;

    // Private Methods
    private void initialize() {
        Log.i(TAG, "Initializing Database and Storage...");
        databaseRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(EVENT);
        storageRef = FirebaseStorage
                .getInstance()
                .getReference();
        locationClient = LocationServices
                .getFusedLocationProviderClient(this);

        Log.i(TAG, "Loading View components into data...");
        progressBar = findViewById(R.id.createEventProgressBar);
        imageView = findViewById(R.id.createEventImage);
        nameInput = findViewById(R.id.createEventNameTextField);
        descriptionInput = findViewById(R.id.createEventDescriptionTextField);
        dateInput = findViewById(R.id.createEventDateTextField);
        latitudeView = findViewById(R.id.createEventLatitudeTextView);
        longitudeView = findViewById(R.id.createEventLongitudeTextView);
        currentLocationButton = findViewById(R.id.createEventCurrentLocationButton);
        customLocationButton = findViewById(R.id.createEventCustomLocationButton);
        uploadButton = findViewById(R.id.createEventUploadImageButton);
        createButton = findViewById(R.id.createEventButton);

        Log.i(TAG, "Initializing Location...");
        changeLocation(0, 0);
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

    private void setupHandlers() {
        Log.i(TAG, "Setting up event handlers...");
        dateInput.setOnClickListener(this::openDatePicker);
        currentLocationButton.setOnClickListener(this::requestLocation);
        customLocationButton.setOnClickListener(this::openMap);
        uploadButton.setOnClickListener(this::openImageDialog);
        createButton.setOnClickListener(this::createEvent);
    }

    // Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate - Lifecycle Event");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        authorize();
        initialize();
        setupHandlers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!validatePermission(requestCode, grantResults[1])) return;
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                openCamera();
                break;
            case STORAGE_PERMISSION_REQUEST_CODE:
                openGallery();
                break;
            case LOCATION_PERMISSION_REQUEST_CODE:
                getLocation();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast
                    .makeText(this, "An unknown error has occurred for Request Code: " + requestCode, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data == null) {
            Toast
                    .makeText(this, "No data has been found!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        switch (requestCode) {
            case PICK_IMAGE_GALLERY_CODE:
                try {
                    imagePath = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast
                            .makeText(this, "Failed to load the selected image!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case CAMERA_PICTURE_REQUEST_CODE:
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Unknown", null);
                imagePath = Uri.parse(path);
                break;
            case LOCATION_SELECT_REQUEST_CODE:
                Bundle location = data.getExtras();
                LatLng coordinates = (LatLng) location.get(INTENT_LOCATION_EXTRA_NAME);
                changeLocation(coordinates.latitude, coordinates.longitude);
                break;
            default:
                break;
        }
    }

    private boolean validatePermission(int requestCode, int result) {
        if (result != PackageManager.PERMISSION_GRANTED) {
            final String error = "Permission denied for request code: " + requestCode;
            Toast
                    .makeText(this, error, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    // Event Handlers
    private void openImageDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Image");
        builder.setMessage("Choose a photo from the Gallery or take one in the moment!");

        builder.setPositiveButton(R.string.camera_btn, (dialogInterface, i) -> {
            requestCamera();
            dialogInterface.dismiss();
        });
        builder.setNeutralButton(R.string.cancel_btn, (dialogInterface, i) ->
                dialogInterface.dismiss());
        builder.setNegativeButton(R.string.gallery_btn, (dialogInterface, i) -> {
            requestGallery();
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean requestPermission(String permission, int requestCode) {
        boolean hasPermission = ContextCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission) {
            return false;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        return true;
    }

    private void requestCamera() {
        if (requestPermission(Manifest.permission.CAMERA, CAMERA_PICTURE_REQUEST_CODE)) {
            return;
        }
        openCamera();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_PICTURE_REQUEST_CODE);
        }
    }

    private void requestGallery() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_REQUEST_CODE)) {
            return;
        }
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Upload Image"),
                PICK_IMAGE_GALLERY_CODE);
    }

    private void openDatePicker(View v) {
        Log.i(TAG, "Opening Date Picker...");
        final Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, this::setDateOnChange, currentYear, currentMonth, currentDay).show();
    }

    private void setDateOnChange(DatePicker view, int year, int month, int day) {
        dateInput.setText(String.format(DATE_FORMAT, year, month + 1, day));
    }

    private void requestLocation(View v) {
        if (requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)) {
            return;
        }
        getLocation();
    }

    private void getLocation() {
        locationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        changeLocation(0, 0);
                        final String message = "Activate Location to use this feature!";
                        Toast
                                .makeText(this, message, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    changeLocation(location.getLatitude(), location.getLongitude());
                })
                .addOnFailureListener(e -> Log.e(TAG, "An error has occurred: " + e.getMessage()));
    }

    private void openMap(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivityForResult(intent, LOCATION_SELECT_REQUEST_CODE);
    }

    private void changeLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeView.setText(String.format("Latitude: %s", latitude));
        this.longitudeView.setText(String.format("Longitude: %s", longitude));
    }

    private void createEvent(View v) {
        Log.i(TAG, "Creating Event...");
        if (imagePath == null) {
            // throw error
            return;
        }

        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(PARSE_FORMAT, Locale.ENGLISH);

            final String name = nameInput.getText().toString();
            final String description = descriptionInput.getText().toString();
            final String dateString = dateInput.getText().toString();
            final Date date = formatter.parse(dateString);

            // upload image
            progressBar.setVisibility(View.VISIBLE);
            String fsImagePath = String.format("event/%s/%s_%s",
                    uuidUser,
                    name,
                    dateString);
            StorageReference imageRef = storageRef.child(fsImagePath);

            imageRef.putFile(imagePath).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(fsUri -> {
                            Event event = new Event(name, description, date, fsUri.toString(), latitude, longitude, uuidUser);
                            databaseRef.push().setValue(event);
                            progressBar.setVisibility(View.GONE);
                            Toast
                                    .makeText(this, "Event created successfully!", Toast.LENGTH_SHORT)
                                    .show();
                            backToMain();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast
                                .makeText(this, "Event creation has failed!", Toast.LENGTH_SHORT)
                                .show();
                    });
        } catch (ParseException e) {
            Log.e(TAG, "An error has occurred!");
        }
    }

    private void backToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}