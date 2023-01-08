package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.simov_project_1180874_1191455__1200606.Entity.Comment;
import com.example.simov_project_1180874_1191455__1200606.R;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private final List<Comment> items;

    public CommentAdapter(final List<Comment> items) {
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
        final Comment row = this.items.get(i);
        View itemView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.comment_item, null);
        } else {
            itemView = view;
        }

        // Set the text of the row
        TextView userView = itemView.findViewById(R.id.commentUserTextView);
        userView.setText(row.getUser());

        TextView dateView = itemView.findViewById(R.id.commentDateTextView);
        dateView.setText(row.getDate().toString());

        TextView commentView = itemView.findViewById(R.id.commentTextView);
        commentView.setText(row.getText());

        return itemView;
    }
}
