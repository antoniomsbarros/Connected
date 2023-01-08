package com.example.simov_project_1180874_1191455__1200606.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simov_project_1180874_1191455__1200606.Entity.Message;
import com.example.simov_project_1180874_1191455__1200606.Entity.User;
import com.example.simov_project_1180874_1191455__1200606.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private ArrayList<Message> messages;


    public MessageAdapter(Context context, final List<Message> messages) {
        this.context = context;
        this.messages = (ArrayList<Message>) messages;
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Message row = this.messages.get(i);
        View itemView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.receive_msg, null);
        } else {
            itemView = view;
        }

        TextView textview = itemView.findViewById(R.id.messageBox);
        textview.setText(row.getMessage());
        TextView userview = itemView.findViewById(R.id.userNameDisplay);
        userview.setText(row.getSenderId());
        return itemView;
    }
}
        /*

    private String senderRoom;
    private String receiverRoom;


         */