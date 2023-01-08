package com.example.simov_project_1180874_1191455__1200606;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.simov_project_1180874_1191455__1200606.Adapter.CommentAdapter;
import com.example.simov_project_1180874_1191455__1200606.Entity.Comment;
import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";

    // Constants
    private final int ADD_COMMENT_SUCCESS_CODE = 94378;
    private final int ADD_COMMENT_FAILURE_CODE = 94379;

    private final String USER = "users";
    private final String POST = "post";

    // Properties
    private DatabaseReference databaseRef;

    private ListView listView;
    private CommentAdapter adapter;

    private EditText commentEditText;
    private Button postButton;

    private List<Comment> comments;
    private String imageId;
    private String userName;
    private String uuidUser;

    // Private Methods
    private void initialize() {
        Log.i(TAG, "Initializing Database and Storage...");
        databaseRef = FirebaseDatabase
                .getInstance()
                .getReference();

        Log.i(TAG, "Loading View components into data...");
        listView = findViewById(R.id.commentsListView);
        commentEditText = findViewById(R.id.commentEditText);
        postButton = findViewById(R.id.postButton);

        Log.i(TAG, "Initializing Comments list...");
        comments = new ArrayList<>();
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

    private void getUser() {
        databaseRef
                .child(USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String key = userSnapshot.getKey();
                            if (!key.equals(uuidUser)) continue;
                            User user = userSnapshot.getValue(User.class);
                            userName = user.fullname;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "An error has occurred in the database.");
                        setResult(ADD_COMMENT_FAILURE_CODE);
                        finish();
                    }
                });
    }

    private void setupAdapter() {
        adapter = new CommentAdapter(comments);
        listView.setAdapter(adapter);
    }

    private void fetchImageId() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        imageId = extras.getString(POST);
    }

    private void fetchComments() {
        databaseRef
                .child(POST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if (!post.getImageURL().equals(imageId)) continue;
                            comments.clear();
                            comments.addAll(post.getComments());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "An unknown error has occurred in the database!");
                    }
                });
    }

    private void setupHandlers() {
        postButton.setOnClickListener(this::addComment);
    }

    private void focusTextArea() {
        boolean focused = commentEditText.requestFocus();
        if (!focused) return;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void addComment(View v) {
        String text = commentEditText.getText().toString();
        Date date = new Date();

        Comment comment = new Comment(text, date, userName);
        databaseRef
                .child(POST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            String key = postSnapshot.getKey();
                            if (!post.getImageURL().equals(imageId)) continue;
                            post.addComment(comment);
                            databaseRef
                                    .child(POST)
                                    .child(key)
                                    .setValue(post)
                                    .addOnSuccessListener(unused -> {
                                        setResult(ADD_COMMENT_SUCCESS_CODE);
                                        finish();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "An error has occurred in the database.");
                        setResult(ADD_COMMENT_FAILURE_CODE);
                        finish();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate - Lifecycle Event");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        authorize();
        initialize();
        getUser();
        setupAdapter();
        fetchImageId();
        fetchComments();
        setupHandlers();
        focusTextArea();
    }
}