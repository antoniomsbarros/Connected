package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.simov_project_1180874_1191455__1200606.Entity.User;
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

public class MainActivity extends AppCompatActivity {

    TextView fullNameTxt,phoneTxt,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser==null){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            finish();
            return;
        }
        fullNameTxt=findViewById(R.id.tvFirstName);
        phoneTxt=findViewById(R.id.phone2);
        email=findViewById(R.id.tvEmail);
        Button btnLogout=findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        String uid=currentUser.getUid();
        DatabaseReference reference=database.getReference("users").child(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user!=null){
                    fullNameTxt.setText("Full Name: "+user.getFullname());
                    email.setText("Email: "+user.getEmail());
                    phoneTxt.setText("Phone: "+ user.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }
}