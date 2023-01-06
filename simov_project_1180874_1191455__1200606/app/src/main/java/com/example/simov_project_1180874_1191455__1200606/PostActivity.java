package com.example.simov_project_1180874_1191455__1200606;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private Button selectButton;
    private Button uploadButton;
    private Button Buttonlocation;
    private Button buttonGoback;
    private ImageView imagePreview;
    private ProgressBar progressBar;
    private TextInputLayout country;
    private TextInputLayout city;
    private TextInputLayout street;

    private String latidadetxt="";
    private String longitudetxt="";
    private String useruuid="";
    private String countryTxt="";
    private String citytxt="";
    private String streettxt="";

    private Uri filePath=null;
    private  StorageReference storageReference =null;
    private DatabaseReference databaseReference=null;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final int PICK_IMAGE_GALLERY_CODE=78;
    private final int CAMERA_PERMISSION_REQUEST_CODE=12345;
    private final int CAMERA_PICTURE_REQUEST_CODE=56789;
    private final int REQUEST_FINE_lOCATION=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        databaseReference=database.getReference().child("post");

        selectButton=findViewById(R.id.selectButton);
        uploadButton=findViewById(R.id.uploadButton);
        imagePreview=findViewById(R.id.imagePreview);
        Buttonlocation=findViewById(R.id.GetCurrentLo);
        progressBar=findViewById(R.id.progressBar);
        country=findViewById(R.id.Location);
        city=findViewById(R.id.City);
        street=findViewById(R.id.Street);
        buttonGoback=findViewById(R.id.goBack);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser==null){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            finish();
            return;
        }
        useruuid=currentUser.getUid();

        Buttonlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetLastLocation();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSelectedDialog();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        buttonGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gobackMain();
            }
        });
    }

    private void GetLastLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        Geocoder geocoder=new Geocoder(PostActivity.this, Locale.getDefault());
                        List<Address>addressList= null;
                        try {
                            addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            latidadetxt=String.valueOf(addressList.get(0).getLatitude());
                            longitudetxt=String.valueOf(addressList.get(0).getLongitude());
                            countryTxt=String.valueOf(addressList.get(0).getCountryName());
                            citytxt=String.valueOf(addressList.get(0).getLocality());
                            streettxt=String.valueOf(addressList.get(0).getAddressLine(0));
                            country.getEditText().setText(countryTxt);
                            if (!citytxt.equals("null")){
                                city.getEditText().setText(citytxt);
                            }else{
                                city.getEditText().setText("City not available");
                            }
                            street.getEditText().setText(streettxt);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else{
            getPermission();
        }
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(PostActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_lOCATION);
    }


    private void uploadImage() {
        if (filePath!=null){
            getLocationfrominout();
            progressBar.setVisibility(View.VISIBLE);
            StorageReference ref=storageReference.child("post/"+useruuid+"/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Post post=new Post(uri.toString(),useruuid,latidadetxt,longitudetxt);
                            databaseReference.push().setValue(post);
                            Toast.makeText(PostActivity.this,"Image uploaded successful",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            gobackMain();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostActivity.this,"Image uploaded failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getLocationfrominout() {
        Geocoder geocoder=new Geocoder(this);
        List<Address> addressList;
        try {
            addressList=geocoder.getFromLocationName(street.getEditText().getText().toString(),1);
            if (addressList!=null){
                latidadetxt=String.valueOf(addressList.get(0).getLatitude());
                longitudetxt=String.valueOf(addressList.get(0).getLongitude());
            }else {
                Toast.makeText(PostActivity.this,"Location Not found",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImageSelectedDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setMessage("Please select an option");

        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ckeckCameraPermission();
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectFromGallery();
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void selectFromGallery(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==PICK_IMAGE_GALLERY_CODE && resultCode== Activity.RESULT_OK){
            if (data==null|| data.getData()==null){
                return;
            }
            try {
                filePath= data.getData();
                Bitmap bitmap= null;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imagePreview.setImageBitmap(bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode==CAMERA_PICTURE_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            Bundle extras= data.getExtras();
            Bitmap bitmap= (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(bitmap);
            filePath=getImageUri(getApplicationContext(),bitmap);
        }
    }
    private void ckeckCameraPermission(){
        if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(PostActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostActivity.this,new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },CAMERA_PERMISSION_REQUEST_CODE);
        }else{
            openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults[1]==PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
        }else if(requestCode==REQUEST_FINE_lOCATION){
            if (grantResults[1]==PackageManager.PERMISSION_GRANTED){
                GetLastLocation();
            }
        }


    }

    private void openCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,CAMERA_PICTURE_REQUEST_CODE );
        }

    }
    private Uri getImageUri(Context context,Bitmap bitmap){
        ByteArrayOutputStream byteArrayInputStream=new ByteArrayOutputStream ();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayInputStream);
        String path=MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"title",null);
        return  Uri.parse(path);
    }
private void gobackMain(){
    Intent intent=new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
}
}