package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.simov_project_1180874_1191455__1200606.Adapter.FriendAdapter;
import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
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

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = "FriendsListActivity";

    private final String FRIENDS = "friends";
    private final String USERS = "users";

    // Properties
    private DatabaseReference databaseRef;

    private ListView listView;
    private FriendAdapter adapter;

    private List<User> users;
    private Map<User, Friends> friendships;
    private String uuidUser;

    // Private Methods
    private void initialize() {
        Log.i(TAG, "Initializing Database and Storage...");
        databaseRef = FirebaseDatabase
                .getInstance()
                .getReference();

        Log.i(TAG, "Loading View components into data...");
        listView = findViewById(R.id.friendsListView);

        Log.i(TAG, "Initializing list and dictionary");
        initFriends();
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

    private void initFriends() {
        users = new ArrayList<>();
        friendships = new HashMap<>();
    }

    private void fetchFriends() {
        databaseRef
                .child(FRIENDS)
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
                        Log.w(TAG, "A database error has occurred: " + error.getMessage());
                    }
                });
    }

    private void addUserById(String uuid, Friends friend) {
        Log.i(TAG, "USER UUID: " + uuid);
        databaseRef
                .child(USERS)
                .child(uuid)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "An error has occurred fetching data for UUID: " + uuid);
                        return;
                    }
                    User result = task.getResult().getValue(User.class);
                    if (result == null) {
                        Log.e(TAG, "Result is not valid for UUID: " + uuid);
                        return;
                    }
                    if (users.contains(result)) return;
                    users.add(result);
                    friendships.put(result, friend);
                    adapter.notifyDataSetChanged();
                });
    }

    private void setupAdapter() {
        adapter = new FriendAdapter(this, users);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate - Lifecycle Event");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        authorize();
        initialize();
        setupAdapter();
        fetchFriends();
    }

    public void unfriendUser(User user) {
        Friends friendship = friendships.get(user);
        databaseRef
                .child(FRIENDS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot fsSnapshot : snapshot.getChildren()) {
                            Friends dbFriendship = fsSnapshot.getValue(Friends.class);
                            if (!dbFriendship.equals(friendship)) continue;
                            Log.i(TAG, "Deleted User: " + user.fullname);
                            users.remove(user);
                            friendships.remove(user);
                            adapter.notifyDataSetChanged();
                            fsSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}