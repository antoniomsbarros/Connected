package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.example.simov_project_1180874_1191455__1200606.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mpost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mpost) {
        this.mContext = mContext;
        this.mpost = mpost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_view,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            Post post=mpost.get(position);
            Glide.with(mContext).load(post.getImageURL()).into(holder.post);
    }

    @Override
    public int getItemCount() {
        return mpost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile;
        public ImageView post;
        public ImageView like;
        public ImageView comment;

        public TextView username, publisher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.ivProfile);
            post=itemView.findViewById(R.id.ivPost);
            like=itemView.findViewById(R.id.ivLike);
            comment=itemView.findViewById(R.id.ivComment);
           // username=itemView.findViewById(R.id.title);
            publisher=itemView.findViewById(R.id.message);
        }
    }

    private void publisherInfo(ImageView image_profile, TextView username,TextView publisher,String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                username.setText(user.getEmail());
                publisher.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
