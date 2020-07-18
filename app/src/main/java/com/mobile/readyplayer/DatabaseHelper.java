package com.mobile.readyplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SONGS = "songs_table";
    public static final String SONGS_ID = "ID";
    public static final String SONGS_NAME = "songName";
    public static final String SONGS_PATH = "pathOfSong";

    public static final String TABLE_PLAYLISTS = "playlists_table";
    public static final String PLAYLISTS_ID = "ID";
    public static final String PLAYLISTS_NAME = "playlistName";

    public DatabaseHelper(Context context) {
        super(context, TABLE_SONGS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " ( " + SONGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SONGS_NAME + " TEXT, " + SONGS_PATH + " TEXT)";

        String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " ( " + PLAYLISTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYLISTS_NAME + " TEXT)";

        db.execSQL(createSongsTable);
        db.execSQL(createPlaylistsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_SONGS);
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_PLAYLISTS);

        onCreate(db);
    }

    public boolean addPlaylistName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYLISTS_NAME, name);

        Log.d("test", "attempt to add = " + name);

        long result =  db.insert(TABLE_PLAYLISTS, null, values);

        // result = -1 means not successful, 0 means success!
        return result != -1;
    }

    public Cursor getData(String tableID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableID;

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void remove(String tableName, String column, String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + tableName + " WHERE " + column + " = '" + item + "'";

        db.execSQL(query);
    }
}
