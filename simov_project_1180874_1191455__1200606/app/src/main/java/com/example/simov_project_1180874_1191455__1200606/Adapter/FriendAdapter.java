package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.example.simov_project_1180874_1191455__1200606.FriendsListActivity;
import com.example.simov_project_1180874_1191455__1200606.R;

import java.util.List;

public class FriendAdapter extends BaseAdapter {
    private final Context context;
    private final List<User> items;

    public FriendAdapter(Context context, final List<User> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final User row = this.items.get(i);
        View itemView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.friend_item, null);
        } else {
            itemView = view;
        }

        // Set the text of the row
        TextView nameView = itemView.findViewById(R.id.friendNameTextView);
        nameView.setText(row.fullname);

        Button unfriendButton = itemView.findViewById(R.id.unfriendButton);
        unfriendButton.setOnClickListener(v -> {
            if (!(context instanceof FriendsListActivity)) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Unfriend User");
            builder.setMessage(String.format("Are you sure you want to unfriend %s?", row.fullname));

            builder.setPositiveButton("Confirm", (dialogInterface, index) -> {
                ((FriendsListActivity) context).unfriendUser(row);
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, index) -> {
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return itemView;
    }
}
