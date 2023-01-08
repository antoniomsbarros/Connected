package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simov_project_1180874_1191455__1200606.Adapter.FriendMessageAdapter;
import com.example.simov_project_1180874_1191455__1200606.Adapter.MessageAdapter;
import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
import com.example.simov_project_1180874_1191455__1200606.Entity.Message;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    TextView username;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    EditText newMessageBox;
    ImageView sendMessageICon;
    private String uuidUser;
    private String uuidFriend;
    private String userEmail;
    private MessageAdapter adapter;
    ListView recyclerView;

    private ArrayList<Message> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("user_email");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth
                .getCurrentUser();
        if (user == null) {
            Intent intent2 = new Intent(this, Login.class);
            startActivity(intent2);
            finish();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        uuidUser = user.getUid();
        getUserIdByMail();

        messages = new ArrayList<>();


        newMessageBox = findViewById(R.id.newMessageBox);
        sendMessageICon = findViewById(R.id.sendMessageIcon);
        recyclerView = findViewById(R.id.conversa);
        username = findViewById(R.id.userNameShow);

        //mDatabase.child("users").child(userId).child("username").setValue(name);
        setupAdapter();
        fetchMessages();

        sendMessageICon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                newMessageBox.setText("");
                newMessageBox.setHint("Write your message");
            }
        });
    }

    private void getUserIdByMail(){
        databaseReference
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot usersSnapshot : snapshot.getChildren()) {
                            User us = usersSnapshot.getValue(User.class);
                            if(us != null){
                                if(userEmail.equals(us.email)) {
                                    String x = us.fullname;
                                    username.setText(x);
                                   uuidFriend = (String) usersSnapshot.getKey();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void fetchMessages() {
        databaseReference
                .child("message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot messagesSnapshot : snapshot.getChildren()) {
                            Message msg = messagesSnapshot.getValue(Message.class);
                            if(msg != null){
                                if(msg.getReceiverId().equals(uuidUser) && msg.getSenderId().equals(uuidFriend) ||
                                        msg.getReceiverId().equals( uuidFriend) && msg.getSenderId().equals(uuidUser)){
                                    messages.add(msg);
                                }
                            }

                        }
                        System.out.println(messages.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void sendMessage(){
        String newMessage = newMessageBox.getText().toString();
        if(newMessage != ""){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Message toSend = new Message(uuidFriend, uuidUser, newMessage, LocalDateTime.now());
                databaseReference.child("message").push().setValue(toSend);
            }
        }


    }
    private void setupAdapter() {
        adapter = new MessageAdapter(this, messages);
        recyclerView.setAdapter(adapter);
    }
}