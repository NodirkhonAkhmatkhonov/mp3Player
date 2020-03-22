package com.mobile.readyplayer.ui.playlist;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobile.readyplayer.AdapterExplorerCallBack;
import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.ui.explorer.FragmentExplorer;
import com.mobile.readyplayer.ui.playlist.FragmentPlaylist;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlaylistPage extends AppCompatActivity implements AdapterExplorerCallBack {

    private FragmentExplorer fragmentExplorer;
    private FragmentPlaylist fragmentPlaylist;

    public ArrayList<ItemSongs> listOfSongs;
    public ArrayList<ItemSongs> listOfSelectedSongs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        fragmentPlaylist = new FragmentPlaylist();
        fragmentExplorer = new FragmentExplorer();

        listOfSongs = (ArrayList<ItemSongs>) getIntent().getSerializableExtra("listOfSongs");
        listOfSelectedSongs = new ArrayList<>();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,  fragmentPlaylist, "open_playlist_page")
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

    @Override
    public void uncheckCheckAllButton() {
        fragmentExplorer.uncheckCheckAllButtons();
    }
}
