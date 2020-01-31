package com.mobile.readyplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mobile.readyplayer.ui.explorer.FragmentExplorer;
import com.mobile.readyplayer.ui.playlist.FragmentPlaylist;

public class ActivityPlaylistPage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        openPlaylistFragment();
    }

    public void openPlaylistFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  new FragmentPlaylist(), "open_playlist_page")
                .commit();
    }

    public void openExplorerFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  new FragmentExplorer(), "open_explorer_page")
                .addToBackStack(null)
                .commit();
    }
}
