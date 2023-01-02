package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://simov-6334e-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            finish();
            return;
        }
        final EditText fullname=findViewById(R.id.fullname);
        final EditText email=findViewById(R.id.email);
        final EditText phone =findViewById(R.id.phone);
        final EditText password=findViewById(R.id.password);
        final EditText conpassword=findViewById(R.id.conPassword);

        final Button registerBtn=findViewById(R.id.registerBtr);
        final TextView loginNowBtr=findViewById(R.id.loginNowBtr);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullNameTxt=fullname.getText().toString();
                final String emailTxt=email.getText().toString();
                final String phoneTxt=phone.getText().toString();
                final String passwordTxt=password.getText().toString();
                final String conpasswordTxt=conpassword.getText().toString();

                if (fullNameTxt.isEmpty()||emailTxt.isEmpty()||phoneTxt.isEmpty()||passwordTxt.isEmpty()||conpasswordTxt.isEmpty()){
                    Toast.makeText(Register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else if (!passwordTxt.equals(conpasswordTxt)){
                    Toast.makeText(Register.this, "Please password and conPassword must be the same", Toast.LENGTH_SHORT).show();
                }else if(passwordTxt.length()<7){
                    Toast.makeText(Register.this, "Password to short", Toast.LENGTH_SHORT).show();
                }else {
                    registerUser(fullNameTxt,emailTxt,phoneTxt,passwordTxt);
                }
            }
        });

        loginNowBtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void registerUser(String fullNameTxt,String emailTxt,String phoneTxt, String passwordTxt){
        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user=new User(fullNameTxt,emailTxt,phoneTxt,passwordTxt);
                            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showMainActivity();

                                        }
                                    });
                        } else {
                            Toast.makeText(Register.this,"Authentication faild",Toast.LENGTH_SHORT).show();
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