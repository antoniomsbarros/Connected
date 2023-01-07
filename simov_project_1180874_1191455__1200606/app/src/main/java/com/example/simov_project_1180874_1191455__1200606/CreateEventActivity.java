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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.simov_project_1180874_1191455__1200606.Entity.Event;
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
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {
    private final static String TAG = "CreateEventActivity";

    // Constants
    private final String EVENT = "event";
    private final String DATE_FORMAT = "%04d/%02d/%02d";
    private final String PARSE_FORMAT = "yyyy/mm/dd";
    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 12345;
    private final int STORAGE_PERMISSION_REQUEST_CODE = 12945;
    private final int CAMERA_PICTURE_REQUEST_CODE = 56789;
    private final int REQUEST_FINE_LOCATION = 100;

    // Properties
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private ProgressBar progressBar;
    private ImageView imageView;
    private EditText nameInput;
    private EditText descriptionInput;
    private EditText dateInput;
    private DatePickerDialog dateDialog;
    private Button uploadButton;
    private Button createButton;

    private Uri imagePath;
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

        Log.i(TAG, "Loading View components into data...");
        progressBar = findViewById(R.id.createEventProgressBar);
        imageView = findViewById(R.id.createEventImage);
        nameInput = findViewById(R.id.createEventNameTextField);
        descriptionInput = findViewById(R.id.createEventDescriptionTextField);
        dateInput = findViewById(R.id.createEventDateTextField);
        uploadButton = findViewById(R.id.createEventUploadImageButton);
        createButton = findViewById(R.id.createEventButton);
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
        uploadButton.setOnClickListener(this::openImageDialog);
        dateInput.setOnClickListener(this::openDatePicker);
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
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast
                            .makeText(this, "Camera can not be used without permission!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                openCamera();
                break;
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast
                            .makeText(this, "Storage can not be accessed without permission!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                openGallery();
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
            default:
                break;
        }
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

        dateDialog = new DatePickerDialog(this, this::setDateOnChange, currentYear, currentMonth, currentDay);
        dateDialog.show();
    }

    private void setDateOnChange(DatePicker view, int year, int month, int day) {
        dateInput.setText(String.format(DATE_FORMAT, year, month + 1, day));
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
                            Event event = new Event(name, description, date, fsUri.toString(), null, null, uuidUser);
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
        // FIXME: return to main w/o new intent?
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}