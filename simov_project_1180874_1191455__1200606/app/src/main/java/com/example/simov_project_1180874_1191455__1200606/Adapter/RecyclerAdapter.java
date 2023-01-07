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
import com.example.simov_project_1180874_1191455__1200606.Entity.TempShowPost;
import com.example.simov_project_1180874_1191455__1200606.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Post> arrayList;
    private Context context;

    public RecyclerAdapter(ArrayList<Post> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.ivProfile);
            postImage=itemView.findViewById(R.id.ivPost);
            title=itemView.findViewById(R.id.title12);
            message=itemView.findViewById(R.id.message);

        }
    }

}
