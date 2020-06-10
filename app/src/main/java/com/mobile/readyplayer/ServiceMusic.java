package com.mobile.readyplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.mobile.readyplayer.ui.notification.NotificationReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.mobile.readyplayer.ui.notification.App.CHANNEL_ID_1;

public class ServiceMusic extends Service{

    private MyBinder musicServiceBinder = new MyBinder();

    public ArrayList<ItemSongs> listOfSongs;
    public ItemSongs currentSong;

    public boolean isRepeated;
    public boolean isMute;

    public boolean isFirstTime = false;

    public MediaPlayer mediaPlayer = new MediaPlayer();
    private Timer timer;

    public int currentSongPos;

    private BroadcastReceiver receiverNotification;

    private Notification notification;
    ///

    private NotificationManagerCompat managerCompat;

    private MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        managerCompat = NotificationManagerCompat.from(this);
        mediaSessionCompat = new MediaSessionCompat(this, "tag");
        registerNotificationReceiver();
        return START_NOT_STICKY;
    }

    public void closeNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        nMgr.cancelAll();
    }

    public void sendNotification() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastPreviousIntent = new Intent(this, NotificationReceiver.class);
        broadcastPreviousIntent.putExtra("previous", Constants.NOTIFICATION_PREVIOUS);
        PendingIntent actionPreviousIntent = PendingIntent.getBroadcast(this,
                0, broadcastPreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastPlayIntent = new Intent(this, NotificationReceiver.class);
        broadcastPlayIntent.putExtra("play", Constants.NOTIFICATION_PLAY);
        PendingIntent actionPlayIntent = PendingIntent.getBroadcast(this,
                1, broadcastPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastNextIntent = new Intent(this, NotificationReceiver.class);
        broadcastNextIntent.putExtra("next", Constants.NOTIFICATION_NEXT);
        PendingIntent actionNextIntent = PendingIntent.getBroadcast(this,
                2, broadcastNextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastCloseIntent = new Intent(this, NotificationReceiver.class);
        broadcastCloseIntent.putExtra("close", Constants.NOTIFICATION_CLOSE);
        PendingIntent actionCloseIntent = PendingIntent.getBroadcast(this,
                3, broadcastCloseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeImage = BitmapFactory.decodeResource(getResources(), R.drawable.photo);

        notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle(currentSong.title)
                .setContentText(currentSong.artist)
                .setLargeIcon(largeImage)
                .setOngoing(true)
                .addAction(R.drawable.ic_skip_previous_white_24dp, "previous", actionPreviousIntent)
                .addAction(!mediaPlayer.isPlaying() ? R.drawable.ic_play_arrow_white_24dp : R.drawable.ic_pause_white_24dp,
                        "play", actionPlayIntent)
                .addAction(R.drawable.ic_skip_next_white_24dp, "next", actionNextIntent)
                .addAction(R.drawable.ic_close_black_24dp, "close", actionCloseIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .build();

        startForeground(1, notification);
    }
        private void registerNotificationReceiver() {
            receiverNotification = new NotificationReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int code = intent.getIntExtra("type", -1);

                    if (!Constants.ACTIVITY_ALIVE) {
                        switch (code) {
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
                }
            };
            IntentFilter intentFilter = new IntentFilter(Constants.MAIN_ACTION);
            registerReceiver(receiverNotification, intentFilter);
        }

    private void onClosePressed() {
        closeNotification();
        stopSelf();
    }

    private void onNextPressed() {
        changeMusic(false);
    }

    private void onPlayPressed() {
        playMusic();
    }

    private void onPreviousPressed() {
        changeMusic(true);
    }

    public void seekTo(int toPosition) {
        mediaPlayer.seekTo(toPosition);
    }

    private void sendPosition() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(Constants.MAIN_ACTION);
                    intent.putExtra("type", Constants.SERVICE_SEEK_TO);
                    try {
                        intent.putExtra("seek_to_position", mediaPlayer.getCurrentPosition());
                        if (mediaPlayer != null && mediaPlayer.isPlaying())
                            sendBroadcast(intent);
                    } catch (Exception e){
                    }

                }
            }, 0, 1000);
    }

    public void changeMusic(boolean isPrevious) {

        int targetPosition;
        if (isPrevious) {
            targetPosition = currentSongPos == 0 ? listOfSongs.size() - 1 : currentSongPos - 1;
        } else {
            targetPosition = currentSongPos == listOfSongs.size() - 1 ? 0 : currentSongPos + 1;
        }
        setMusic(targetPosition);
        currentSongPos = targetPosition;
    }

    public void setMusic(int position) {
        currentSong = listOfSongs.get(position);
        makeMediaPlayer();
        currentSongPos = position;
    }

    public void makeMediaPlayer() {
        if (listOfSongs.isEmpty()) return;
        clearMediaPlayer();
        try {
            mediaPlayer = new MediaPlayer();
            if (!isFirstTime) {
                Log.d("test", "is first time");
                currentSong = listOfSongs.get(0);
                isFirstTime = true;
            }

            mediaPlayer.setDataSource(currentSong.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playMusic();
                    sendCurrentSongDet();
                    sendPosition();
                    if (isMute) {
                        mediaPlayer.setVolume(0, 0);
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    Intent intent = new Intent(Constants.MAIN_ACTION);
//                    intent.putExtra("type", Constants.SERVICE_ON_SONG_COMPLETED);
//                    sendBroadcast(intent);

                    if (isRepeated) {
                        playMusic();
                        mediaPlayer.seekTo(0);
                    } else changeMusic(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendCurrentSongDet() {
        Intent intent = new Intent(Constants.MAIN_ACTION);
        intent.putExtra("type", Constants.SERVICE_CURRENT_SONG_DETAIL);
        intent.putExtra("current_song_item", currentSong);
        sendBroadcast(intent);
    }

    private void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    public void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            if (!isFirstTime) {
                makeMediaPlayer();
                isFirstTime = true;
            }
            else {
                mediaPlayer.start();
                sendPosition();
            }
        } else {
            mediaPlayer.pause();
        }
        sendNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
        mediaPlayer = null;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicServiceBinder;
    }

    class MyBinder extends Binder {
        ServiceMusic getService() {
            return ServiceMusic.this;
        }
    }

    /// this method is called my MainActivity when the user has chosen a song from search page. As an argument, absolute path of a song
    /// comes and found through the list.
    public void identifyThroughTheList(String targetPath) {
        for (int i = 0; i < listOfSongs.size(); i++) {
            if (listOfSongs.get(i).getAbsolutePath().equals(targetPath)){
                setMusic(i);
                break;
            }
        }
    }

    public void findList() {
        listOfSongs = new ArrayList<>();
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DISPLAY_NAME + " ASC");

        if (audioCursor != null && audioCursor.moveToFirst()) {
            int artistColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int titleColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int pathColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumnId = audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
//            int albumArt = audioCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            do {
                String artist = audioCursor.getString(artistColumnId);
                String title = audioCursor.getString(titleColumnId);
                String path = audioCursor.getString(pathColumnId);
                long duration = audioCursor.getLong(durationColumnId);
//                String pathSong = audioCursor.getString(albumArt);
//                Log.d("test", "Album_ID = " + pathSong);

                listOfSongs.add(new ItemSongs(title, path, artist, duration));
            } while (audioCursor.moveToNext());


        }

        informListReady();
    }

    public void setCurrentPos() {
        /// Why it is checked for the value of isFirstTime is for a reason. If the user shuffles the list when the app is run for the first
        /// time, then it must always choose the first song always. When shuffle button is pressed it shuffles the list and the first song
        /// might also change. In order to keep the first position, is checked for if the app is ran for the first time.
        if (isFirstTime)
        currentSongPos = listOfSongs.indexOf(currentSong);
    }

    /// This method is a one-time-called method when the list of all songs is ready. It broadcasts it to receivers.
    private void informListReady() {
        Intent intent = new Intent(Constants.MAIN_ACTION);
        intent.putExtra("type", Constants.SERVICE_LIST_READY);
        sendBroadcast(intent);
    }
}
