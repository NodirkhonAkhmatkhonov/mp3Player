package com.mobile.readyplayer.ui.explorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobile.readyplayer.ActivityPlaylistPage;
import com.mobile.readyplayer.AdapterExplorer;
import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.base.BaseFragment;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentExplorer extends BaseFragment {

    private RecyclerView recyclerViewFiles;
    private AdapterExplorer adapterExplorer;
    private List<ItemExplorer> listOfFiles;
    private ArrayList<String> listOfFormats;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options_explorer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((ActivityPlaylistPage)getActivity()).openPlaylistFragment();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        setToolbar(view);
        setHasOptionsMenu(true);
        initializeFormats();
        recycleExplorerFiles(view);
    }

    private void initializeFormats() {
        listOfFormats = new ArrayList<>(Arrays.asList(
                "mp3", "wma", "wav", "mp2", "aac", "ac3", "au", "ogg", "flac"
        ));
    }

    private void setToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarPlaylist);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideDirectory();
            }
        });
    }

    private void setTitle() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Constants.MUTABLE_PATH);
    }

    private void recycleExplorerFiles(View view) {
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles);
        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFiles.setHasFixedSize(true);

        listOfFiles = new ArrayList<>();

        makeList();

        adapterExplorer = new AdapterExplorer(listOfFiles, getContext());
        recyclerViewFiles.setAdapter(adapterExplorer);

        ///////////////////////////
        adapterExplorer.notifyDataSetChanged();
    }

    private void makeList() {
        final File dir = new File(Constants.MUTABLE_PATH);
        final File[] files = dir.listFiles();

        for (File file: files) {
            if (!file.isHidden()) {
                String fileType;

                if (file.isDirectory()) {
                    fileType = "dir";
                } else fileType = getType(file.getPath());

                if (file.isDirectory() || listOfFormats.contains(fileType))
                listOfFiles.add(new ItemExplorer(fileType, file.getName(), file.getAbsolutePath()));
            }
        }
    }

    private String getType(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public void outsideDirectory() {
        if (Constants.MUTABLE_PATH.equals(Constants.ROOT_PATH)) return;

        String mutablePath = Constants.MUTABLE_PATH;
        int lastIndexOfSlash = mutablePath.lastIndexOf('/');
        Constants.MUTABLE_PATH = mutablePath.substring(0, lastIndexOfSlash);

        // here title which shows the address of directory is changed by calling this method
        setTitle();

        listOfFiles.clear();
        makeList();
        adapterExplorer.notifyDataSetChanged();
    }

    public void insideDirectory(String nameOfFile) {
        listOfFiles.clear();

        Constants.MUTABLE_PATH += "/" + nameOfFile;

        setTitle();

        makeList();
        adapterExplorer.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explorer;
    }
}