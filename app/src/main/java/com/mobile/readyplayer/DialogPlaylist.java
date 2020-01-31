package com.mobile.readyplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogPlaylist extends AppCompatDialogFragment {
    private EditText editText;
    private Context context;

    public interface OnInputListener {
        void sendInPut(String input);
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playlist, null);

        builder.setView(view)
                .setTitle("New PlayList")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playListName = editText.getText().toString().trim();
                        if (!playListName.isEmpty()) {
                            mOnInputListener.sendInPut(playListName);
                        } else {
                            editText.setError("Field can't be blank!");
                        }
                    }
                });

        editText = view.findViewById(R.id.edit_text_playlist);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();

                if (text.isEmpty()) {
                    editText.setError("Field can't be blank!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return builder.create();
    }
}
