package com.mobile.readyplayer.ui.explorer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.ui.playlist.ActivityPlaylistPage;
import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.ServiceMusic;
import com.mobile.readyplayer.base.BaseFragment;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentExplorer extends BaseFragment implements View.OnClickListener {

    private RecyclerView recyclerViewFiles;
    private AdapterExplorer adapterExplorer;
    private List<ItemExplorer> listOfFiles;
    private ArrayList<String> listOfFormats;
    private FloatingActionButton floatingActionButton;
    private CheckBox checkBoxCheckAll;

    private Button button;
    private File[] files;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options_explorer, menu);

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        setToolbar(view);
        setHasOptionsMenu(true);

        listOfFiles = new ArrayList<>();

        floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.hide();

        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"" + adapterExplorer.listOfCheckedFiles.size(), Toast.LENGTH_SHORT).show();
            }
        });

        checkBoxCheckAll = view.findViewById(R.id.chb_checkAll);
        checkBoxCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // it calls a method from adapter to check all checkboxes
                adapterExplorer.checkAllCheckBoxes(checkBoxCheckAll.isChecked());
                ((ActivityPlaylistPage)getActivity()).isCheckAllBoxOn = checkBoxCheckAll.isChecked();
            }
        });

        initializeFormats();
        recycleExplorerFiles(view);
    }

    private void initializeFormats() {
        // this is the list of media files which android can play
        listOfFormats = new ArrayList<>(Arrays.asList(
                "mp3", "wma", "wav", "mp2", "aac", "ac3", "au", "ogg", "flac"
        ));
    }

    public void showFloatingActionButton(boolean listEmpty) {
        if (!listEmpty)
            floatingActionButton.show();
        else
            floatingActionButton.hide();
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

        makeList();

        adapterExplorer = new AdapterExplorer(listOfFiles, getContext());
        recyclerViewFiles.setAdapter(adapterExplorer);
    }

    private void makeList() {
        final File dir = new File(Constants.MUTABLE_PATH);
        files = dir.listFiles();

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

        if (!listOfFiles.isEmpty()) {
            checkBoxCheckAll.setClickable(true);
        } else
            checkBoxCheckAll.setClickable(false);
    }

    private String getType(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((ActivityPlaylistPage)getActivity()).openPlaylistFragment();
        return super.onOptionsItemSelected(item);
    }

    public void outsideDirectory() {
        if (Constants.MUTABLE_PATH.equals(Constants.ROOT_PATH)) return;

        String mutablePath = Constants.MUTABLE_PATH;
        int lastIndexOfSlash = mutablePath.lastIndexOf('/');
        Constants.MUTABLE_PATH = mutablePath.substring(0, lastIndexOfSlash);

        // here title which shows the address of directory is changed by calling this method
        setTitle();

        listOfFiles.clear();
        checkBoxCheckAll.setChecked(false);

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

    public void uncheckCheckAllButtons() {
        checkBoxCheckAll.setChecked(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button: {
                addSelectedSongs();
                Log.d("test", "size of checked files before button pressed = " + ((ActivityPlaylistPage)getActivity()).listOfSelectedSongs.size());
                ((ActivityPlaylistPage)getActivity()).sendListBack();
//                ((ActivityPlaylistPage)getActivity()).openPlaylistFragment();
                break;
            }
        }
    }

    // Here this method adds all selected songs and the songs which were inside folders selected to be inside the playlist
    private void addSelectedSongs() {
        Log.d("test", "size of checked files = " + adapterExplorer.listOfCheckedFiles.size());

        for (ItemExplorer itemExplorer: adapterExplorer.listOfCheckedFiles){
             for (ItemSongs itemSongs : ((ActivityPlaylistPage) getActivity()).listOfSongs) {

                    if (!itemExplorer.getFileType().equals("dir") && itemSongs.getAbsolutePath().equals(itemExplorer.getAbsolutePath())) {
                        ((ActivityPlaylistPage) getActivity()).listOfSelectedSongs.add(itemSongs);
                    } else if (itemExplorer.getFileType().equals("dir") && itemSongs.getAbsolutePath().startsWith(itemExplorer.getAbsolutePath())) {
                        ((ActivityPlaylistPage) getActivity()).listOfSelectedSongs.add(itemSongs);
                    }

             }

             Log.d("test", "from explorer fragment = " + itemExplorer.getAbsolutePath() + "\n");
        }

        // Due to we use the object of Fragment Explorer class, it will restore the UI's state, like checkboxes.
        // If we don't set this checkbox as checked, then the checkbox appears as checked if it was checked before
        // floatActionButton was clicked
        checkBoxCheckAll.setChecked(false);
    }
}