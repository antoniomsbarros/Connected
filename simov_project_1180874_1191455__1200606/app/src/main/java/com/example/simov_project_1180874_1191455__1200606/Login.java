package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://simov-6334e-default-rtdb.firebaseio.com/");
    FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        final EditText email = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final Button loginBtr = findViewById(R.id.loginBtr);
        final TextView registerNowBtr = findViewById(R.id.registerNowBtr);

        myAuth=FirebaseAuth.getInstance();
        loginBtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter your mobile or password", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(emailTxt,passwordTxt);
                }
            }
        });

        registerNowBtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void authenticateUser(String emailTxt,String passwordTxt){
        myAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMainActivity();
                        } else {
                            Toast.makeText(Login.this,"Authentication faild",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}