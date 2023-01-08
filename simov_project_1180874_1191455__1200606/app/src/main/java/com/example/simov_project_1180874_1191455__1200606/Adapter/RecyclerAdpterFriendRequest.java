package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simov_project_1180874_1191455__1200606.Entity.Friends;
import com.example.simov_project_1180874_1191455__1200606.Entity.FriendsStatus;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.example.simov_project_1180874_1191455__1200606.MainActivity;
import com.example.simov_project_1180874_1191455__1200606.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAdpterFriendRequest extends RecyclerView.Adapter<RecyclerAdpterFriendRequest.ViewHolder> {
    private ArrayList<Friends> arrayList;
    private Context context;
    private FirebaseUser firebaseUser;
    private String useruuid;
    private FirebaseDatabase database;
    private String emailRequester="";
    public RecyclerAdpterFriendRequest(ArrayList<Friends> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        database=FirebaseDatabase.getInstance();
        getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerAdpterFriendRequest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friendrequest,parent,false);
        return new RecyclerAdpterFriendRequest.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friends friends=arrayList.get(position);
        if (useruuid==null){
            getCurrentUser();
        }
        if (useruuid.equals(friends.getUuidUserA())){
            holder.friendNameTextViewAccept.setText(friends.getUidUseremail());
        }else{
            holder.friendNameTextViewAccept.setText(friends.getUidUseremail());

        }
        holder.acceptFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friends.setStatus(FriendsStatus.accept);
                DatabaseReference databaseReference= database.getReference("friends");
                final String[] friendsuuid = {""};
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            Friends temp=ds.getValue(Friends.class);
                            if (temp!=null){
                                if (temp.getStatus().equals(FriendsStatus.Pending)){
                                    if ((temp.getUuiduserb().equals(useruuid) && temp.getUuidUserA().equals(friends.getUuidUserA()))
                                            || (temp.getUuidUserA().equals(useruuid) && temp.getUuiduserb().equals(friends.getUuidUserA()))){
                                        friendsuuid[0] = ds.getKey();
                                        databaseReference.child(friendsuuid[0]).child("status").setValue(FriendsStatus.accept);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                showMainActivity();

            }
        });
        holder.rejectFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friends.setStatus(FriendsStatus.rejected);
                DatabaseReference databaseReference= database.getReference("friends");
                final String[] friendsuuid = {""};
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            Friends temp=ds.getValue(Friends.class);
                            if (temp!=null){
                                if (temp.getStatus().equals(FriendsStatus.Pending)){
                                    if ((temp.getUuiduserb().equals(useruuid) && temp.getUuidUserA().equals(friends.getUuidUserA()))
                                            || (temp.getUuidUserA().equals(useruuid) && temp.getUuiduserb().equals(friends.getUuidUserA()))){
                                        friendsuuid[0] = ds.getKey();
                                        databaseReference.child(friendsuuid[0]).child("status").setValue(FriendsStatus.rejected);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                showMainActivity();
            }
        });

    }

    private void getemail(String uuiduserb) {
        DatabaseReference databaseReference=database.getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.getKey().equals(uuiduserb)){
                        User user=ds.getValue(User.class);
                        if (user!=null){
                             emailRequester=user.getEmail();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView friendNameTextViewAccept;
        ImageView acceptFriendRequest;
        ImageView rejectFriendRequest;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.friendImageView122);
            friendNameTextViewAccept=itemView.findViewById(R.id.friendNameTextViewAccept);
            acceptFriendRequest=itemView.findViewById(R.id.acceptFriendRequest);
            rejectFriendRequest=itemView.findViewById(R.id.rejectFriendRequest);
        }
    }

    private void getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        useruuid = firebaseUser.getUid();
    }

    private void showMainActivity(){
        Intent intent=new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
