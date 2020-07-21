package com.mobile.readyplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "da";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SONGS = "songs_table";
    public static final String SONGS_ID = "ID";
    public static final String SONGS_PLAYLIST = "songName";
    public static final String SONGS_PATH = "pathOfSong";
    public static final int SONGS_ID_ID = 0;
    public static final int SONGS_ID_PLAYLIST = 1;
    public static final int SONGS_ID_PATH = 2;

    public static final String TABLE_PLAYLISTS = "playlists_table";
    public static final String PLAYLISTS_ID = "ID";
    public static final String PLAYLISTS_NAME = "playlistName";
    public static final int PLAYLIST_ID_ID = 0;
    public static final int PLAYLIST_ID_NAME = 1;

    public static final String TABLE_LAST_RECORDS = "last_records_table";
    public static final String LAST_RECORDS_ID = "ID";
    public static final String LAST_RECORDS_LAST_FILE = "lastFile"; // It is the column for the last playlist and song which was played
    public static final String LAST_RECORDS_LAST_SEEK = "lastPoint"; // It represents tha last point of progress bar seek

    private int sizeOfPlaylists = 0;

    public DatabaseHelper(Context context) {
        super(context, TABLE_SONGS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " ( " + SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SONGS_PLAYLIST + " TEXT, " + SONGS_PATH + " TEXT)";

        String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " ( " + PLAYLISTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYLISTS_NAME + " TEXT)";

        String createLastPlaylistTable = "CREATE TABLE " + TABLE_LAST_RECORDS + " ( " + LAST_RECORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LAST_RECORDS_LAST_FILE + " TEXT, " + LAST_RECORDS_LAST_SEEK + " INTEGER)";

        db.execSQL(createSongsTable);
        db.execSQL(createPlaylistsTable);
        db.execSQL(createLastPlaylistTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_SONGS);
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_LAST_RECORDS);

        onCreate(db);
    }

    public boolean addPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PLAYLISTS_NAME, name);

        long result =  db.insert(TABLE_PLAYLISTS, null, values);

        // result = -1 means not successful, 0 means success!
        return result != -1;
    }

    public void addNewSong(String playlistName, ArrayList<String> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (String path: list) {
            values.put(SONGS_PATH, path);
            values.put(SONGS_PLAYLIST, playlistName);

            db.insert(TABLE_SONGS, null, values);
        }

    }

    public void recordLastPlaylist(String lastPlaylist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LAST_RECORDS_LAST_FILE, lastPlaylist);
        db.insert(TABLE_LAST_RECORDS, null, values);
    }

    public Cursor getPlaylists(String tableID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableID;

        Cursor data = db.rawQuery(query, null);

        sizeOfPlaylists = data.getCount();

        return data;
    }

    public Cursor getSongs(String playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SONGS + " WHERE " + SONGS_PLAYLIST + " = " + "'" + playlist + "'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // In order to get the last played playlist
    public void setLastPlaylist(String currentPlaylist, String helperPlaylist) {
        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "UPDATE " + TABLE_PLAYLISTS + " SET ID = " + 100 + " WHERE " + PLAYLISTS_NAME + " = ''" + ;
//        db.execSQL(query);
    }
    public void removePlaylistItem(String tableName, String column, String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + tableName + " WHERE " + column + " = '" + item + "'";

        db.execSQL(query);
    }

}
