package com.mobile.readyplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DialogRemovePlaylist extends AppCompatDialogFragment {
    private Context context;

    public interface OnInputListener {
        void removePlayListItem();
    }

    public OnInputListener mOnInputListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.d("TAG", "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_playlist, null);

        builder.setTitle("Warning!")
                .setMessage("Do you want to delete this playlist?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnInputListener.removePlayListItem();
                    }
                });

        return builder.create();
    }
}
