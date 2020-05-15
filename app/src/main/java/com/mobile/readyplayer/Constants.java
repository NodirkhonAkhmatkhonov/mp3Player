package com.mobile.readyplayer;

public class Constants {

    // actions
    public static final String MAIN_ACTION = "MAIN_ACTION";
//    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";

    // for Main UI
    public static final int SERVICE_SEEK_TO = 1;
    public static final int SERVICE_LIST_READY = 2;
    public static final int SERVICE_CURRENT_SONG_DETAIL = 3;
    public static final int SERVICE_ON_SONG_COMPLETED = 4;

    // for Notification Receiver
    public static final int NOTIFICATION_PREVIOUS = 5;
    public static final int NOTIFICATION_PLAY = 6;
    public static final int NOTIFICATION_NEXT = 7;
    public static final int NOTIFICATION_CLOSE = 8;

    public static boolean ACTIVITY_ALIVE = true;

    public static final String ROOT_PATH = "/storage/emulated/0";
    public static String MUTABLE_PATH = ROOT_PATH;

//    public static int NUMBER_OF_TASKS = 0;
}
