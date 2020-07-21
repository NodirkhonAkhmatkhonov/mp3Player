package com.mobile.readyplayer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.readyplayer.ui.notification.NotificationReceiver;
import com.mobile.readyplayer.ui.explorer.ActivityExplorerPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogPlaylist.OnInputListener, AdapterCallBack, View.OnClickListener, DialogRemovePlaylist.OnInputListener {

    private String TAG = "test";

    private String currentPlaylist = "Nodirkhon";

    private BroadcastReceiver receiverMain;

    private ServiceMusic musicService = null;

    private RecyclerView recyclerViewPlaylists;
    private RecyclerView recyclerViewBottomSheet;

    private AdapterSongs adapterSongs;
    private AdapterPlaylist adapterPlaylist;

    private List<ItemSongs> listOfSongs;
    private List<ItemSongs> listOfAllSongs;
    public ArrayList<String> listOfPlaylists;

    private ItemSongs currentSongItem;

    private ImageView ivSongImage;
    private ImageView ivPlayButton;
    private ImageView ivNextButton;
    private ImageView ivPreviousButton;
    private ImageView ivRepeatButton;
    private ImageView ivMuteButton;
    private ImageView icDirection;

    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvDuration;
    private TextView tvCurrentPoint;
    private TextView tvPlaylistName;
    private TextView tvBottomSheetPlaylistName;

    private SeekBar ivSeekbar;

    private int currentSongPosition = 0;

    private boolean isPlaying;
    private boolean isRepeated;
    private boolean isMute;
    private boolean isExpand;

    private int SERVICE_RESULT = 1;
    private int LAUNCH_PLAYLIST_ACTIVITY = 2;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FloatingActionButton floatingActionButton;
    private FloatingActionButton fabAdd;

    private View header;
    private Button btnAdd;

    private NotificationReceiver notificationReceiver = null;

    private BottomSheetBehavior bottomSheetBehavior;

    DatabaseHelper mDatabaseHelper;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (isExpand) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            isExpand = !isExpand;
            return;
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SERVICE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                musicService.isFirstTime = true;
                musicService.identifyThroughTheList(data.getStringExtra("song_item"));
            }
        } else if (requestCode == LAUNCH_PLAYLIST_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> resultList = data.getStringArrayListExtra("List");

                addListFromExplorer(resultList);
                adapterSongs.notifyDataSetChanged();
                onBottomSheetArrowClicked();

//                 Write songs inside the list
                addNewSongToDB(currentPlaylist, resultList);

            }
        }
    }

    private void addNewSongToDB(String playlist, ArrayList<String> list) {
        mDatabaseHelper.addNewSong(playlist, list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button: {
                Intent intent = new Intent(this, ActivitySearchPage.class);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.close: {
                onClosePressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // change status bar color
        changeStatusBarColor();

        navigationView = findViewById(R.id.nav_view);

        ivPlayButton = findViewById(R.id.playButton);
        ivPreviousButton = findViewById(R.id.previousButton);
        ivNextButton = findViewById(R.id.nextButton);
        ivMuteButton = findViewById(R.id.muteButton);
        ivRepeatButton = findViewById(R.id.repeatButton);
        icDirection = findViewById(R.id.icDirection);

        header = navigationView.getHeaderView(0);

        btnAdd = header.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        ivPlayButton.setOnClickListener(this);
        ivPreviousButton.setOnClickListener(this);
        ivNextButton.setOnClickListener(this);
        ivMuteButton.setOnClickListener(this);
        ivRepeatButton.setOnClickListener(this);


        ivSeekbar = findViewById(R.id.seekBar);

        tvTitle = findViewById(R.id.songNamePanel);
        tvArtist = findViewById(R.id.songArtistPanel);
        tvDuration = findViewById(R.id.duration);
        tvCurrentPoint = findViewById(R.id.currentPoint);

        // registering database
        mDatabaseHelper = new DatabaseHelper(this);

        recyclerViewPlaylists = header.findViewById(R.id.recyclerViewPlaylist);

        recyclerViewBottomSheet = findViewById(R.id.recyclerViewBottomSheet);

        floatingActionButton = findViewById(R.id.floating_action_bar);
        fabAdd = findViewById(R.id.fabAdd);

        floatingActionButton.setOnClickListener(this);
        fabAdd.setOnClickListener(this);

        notificationReceiver = new NotificationReceiver();

        // Registering notification receiver
        IntentFilter callInterceptorIntentFilter = new IntentFilter("android.intent.action.ANY_ACTION");
        registerReceiver(notificationReceiver,  callInterceptorIntentFilter);

        if (!isMyServiceRunning(ServiceMusic.class)) {
            startService();
        }

        checkExternalMemoryPermission();

        if (checkIfAlreadyHavePermission()) {
            initReceiver();
        }

        // registering notification receiver
        registerMyReceiver();

        ivSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!musicService.isFirstTime) {
                    musicService.playMusic();
                } else {
                    tvCurrentPoint.setText(formatTime(progress / 1000));
                }
                musicService.seekTo(progress);
            }
        });

        recyclePlaylists();

        initBottomSheet();
    }

    private void recycleBottomSheet() {
        recyclerViewBottomSheet.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewBottomSheet.setHasFixedSize(true);

//        listOfSongs = musicService.listOfSongs;
        listOfSongs = new ArrayList<>();
        adapterSongs = new AdapterSongs(listOfSongs, this);
        recyclerViewBottomSheet.setAdapter(adapterSongs);
    }

    private void initBottomSheet() {
        View view = findViewById(R.id.bottom_sheet_scroll);
        bottomSheetBehavior = BottomSheetBehavior.from(view);

        tvPlaylistName = findViewById(R.id.name_playlist);

        tvPlaylistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomSheetArrowClicked();
            }
        });
    }

    private void onBottomSheetArrowClicked() {
        if (!isExpand) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            icDirection.setImageResource(R.drawable.ic_arrow_downward_white_24dp);
            fabAdd.show();
        }
        else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            icDirection.setImageResource(R.drawable.ic_arrow_upward_white_24dp);
            fabAdd.hide();
        }

        isExpand = !isExpand;
    }

    private void initReceiver() {
        Intent intent = new Intent(MainActivity.this, ServiceMusic.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                musicService = ((ServiceMusic.MyBinder) iBinder).getService();

                // Music Service's findList() is called to find the list of all audios in Internal Memory
                musicService.findList();

                recycleBottomSheet();

                if (musicService.currentSong != null) {

                    updateInfoSong(true);

                    isMute = musicService.isMute;
                    isRepeated = musicService.isRepeated;
                    isPlaying = !musicService.mediaPlayer.isPlaying();

                    setPlayButton(!isPlaying);

                    adapterSongs.changeCurrentSongPos(musicService.currentSongPos);
                    currentSongPosition = musicService.currentSongPos;
                    int lastPos = !musicService.isFirstTime ? 0 : musicService.mediaPlayer.getCurrentPosition();
                    ivSeekbar.setProgress(lastPos);
                    tvCurrentPoint.setText(formatTime(lastPos/1000));
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                musicService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void recyclePlaylists() {
        recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewPlaylists.setHasFixedSize(true);

        listOfPlaylists = new ArrayList<>();

        adapterPlaylist = new AdapterPlaylist(listOfPlaylists, this);
        recyclerViewPlaylists.setAdapter(adapterPlaylist);

        addPlaylistFromDB();
    }

    private void addPlaylistFromDB() {
        Cursor data = mDatabaseHelper.getPlaylists(DatabaseHelper.TABLE_PLAYLISTS);

        while (data.moveToNext()) {
            listOfPlaylists.add(data.getString(DatabaseHelper.DATABASE_VERSION));
        }

        if (listOfPlaylists.size() == 0) {
            addNewPlaylist("Default");
        }

        currentPlaylist = listOfPlaylists.get(0);
        Toast.makeText(this, "" + currentPlaylist, Toast.LENGTH_SHORT).show();

        listOfPlaylists.add(0, "All");
    }

    private void fakeGetSongFromDB() {
        Cursor data = mDatabaseHelper.getSongs(currentPlaylist);

        while (data.moveToNext()) {
            Log.d("test", data.getString(DatabaseHelper.SONGS_ID_PATH));
        }

    }

    private void fakeGetPlaylistFromDB() {
        Cursor data = mDatabaseHelper.getPlaylists(DatabaseHelper.TABLE_PLAYLISTS);

        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(data.getString(DatabaseHelper.PLAYLIST_ID_NAME));
        }

        for (String name: listData) {
            Log.d("test", "playlist = " + name);
        }
    }


    private void setPlayButton(boolean doIPlay) {
        if (doIPlay) {
            ivPlayButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            ivPlayButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        isPlaying = !isPlaying;
    }

    private void registerMyReceiver() {
        receiverMain = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getIntExtra("type", -1)) {
                    case Constants.SERVICE_LIST_READY: {
                        updateInfoSong(true);
                        break;
                    }
                    case Constants.SERVICE_SEEK_TO: {
                        int seekToPosition = intent.getIntExtra("seek_to_position", 0);
                        ivSeekbar.setProgress(seekToPosition);
                        tvCurrentPoint.setText(formatTime(seekToPosition / 1000));
                        break;
                    }
                    case Constants.SERVICE_CURRENT_SONG_DETAIL: {
                        updateInfoSong(false);

                        Toast.makeText(context, "Detail", Toast.LENGTH_SHORT).show();
                        adapterSongs.changeCurrentSongPos(currentSongPosition);
                        isPlaying = false;
                        setPlayButton(true);
                        break;
                    }
                    case Constants.SERVICE_ON_SONG_COMPLETED: {
                        if (!isRepeated) {
                            setPlayButton(false);
                        }
                        ivSeekbar.setProgress(0);
                        tvCurrentPoint.setText("00:00");
                        break;
                    }
                    case Constants.NOTIFICATION_PREVIOUS: {
                        onPreviousPressed();
                        break;
                    }
                    case Constants.NOTIFICATION_PLAY: {
                        onPlayPressed();
                        break;
                    }
                    case Constants.NOTIFICATION_NEXT: {
                        onNextPressed();
                        break;
                    }
                    case Constants.NOTIFICATION_CLOSE: {
                        onClosePressed();
                        break;
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(Constants.MAIN_ACTION);
        registerReceiver(receiverMain, filter);

    }

    private void onClosePressed() {
        musicService.closeNotification();
        musicService.stopSelf();
        finish();
    }

    private void onNextPressed() {
        musicService.changeMusic(false);
    }

    private void onPlayPressed() {
        musicService.playMusic();
        setPlayButton(!isPlaying);
    }

    private void onPreviousPressed() {
        musicService.changeMusic(true);
    }

    private void updateInfoSong(boolean isListReady) {
        if (musicService.listOfAllSongs.isEmpty()) return;
        if (isListReady) {
            musicService.currentSong = musicService.listOfAllSongs.get(0);
            musicService.sendNotification();
            currentSongItem = musicService.listOfAllSongs.get(0);
            listOfAllSongs = musicService.listOfAllSongs;
        }
        else
            currentSongItem = musicService.currentSong;

        tvTitle.setText(currentSongItem.getTitle());
        tvArtist.setText(currentSongItem.getArtist());
        ivSeekbar.setMax(currentSongItem.getDuration());
        tvDuration.setText(formatTime(currentSongItem.getDuration() / 1000));

    }

    private void setRepeatButton() {
        if (!isRepeated) {
            ivRepeatButton.setImageResource(R.drawable.ic_autorenew_pink_24dp);
        } else
            ivRepeatButton.setImageResource(R.drawable.ic_autorenew_white_24dp);

        isRepeated = !isRepeated;
        musicService.isRepeated = isRepeated;
    }

    private void setMutedButton() {
        if (!isMute) {
            ivMuteButton.setImageResource(R.drawable.ic_volume_off_white_24dp);
            musicService.mediaPlayer.setVolume(0, 0);
        } else {
            ivMuteButton.setImageResource(R.drawable.ic_volume_up_white_24dp);
            musicService.mediaPlayer.setVolume(1, 1);
        }
        isMute = !isMute;
        musicService.isMute = isMute;
    }

    private void addListFromExplorer(ArrayList<String> listOfSelectedSongs) {

        if (listOfSelectedSongs.isEmpty()) {
            Toast.makeText(this, "No song is chosen!", Toast.LENGTH_SHORT).show();
            return;
        }

        listOfAllSongs = musicService.getListOfAllSongs();

        Log.d(TAG, "All songs");
        for (ItemSongs itemSongs: listOfAllSongs) {
            Log.d(TAG, itemSongs.getAbsolutePath());
        }

        Log.d(TAG, "Selected songs");
        for (String path: listOfSelectedSongs) {
            Log.d(TAG, path);
        }

//
//        ArrayList<ItemSongs> tempList = new ArrayList<>();

        for (String path: listOfSelectedSongs) {
            for (ItemSongs itemSongs: listOfAllSongs) {
                if (!path.contains(".") && itemSongs.getAbsolutePath().startsWith(path) && listOfSongs.indexOf(itemSongs) == -1) {
//                    tempList.add(itemSongs);
                    listOfSongs.add(itemSongs);
                    Log.d(TAG, "Folder");
                } else if (path.contains(".") && itemSongs.getAbsolutePath().equals(path) && listOfSongs.indexOf(itemSongs) == -1) {
//                    tempList.add(itemSongs);
                    listOfSongs.add(itemSongs);
                    Log.d(TAG, "Music");
                }
            }
        }

//        Log.d(TAG, "From explorer size = " + tempList.size());
//
//        for (ItemSongs itemSongs : tempList) {
//            if (listOfSongs.indexOf(itemSongs) == -1)
//                listOfSongs.add(itemSongs);
//        }
//        listOfSongs.addAll(tempList);

        adapterSongs.notifyDataSetChanged();

        musicService.setPlaylist((ArrayList<ItemSongs>) listOfSongs);
    }

    @Override
    public void onMethodCallBack(int position) {

        /// Here there is a trick. In order to avoid exception, we declared isFirstTime inside musicService. If we press play
        /// button straightforward when we ran app for the first time, service has to play first music in the list as the default one
        /// But inside service it is controlled using isFirstTime. If it is false, then it means app must play the first song as default
        /// If the user chooses any song from the list when the app is opened for the first time, it should play that song. So if we
        /// change the value of isFirstTime into true, then everything works as usual.
        if (!musicService.isFirstTime) {
            musicService.isFirstTime = true;
        }
        ///

        currentSongPosition = position;
        musicService.setMusic(position);

        // if any song is selected, then the bottom sheet will come to the collapsed state
        onBottomSheetArrowClicked();
    }

    @Override
    public void changePlaylist(String playlist) {

        if (playlist.equals("All")) {
            currentPlaylist = playlist;
            tvPlaylistName.setText(playlist);

            listOfSongs = listOfAllSongs;

            adapterSongs.listOfFiles = listOfAllSongs;
            adapterSongs.notifyDataSetChanged();

            setNewPlaylistToService();
        }

        Toast.makeText(this, currentPlaylist, Toast.LENGTH_SHORT).show();
//        Cursor data = mDatabaseHelper.getPlaylists(DatabaseHelper.TABLE_PLAYLISTS);
    }

    private void setNewPlaylistToService() {
        musicService.setPlaylist((ArrayList<ItemSongs>) listOfSongs);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton: {
                onPlayPressed();
                break;
            }

            case R.id.previousButton: {
                onPreviousPressed();
                break;
            }

            case R.id.nextButton: {
                onNextPressed();
                break;
            }

            case R.id.repeatButton: {
                setRepeatButton();
                break;
            }

            case R.id.muteButton: {

                fakeGetSongFromDB();
//                setMutedButton();
                break;
            }

            case R.id.btnAdd: {
                openPlaylistDialog();
            }

            case R.id.floating_action_bar: {
                Collections.shuffle(musicService.listOfAllSongs);

                musicService.setCurrentPos();
                currentSongPosition = musicService.currentSongPos;
                adapterSongs.changeCurrentSongPos(currentSongPosition);

                if (!musicService.isFirstTime)
                    updateInfoSong(true);
                break;
            }

            case R.id.fabAdd: {
                openActivityPlaylistPage();
            }
        }
    }

    public void openPlaylistDialog() {
        DialogPlaylist dialogPlaylist = new DialogPlaylist();
        dialogPlaylist.show(getSupportFragmentManager(), "playlist dialog");
    }

    public void openRemovePlaylistDialog() {
        DialogRemovePlaylist dialogRemovePlaylist = new DialogRemovePlaylist();
        dialogRemovePlaylist.show(getSupportFragmentManager(), "remove playlist dialog");
    }

    @Override
    public void addNewPlaylist(String newEntry) {
        listOfPlaylists.add(newEntry);
        adapterPlaylist.notifyDataHasChanged();

        mDatabaseHelper.addPlaylist(newEntry);
    }

    public void openActivityPlaylistPage() {
        Intent intent = new Intent(this, ActivityExplorerPage.class);
        intent.putExtra("listOfSongs", (Serializable) listOfAllSongs);
        startActivityForResult(intent, LAUNCH_PLAYLIST_ACTIVITY);
    }

    @Override
    public void removePlayListItem() {
        // deleting from database
        mDatabaseHelper.removePlaylistItem(DatabaseHelper.TABLE_PLAYLISTS, DatabaseHelper.PLAYLISTS_NAME, adapterPlaylist.removablePlaylistName());

        // deleteing from adapter
        adapterPlaylist.removePlayListItem();
        adapterPlaylist.notifyDataSetChanged();

    }








    @Override
    protected void onStart() {
        super.onStart();
        Constants.ACTIVITY_ALIVE = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mDatabaseHelper.setLastPlaylist();

        Constants.ACTIVITY_ALIVE = false;
    }

    private String formatTime(int position) {
        return String.format("%s:%s", fillNumber(position / 60), fillNumber(position % 60));
    }

    private String fillNumber(int number) {
        return number < 10 ? String.format("0%d", number) : String.valueOf(number);
    }

    private void checkExternalMemoryPermission() {
        if (!checkIfAlreadyHavePermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initReceiver();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void startService() {
        Intent intent = new Intent(this, ServiceMusic.class);
        startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void changeStatusBarColor() {
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
    }
}
