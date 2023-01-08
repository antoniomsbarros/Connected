package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simov_project_1180874_1191455__1200606.Dialog.ConfirmationDialog;
import com.example.simov_project_1180874_1191455__1200606.Dialog.FingerPrintConfirmationDialog;
import com.example.simov_project_1180874_1191455__1200606.Entity.FingerPrintStatusEnum;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements FingerPrintConfirmationDialog.DialogListener {

    TextView fullNameTxt, phoneTxt, email;
    private String uid = "";
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        fullNameTxt = findViewById(R.id.tvFirstName);
        phoneTxt = findViewById(R.id.phone2);
        email = findViewById(R.id.tvEmail);
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        database = FirebaseDatabase.getInstance();
        uid = currentUser.getUid();
        DatabaseReference reference = database.getReference("users").child(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    fullNameTxt.setText("Full Name: " + user.getFullname());
                    email.setText("Email: " + user.getEmail());
                    phoneTxt.setText("Phone: " + user.getPhone());
                    if (user.getFingerPrintStatusEnum().equals(FingerPrintStatusEnum.Pending)) {
                        openDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postimages();
            }
        });
        Button addfriendbyUsername = findViewById(R.id.addfriend);
        addfriendbyUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addfriendUsername();
            }
        });

        Button feed = findViewById(R.id.feed);
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feed();
            }
        });

        Button createEvent = findViewById(R.id.createEvent);
        createEvent.setOnClickListener(this::createEvent);

        Button openMap = findViewById(R.id.openMap);
        openMap.setOnClickListener(this::openMap);

        Button openFriends = findViewById(R.id.openFriends);
        openFriends.setOnClickListener(this::openFriends);

        Button messages = findViewById((R.id.messages));
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMessage();
            }
        });
    }

    private void createMessage() {
        Intent intent=new Intent(this, MessagesActivity.class);
        startActivity(intent);
        //finish();

    }

    private void openDialog() {
        FingerPrintConfirmationDialog dialog = new FingerPrintConfirmationDialog();
        dialog.show(getSupportFragmentManager(), "Example");
    }

    private void postimages() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void addfriendUsername() {
        Intent intent = new Intent(this, AddfriendByUsername.class);
        startActivity(intent);
        //finish();
    }

    private void feed() {
        Intent intent = new Intent(this, FeedPost.class);
        startActivity(intent);
        // finish();
    }

    private void createEvent(View v) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
        finish();
    }

    private void openMap(View v) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }

    private void openFriends(View v) {
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfirmed() {
        DatabaseReference reference = database.getReference("users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.fingerPrintStatusEnum = FingerPrintStatusEnum.Accept;
                    reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("email", user.email);
                            editor.putString("password", user.password);
                            editor.putBoolean("isLogin", true);
                            editor.apply();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCancelled() {
        DatabaseReference reference = database.getReference("users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.fingerPrintStatusEnum = FingerPrintStatusEnum.Reject;
                    reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("olaola");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}