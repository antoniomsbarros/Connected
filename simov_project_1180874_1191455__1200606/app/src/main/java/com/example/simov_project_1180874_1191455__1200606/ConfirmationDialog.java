package com.example.simov_project_1180874_1191455__1200606;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ConfirmationDialog extends AppCompatDialogFragment {
    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Send Friend Request")
                .setMessage("Do you want to send a friend request?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onCancelled();
            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onConfirmed();
            }
        });
        return builder.create();
        //return super.onCreateDialog(savedInstanceState);
    }
    public interface DialogListener{
        void onConfirmed();
        void onCancelled();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(DialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement DialogListener");
        }

    }
}
