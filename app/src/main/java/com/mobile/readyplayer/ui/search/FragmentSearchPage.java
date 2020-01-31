package com.mobile.readyplayer.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.readyplayer.AdapterCallBack;
import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.AdapterSongs;

import java.util.ArrayList;

public class FragmentSearchPage extends Fragment implements AdapterCallBack, View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdapterSongs songsAdapter;

    private ArrayList<ItemSongs> listOfSongs;

    private TextView tvSearch;

    private ImageView imBtnClearSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        tvSearch = view.findViewById(R.id.tv_search);
        imBtnClearSearch = view.findViewById(R.id.btnClearSearch);
        imBtnClearSearch.setOnClickListener(this);

        findList("");
        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                findList(s.toString());
            }
        });
    }

    private void sendToRecycle() {
        songsAdapter = new AdapterSongs(listOfSongs, getContext(), this);
        recyclerView.setAdapter(songsAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void findList(String searchArg) {
        listOfSongs = new ArrayList<>();
        Cursor audioCursor = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC");

        if (audioCursor != null && audioCursor.moveToFirst()) {
            int artistColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int titleColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int pathColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String artist = audioCursor.getString(artistColumnId);
                String title = audioCursor.getString(titleColumnId);
                String path = audioCursor.getString(pathColumnId);
                long duration = audioCursor.getLong(durationColumnId);

                if (artist.toLowerCase().contains(searchArg.toLowerCase()) || title.toLowerCase().contains(searchArg.toLowerCase()))
                    listOfSongs.add(new ItemSongs(title, path, artist, duration));
            } while (audioCursor.moveToNext());
        }
        sendToRecycle();
    }

    @Override
    public void onMethodCallBack(int position) {
        Intent intent = new Intent();
        intent.putExtra("song_item", listOfSongs.get(position).getAbsolutePath());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClearSearch: {
                tvSearch.setText("");
                break;
            }
        }
    }
}