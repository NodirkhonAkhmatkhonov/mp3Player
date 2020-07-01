package com.mobile.readyplayer.ui.playlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mobile.readyplayer.AdapterExplorerCallBack;
import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.base.BaseActivity;
import com.mobile.readyplayer.ui.ItemExplorer;
import com.mobile.readyplayer.ui.explorer.AdapterExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityPlaylistPage extends BaseActivity implements AdapterExplorerCallBack, View.OnClickListener {

    public ArrayList<ItemSongs> listOfSongs;
    public ArrayList<ItemSongs> listOfSelectedSongs;
    public ArrayList<ItemSongs> listOfFinalSelectedSongs;
    private ArrayList<String> listOfFormats;
    private List<ItemExplorer> listOfFiles;

    public boolean isCheckAllBoxOn;

    private FloatingActionButton floatingActionButton;
    private CheckBox checkBoxCheckAll;

    private RecyclerView recyclerViewFiles;
    private AdapterExplorer adapterExplorer;

    private Button button;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_explorer, menu);
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        setToolbar();
        listOfSongs = (ArrayList<ItemSongs>) getIntent().getSerializableExtra("listOfSongs");

        listOfSelectedSongs = new ArrayList<>();
        listOfFiles = new ArrayList<>();
        listOfFinalSelectedSongs = new ArrayList<>();

        floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        checkBoxCheckAll = findViewById(R.id.chb_checkAll);
        checkBoxCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxAllCheckbox();
            }
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityPlaylistPage.this, "" + adapterExplorer.listOfSelected.size(), Toast.LENGTH_SHORT).show();
            }
        });
        initializeFormats();
        recycleExplorerFiles();
    }

    private void onCheckBoxAllCheckbox() {
        // activity dagi method
        adapterExplorer.isCheckAll = true;
        adapterExplorer.checkAllCheckboxes(checkBoxCheckAll.isChecked());
    }

    @Override
    protected int getLayoutId(Bundle savedInstanceState) {
        return R.layout.fragment_explorer;
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPlaylist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideDirectory();
            }
        });
    }

    public void outsideDirectory() {
        if (Constants.MUTABLE_PATH.equals(Constants.ROOT_PATH)) return;

        String mutablePath = Constants.MUTABLE_PATH;
        int lastIndexOfSlash = mutablePath.lastIndexOf('/');
        Constants.MUTABLE_PATH = mutablePath.substring(0, lastIndexOfSlash);

        // here title which shows the address of directory is changed by calling this method
        setTitle();

        checkBoxCheckAll.setChecked(false);

        makeList();
        adapterExplorer.notifyDataSetChanged();
    }

    private void recycleExplorerFiles() {
        recyclerViewFiles = findViewById(R.id.recyclerViewFiles);
        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFiles.setHasFixedSize(true);

        makeList();

        adapterExplorer = new AdapterExplorer(listOfFiles, this);
        recyclerViewFiles.setAdapter(adapterExplorer);
    }

    private void makeList() {

        listOfFiles.clear();

        final File dir = new File(Constants.MUTABLE_PATH);
        final File[] files = dir.listFiles();

        for (File file: files) {
            if (!file.isHidden()) {
                String fileType;

                if (file.isDirectory()) {
                    fileType = "dir";
                } else fileType = getType(file.getPath());

                if (file.isDirectory() || listOfFormats.contains(fileType)) {
                    listOfFiles.add(new ItemExplorer(fileType, file.getName(), file.getAbsolutePath()));
                }
            }
        }

        if (!listOfFiles.isEmpty()) {
            checkBoxCheckAll.setClickable(true);
        } else
            checkBoxCheckAll.setClickable(false);
    }

    private void initializeFormats() {
        // this is the list of media files which android can play
        listOfFormats = new ArrayList<>(Arrays.asList(
                "mp3", "wma", "wav", "mp2", "aac", "ac3", "au", "ogg", "flac"
        ));
    }

    private String getType(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    private void setTitle() {
        getSupportActionBar().setTitle(Constants.MUTABLE_PATH);
    }

    public void sendListBack() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("List", adapterExplorer.listOfSelected);
        setResult(Activity.RESULT_OK,returnIntent);

        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button: {
                sendListBack();
                break;
            }
        }
    }

    private void makeFinalSongList() {

        listOfFinalSelectedSongs.clear();

        for (ItemSongs itemSongs: listOfSongs) {
            for (ItemExplorer itemExplorer: adapterExplorer.listOfSelectedSongs){
                if (itemExplorer.fileType.equals("dir") && itemSongs.getAbsolutePath().startsWith(itemExplorer.getAbsolutePath())){
                    listOfFinalSelectedSongs.add(itemSongs);
                } else if (!itemExplorer.fileType.equals("dir") && itemSongs.getAbsolutePath().equals(itemExplorer.getAbsolutePath())){
                    listOfFinalSelectedSongs.add(itemSongs);
                }
            }
        }
    }

    @Override
    public void insideDirectory(String nameOfFile) {

        showFloatingActionButton(true);

        listOfFiles.clear();

        Constants.MUTABLE_PATH += "/" + nameOfFile;

        setTitle();

        makeList();

        adapterExplorer.notifyDataSetChanged();
    }

    @Override
    public void showFloatingActionButton(boolean listEmpty) {

        if (!listEmpty)
            floatingActionButton.show();
        else
            floatingActionButton.hide();

    }
}
