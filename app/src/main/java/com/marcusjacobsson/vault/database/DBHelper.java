package com.marcusjacobsson.vault.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcus Jacobsson on 2015-08-04.
 *
 * ***********************************************
 *
 *      Version History
 *      ***************
 *
 *      Version 1
 *
 *          Initially created Password Request to track password requests made by user.
 *
 *          Created Images and Videos tables mainly to hold info
 *          about previously stored locations.
 *
 *      Version 2
 *
 *          Added SMS table to hold info about previously stored locations
 *          Added methods to backup and restore previously stored data in the database.
 *
 * ************************************************
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "vault.db";
    public static final int DATABASE_VERSION = 2;

    //Password requests
    public static final String TABLE_NAME_PASSWORD_RESET_REQUEST = "password_reset_requests";
    public static final String COLUMN_PRR_ID = "_id";
    public static final String COLUMN_PRR_TIME = "time";

    //Image info
    public static final String TABLE_NAME_IMAGES = "images";
    public static final String COLUMN_IMAGES_ID = "_id";
    public static final String COLUMN_IMAGES_OLD_PATH = "old_path";
    public static final String COLUMN_IMAGES_FILE_NAME = "file_name";

    //video thumbnails
    public static final String TABLE_NAME_VIDEO = "videos";
    public static final String COLUMN_VIDEO_ID = "_id";
    public static final String COLUMN_VIDEO_VIDEO_ID = "video_id";
    public static final String COLUMN_VIDEO_THUMB_PATH = "thumb_path";
    public static final String COLUMN_VIDEO_DURATION = "duration";
    public static final String COLUMN_VIDEO_SIZE = "size";
    public static final String COLUMN_VIDEO_TITLE = "title";
    public static final String COLUMN_VIDEO_OLD_PATH = "old_path";

    //SMS
    public static final String TABLE_NAME_SMS = "sms";
    public static final String COLUMN_SMS_ID = "_id";
    public static final String COLUMN_SMS_THREAD_ID = "thread_id";
    public static final String COLUMN_SMS_ADDRESS = "address";
    public static final String COLUMN_SMS_PERSON = "person";
    public static final String COLUMN_SMS_TIME = "time";
    public static final String COLUMN_SMS_PROTOCOL = "protocol";
    public static final String COLUMN_SMS_READ = "read";
    public static final String COLUMN_SMS_STATUS = "status";
    public static final String COLUMN_SMS_TYPE = "type";
    public static final String COLUMN_SMS_REPLY_PATH_PRESENT = "reply_path_present";
    public static final String COLUMN_SMS_SUBJECT = "subject";
    public static final String COLUMN_SMS_MSG = "msg";
    public static final String COLUMN_SMS_SERVICE_CENTER = "service_center";
    public static final String COLUMN_SMS_LOCKED = "locked";
    public static final String COLUMN_SMS_ERROR_CODE = "error_code";
    public static final String COLUMN_SMS_SEEN = "seen";
    public static final String COLUMN_SMS_FOLDER_NAME = "folder_name";
    public static final String COLUMN_SMS_CONTACT_NAME = "contact_name";

    public static final String DATABASE_CREATE_PASSWORD_RESET_REQUEST = "create table " +
            TABLE_NAME_PASSWORD_RESET_REQUEST +
            "( " +
            COLUMN_PRR_ID + " text primary key not null, " +
            COLUMN_PRR_TIME + " text);";

    public static final String DATABASE_CREATE_IMAGES = "create table " +
            TABLE_NAME_IMAGES +
            "( " +
            COLUMN_IMAGES_ID + " integer primary key autoincrement, " +
            COLUMN_IMAGES_FILE_NAME + " text, " +
            COLUMN_IMAGES_OLD_PATH + " text);";

    public static final String DATABASE_CREATE_VIDEOS = "create table " +
            TABLE_NAME_VIDEO +
            "( " +
            COLUMN_VIDEO_ID + " integer primary key autoincrement, " +
            COLUMN_VIDEO_VIDEO_ID + " text, " +
            COLUMN_VIDEO_THUMB_PATH + " text, " +
            COLUMN_VIDEO_DURATION + " text, " +
            COLUMN_VIDEO_SIZE + " text, " +
            COLUMN_VIDEO_TITLE + " text, " +
            COLUMN_VIDEO_OLD_PATH + " text);";

    public static final String DATABASE_CREATE_SMS = "create table " +
            TABLE_NAME_SMS +
            "( " +
            COLUMN_SMS_ID + " text primary key, " +
            COLUMN_SMS_THREAD_ID + " text, " +
            COLUMN_SMS_ADDRESS + " text, " +
            COLUMN_SMS_PERSON + " person, " +
            COLUMN_SMS_TIME + " text, " +
            COLUMN_SMS_PROTOCOL + " text, " +
            COLUMN_SMS_READ + " text, " +
            COLUMN_SMS_STATUS + " text, " +
            COLUMN_SMS_TYPE + " text, " +
            COLUMN_SMS_REPLY_PATH_PRESENT + " text, " +
            COLUMN_SMS_SUBJECT + " text, " +
            COLUMN_SMS_MSG + " text, " +
            COLUMN_SMS_SERVICE_CENTER + " text, " +
            COLUMN_SMS_LOCKED + " text, " +
            COLUMN_SMS_ERROR_CODE + " text, " +
            COLUMN_SMS_SEEN + " text, " +
            COLUMN_SMS_FOLDER_NAME + " text, " +
            COLUMN_SMS_CONTACT_NAME + " text);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_PASSWORD_RESET_REQUEST);
        db.execSQL(DATABASE_CREATE_IMAGES);
        db.execSQL(DATABASE_CREATE_VIDEOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 1:
                db.execSQL(DATABASE_CREATE_SMS);
                break;
        }
    }
}
