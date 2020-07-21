package com.mobile.readyplayer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.readyplayer.ui.search.FragmentSearchPage;

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
