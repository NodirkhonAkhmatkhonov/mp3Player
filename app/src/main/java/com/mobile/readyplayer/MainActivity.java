package com.mobile.readyplayer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.arch.core.executor.DefaultTaskExecutor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.readyplayer.ui.ItemPlaylist;
import com.mobile.readyplayer.ui.notification.NotificationReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogPlaylist.OnInputListener, AdapterCallBack, View.OnClickListener, DialogRemovePlaylist.OnInputListener {

    private BroadcastReceiver receiverMain;

    private ServiceMusic musicService = null;

    private RecyclerView recyclerViewSongs;
    private RecyclerView recyclerViewPlaylists;

    private AdapterSongs adapterSongs;
    private AdapterPlaylist adapterPlaylist;

    private List<ItemSongs> listOfSongs;
    public List<ItemPlaylist> listOfPlaylists;

    private ItemSongs currentSongItem;

    private ImageView ivSongImage;
    private ImageView ivPlayButton;
    private ImageView ivNextButton;
    private ImageView ivPreviousButton;
    private ImageView ivRepeatButton;
    private ImageView ivMuteButton;

    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvDuration;
    private TextView tvCurrentPoint;

    private SeekBar ivSeekbar;

    private int currentSongPosition = 0;

    private boolean isPlaying;
    private boolean isRepeated;
    private boolean isMute;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FloatingActionButton floatingActionButton;

    private View header;
    private Button btnAdd;

    private NotificationReceiver notificationReceiver = null;

    @Override
    protected void onStart() {
        super.onStart();
        Constants.ACTIVITY_ALIVE = true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
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
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                musicService.isFirstTime = true;
                musicService.identifyThroughTheList(data.getStringExtra("song_item"));
            }
        }
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

        navigationView = findViewById(R.id.nav_view);

        ivPlayButton = findViewById(R.id.playButton);
        ivPreviousButton = findViewById(R.id.previousButton);
        ivNextButton = findViewById(R.id.nextButton);
        ivMuteButton = findViewById(R.id.muteButton);
        ivRepeatButton = findViewById(R.id.repeatButton);

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

        recyclerViewSongs = findViewById(R.id.recyclerView);
        recyclerViewPlaylists = header.findViewById(R.id.recyclerViewPlaylist);

        floatingActionButton = findViewById(R.id.floating_action_bar);
        floatingActionButton.setOnClickListener(this);

        notificationReceiver = new NotificationReceiver();

        IntentFilter callInterceptorIntentFilter = new IntentFilter("android.intent.action.ANY_ACTION");
        registerReceiver(notificationReceiver,  callInterceptorIntentFilter);

        if (!isMyServiceRunning(ServiceMusic.class)) {
            startService();
        }

        checkExternalMemoryPermission();

        if (checkIfAlreadyHavePermission()) {
            initReceiver();
        }

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
    }

    private void initReceiver() {
        Intent intent = new Intent(MainActivity.this, ServiceMusic.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                musicService = ((ServiceMusic.MyBinder) iBinder).getService();

                // Music Service's findList() is called to find the list of all audios in Internal Memory
                musicService.findList();

                recycleSongs();

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

    private void recycleSongs() {
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewSongs.setHasFixedSize(true);

        listOfSongs = musicService.listOfSongs;
        adapterSongs = new AdapterSongs(listOfSongs, this);
        recyclerViewSongs.setAdapter(adapterSongs);
    }

    private void recyclePlaylists() {
        recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewPlaylists.setHasFixedSize(true);

        listOfPlaylists = new ArrayList<>();

        listOfPlaylists.add(new ItemPlaylist("Default"));

        adapterPlaylist = new AdapterPlaylist(listOfPlaylists, this);
        recyclerViewPlaylists.setAdapter(adapterPlaylist);
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

                        adapterSongs.changeCurrentSongPos(listOfSongs.indexOf(currentSongItem));
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
        if (musicService.listOfSongs.isEmpty()) return;
        if (isListReady) {
            musicService.currentSong = musicService.listOfSongs.get(0);
            musicService.sendNotification();
            currentSongItem = musicService.listOfSongs.get(0);
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

    @Override
    public void onMethodCallBack(int position) {

        /// Here there is a trick. In order to avoid exception, we declared isFirstTime inside musicService. If we press play
        /// button straightforward when we ran app for the first time, service has to play first music in the list as the default one
        /// But inside service it is controlled using isFirstTime. If it is false, then it means app must play the first song as default
        /// If the user chooses any song from the list as the app is opened for the first time, it should play that song. So if we
        /// change the value of isFirstTime into true, then everything works as usual.
        if (!musicService.isFirstTime) {
            musicService.isFirstTime = true;
        }
        ///

        currentSongPosition = position;
        musicService.setMusic(position);
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
                setMutedButton();
                break;
            }

            case R.id.floating_action_bar: {
                Collections.shuffle(musicService.listOfSongs);

                musicService.setCurrentPos();
                currentSongPosition = musicService.currentSongPos;
                adapterSongs.changeCurrentSongPos(currentSongPosition);

                if (!musicService.isFirstTime)
                    updateInfoSong(true);
                break;
            }

            case R.id.btnAdd: {
                openPlaylistDialog();
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
    public void sendInPut(String input) {
        listOfPlaylists.add(new ItemPlaylist(input));
        adapterPlaylist.listOfPlaylists = listOfPlaylists;
        adapterPlaylist.notifyDataHasChanged();

        Toast.makeText(musicService, "Playlist " + input + " is created successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.ACTIVITY_ALIVE = false;
    }

    public void openActivityPlaylistPage() {
        Intent intent = new Intent(this, ActivityPlaylistPage.class);
        startActivity(intent);
    }

    @Override
    public void removePlayListItem() {
        adapterPlaylist.removePlayListItem();
    }
}
