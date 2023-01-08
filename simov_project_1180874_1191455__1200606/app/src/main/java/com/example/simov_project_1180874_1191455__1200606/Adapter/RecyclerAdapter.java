package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simov_project_1180874_1191455__1200606.CommentActivity;
import com.example.simov_project_1180874_1191455__1200606.Entity.Post;
import com.example.simov_project_1180874_1191455__1200606.FeedPost;
import com.example.simov_project_1180874_1191455__1200606.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final int ADD_COMMENT_REQUEST_CODE = 94377;
    private final int ADD_COMMENT_FAILURE_CODE = 94379;

    private ArrayList<Post> arrayList;
    private Context context;
    private FirebaseUser firebaseUser;
    private String useruuid;
    private FirebaseDatabase database;
    public RecyclerAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        database=FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new RecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post tempShowPost=arrayList.get(position);
            holder.title.setText(tempShowPost.getEmail());
            holder.message.setText(tempShowPost.getFullname());
            Glide.with(context).load(tempShowPost.getImageURL()).into(holder.postImage);
            getCurrentUser();
            if ( tempShowPost.likes!=null && tempShowPost.likes.contains(useruuid)){
                holder.like.setColorFilter(Color.parseColor("#4C6CFF"));
            }
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference databaseReference= database.getReference("post");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Post post1=ds.getValue(Post.class);
                                if(post1!=null){
                                    if (post1.getImageURL().equals(tempShowPost.getImageURL())){

                                       String uuidpost= ds.getKey();
                                        if (tempShowPost.likes==null){
                                            tempShowPost.likes=new ArrayList<>();
                                        }
                                        if (!tempShowPost.likes.contains(useruuid)){
                                            tempShowPost.likes.add(useruuid);
                                            databaseReference.child(uuidpost).setValue(tempShowPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.like.setColorFilter(Color.parseColor("#4C6CFF"));
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            holder.comment.setOnClickListener(v -> addComment(tempShowPost.getImageURL()));
    }

    private void addComment(String imageUri) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("post", imageUri);
        ((FeedPost)context).startActivityForResult(intent, ADD_COMMENT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != ADD_COMMENT_REQUEST_CODE) return;
        if (resultCode == ADD_COMMENT_FAILURE_CODE) {
            Toast.makeText(context, "An unknown error has occurred!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "Comment added!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        ImageView postImage;
        TextView title;
        TextView message;
        ImageView like;
        ImageView comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.ivProfile);
            postImage=itemView.findViewById(R.id.ivPost);
            title=itemView.findViewById(R.id.title12);
            message=itemView.findViewById(R.id.message);
            like=itemView.findViewById(R.id.ivLike);
            comment = itemView.findViewById(R.id.ivComment);
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
}
