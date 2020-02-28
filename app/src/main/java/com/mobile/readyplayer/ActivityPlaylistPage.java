package com.mobile.readyplayer;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobile.readyplayer.ui.explorer.FragmentExplorer;
import com.mobile.readyplayer.ui.playlist.FragmentPlaylist;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlaylistPage extends AppCompatActivity implements AdapterExplorerCallBack{

    private FragmentExplorer fragmentExplorer;
    public ArrayList<ItemSongs> listOfSongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        fragmentExplorer = new FragmentExplorer();
        listOfSongs = (ArrayList<ItemSongs>) getIntent().getSerializableExtra("listOfSongs");

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  fragmentExplorer, "open_playlist_page")
                .commit();
    }

    public void openPlaylistFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,  new FragmentPlaylist(), "open_playlist_page")
                .commit();
    }

    public void openExplorerFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  fragmentExplorer, "open_explorer_page")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMethodCallBack(String nameOfFile) {
        fragmentExplorer.insideDirectory(nameOfFile);
    }

    @Override
    public void showFloatingActionButton(boolean appear) {
        fragmentExplorer.showFloatingActionButton(appear);
    }
}
