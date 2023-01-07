package com.example.simov_project_1180874_1191455__1200606;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.simov_project_1180874_1191455__1200606.Adapter.RecyclerAdapter;
import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
import com.example.simov_project_1180874_1191455__1200606.Entity.FriendsStatus;
import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.example.simov_project_1180874_1191455__1200606.Entity.TempShowPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FeedPost extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Post> arrayList;
    private ArrayList<String> friendsuuid;
    private RecyclerAdapter recyclerAdapter;
    private DatabaseReference databaseReferencePost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post);

        recyclerView=findViewById(R.id.recyclerView);
        arrayList=new ArrayList<>();


        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=auth.getCurrentUser();
        if (currentUser==null){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            finish();
            return;
        }

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        String uid=currentUser.getUid();
        DatabaseReference reference=database.getReference("friends");
        databaseReferencePost=database.getReference("post");
        friendsuuid=new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Friends friends=ds.getValue(Friends.class);
                    if (friends!=null){
                        if (friends.getStatus().equals(FriendsStatus.accept)){
                            if (friends.getUuiduserb().equals(uid)||friends.getUuidUserA().equals(uid)){
                                if (!friends.getUuiduserb().equals(uid)){
                                    friendsuuid.add(friends.getUuiduserb());
                                }else{
                                    friendsuuid.add(friends.getUuidUserA());
                                }
                            }
                        }
                        getPostFriends();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerAdapter=new RecyclerAdapter(arrayList,this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getPostFriends() {
        databaseReferencePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post=ds.getValue(Post.class);
                    if (post!=null){
                        if (friendsuuid.contains(post.getUuidUser())){
                            arrayList.add(post);
                        }
                    }
                }
            recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // databaseReferencePost.addValueEventListener(listener);
    }

}