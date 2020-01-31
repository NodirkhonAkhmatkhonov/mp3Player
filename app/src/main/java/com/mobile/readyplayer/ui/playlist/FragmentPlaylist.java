package com.mobile.readyplayer.ui.playlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mobile.readyplayer.ActivityPlaylistPage;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.base.BaseFragment;

public class FragmentPlaylist extends BaseFragment {

    private Button button;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        button = view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityPlaylistPage)getActivity()).openExplorerFragment();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_playlist;
    }
}
