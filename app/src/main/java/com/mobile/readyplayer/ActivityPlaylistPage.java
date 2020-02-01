package com.mobile.readyplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobile.readyplayer.ui.explorer.FragmentExplorer;
import com.mobile.readyplayer.ui.playlist.FragmentPlaylist;

public class ActivityPlaylistPage extends AppCompatActivity implements AdapterExplorerCallBack{

    private FragmentExplorer fragmentExplorer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        fragmentExplorer = new FragmentExplorer();

        openPlaylistFragment();
    }

    public void openPlaylistFragment() {
        Log.d("test", "it is here ---------------- " + fragmentExplorer);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  new FragmentPlaylist(), "open_playlist_page")
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
        fragmentExplorer.backDirectory(nameOfFile);
    }
}
