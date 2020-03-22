package com.mobile.readyplayer.ui.playlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaylist extends BaseFragment {

    private ImageButton button;
    private RecyclerView recyclerView;
    private AdapterPlayListSong adapterPlayListSong;
    private List<ItemSongs> listOfSongs;


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        button = view.findViewById(R.id.addAudio);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityPlaylistPage)getActivity()).openExplorerFragment();
            }
        });
        
        initRecycler(view);
    }

    private void initRecycler(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        listOfSongs = new ArrayList<>();
        listOfSongs = ((ActivityPlaylistPage)getActivity()).listOfSelectedSongs;
        adapterPlayListSong = new AdapterPlayListSong(listOfSongs, getContext());
        recyclerView.setAdapter(adapterPlayListSong);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_playlist;
    }
}
