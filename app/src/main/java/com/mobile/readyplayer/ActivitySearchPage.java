package com.mobile.readyplayer;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobile.readyplayer.ui.search.FragmentSearchPage;

import java.util.List;

public class ActivitySearchPage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,  new FragmentSearchPage(), "open_search_page")
                .commit();

    }
}
