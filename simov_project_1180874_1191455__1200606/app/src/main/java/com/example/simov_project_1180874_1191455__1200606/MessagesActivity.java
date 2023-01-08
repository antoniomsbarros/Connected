package com.example.simov_project_1180874_1191455__1200606;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.simov_project_1180874_1191455__1200606.Adapter.FriendAdapter;
import com.example.simov_project_1180874_1191455__1200606.Adapter.FriendMessageAdapter;
import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
import com.example.simov_project_1180874_1191455__1200606.Entity.FriendsStatus;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ListView listView;
    private FriendMessageAdapter adapter;

    private List<User> users;
    private Map<User, Friends> friendships;
    private String uuidUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        FirebaseUser user = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        uuidUser = user.getUid();

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listView = findViewById(R.id.friendsMessageListView);
        users = new ArrayList<>();
        friendships = new HashMap<>();

        setupAdapter();

        fetchFriends();

        //list.setOnClickListener(View.OnClickListen)



    }

    private void setupAdapter() {
        adapter = new FriendMessageAdapter(this, users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) ->
                Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show());
    }

    private void fetchFriends() {
        databaseReference
                .child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            Friends friend = friendSnapshot.getValue(Friends.class);
                            String friendUuid = friend.getFriend(uuidUser);
                            if (friendUuid != null) {
                                addUserById(friendUuid, friend);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void addUserById(String uuid, Friends friend) {
        databaseReference
                .child("users")
                .child(uuid)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    User result = task.getResult().getValue(User.class);
                    if (result == null) {
                        return;
                    }
                    if (users.contains(result)) return;
                    users.add(result);
                    friendships.put(result, friend);
                    adapter.notifyDataSetChanged();
                });
    }
}