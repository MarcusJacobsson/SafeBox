package com.marcusjacobsson.vault.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.marcusjacobsson.vault.pojos.PasswordResetRequest;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.pojos.Video;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marcus Jacobsson on 2015-08-04.
 */
public class DataSource {

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public DataSource(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    public void open() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public long createPasswordResetRequest(String id) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PRR_ID, id);
        values.put(DBHelper.COLUMN_PRR_TIME,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        return database.insert(DBHelper.TABLE_NAME_PASSWORD_RESET_REQUEST, null, values);
    }

    public PasswordResetRequest getPasswordResetRequest(String id) {
        PasswordResetRequest tmpPasswordResetRequest = new PasswordResetRequest();
        String where = DBHelper.COLUMN_PRR_ID + "='" + id + "'";
        Cursor cursor = database.query(true, DBHelper.TABLE_NAME_PASSWORD_RESET_REQUEST, null,
                where, null, null, null, null, null);
        if (cursor != null && !cursor.isAfterLast()) {
            cursor.moveToFirst();
            tmpPasswordResetRequest.setId(cursor.getString(0));
            tmpPasswordResetRequest.setTime(cursor.getString(1));
            cursor.close();
        }
        return tmpPasswordResetRequest;
    }

    public long deletePasswordResetRequest(String id) {
        String where = DBHelper.COLUMN_PRR_ID + "='" + id + "'";
        return database.delete(DBHelper.TABLE_NAME_PASSWORD_RESET_REQUEST, where, null);
    }

    public long createImageEntry(String name, String oldPath) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_IMAGES_FILE_NAME, name);
        values.put(DBHelper.COLUMN_IMAGES_OLD_PATH, oldPath);
        return database.insert(DBHelper.TABLE_NAME_IMAGES, null, values);
    }

    public long deleteImageEntry(String name) {
        String where = DBHelper.COLUMN_IMAGES_FILE_NAME + "='" + name + "'";
        return database.delete(DBHelper.TABLE_NAME_IMAGES, where, null);
    }

    public String getImageEntryOldPath(String name) {
        String path = "";
        String where = DBHelper.COLUMN_IMAGES_FILE_NAME + "='" + name + "'";
        Cursor cursor = database.query(true, DBHelper.TABLE_NAME_IMAGES, null,
                where, null, null, null, null, null);
        if (cursor != null && !cursor.isAfterLast()) {
            cursor.moveToFirst();
            path = cursor.getString(2);
            cursor.close();
        }
        return path;
    }

    public long createVideoEntry(String videoId, String thumbnailPath, String duration, String size, String title, String path) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_VIDEO_VIDEO_ID, videoId);
        values.put(DBHelper.COLUMN_VIDEO_THUMB_PATH, thumbnailPath);
        values.put(DBHelper.COLUMN_VIDEO_DURATION, duration);
        values.put(DBHelper.COLUMN_VIDEO_SIZE, size);
        values.put(DBHelper.COLUMN_VIDEO_TITLE, title);
        values.put(DBHelper.COLUMN_VIDEO_OLD_PATH, path);
        return database.insert(DBHelper.TABLE_NAME_VIDEO, null, values);
    }

    public Video getVideoEntry(String videoTitle) {
        String where = DBHelper.COLUMN_VIDEO_TITLE + "='" + videoTitle + "'";
        Cursor cursor = database.query(true, DBHelper.TABLE_NAME_VIDEO, null,
                where, null, null, null, null, null);
        Video video = new Video();
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                video.setId(cursor.getString(1));
                video.setThumbnailPath(cursor.getString(2));
                video.setDuration(cursor.getString(3));
                video.setSize(cursor.getString(4));
                video.setTitle(cursor.getString(5));
            }
            cursor.close();
        }
        return video;
    }

    public String getVideoEntryOldPath(String title) {
        String path = "";
        String where = DBHelper.COLUMN_VIDEO_TITLE + "='" + title + "'";
        Cursor cursor = database.query(true, DBHelper.TABLE_NAME_VIDEO, null,
                where, null, null, null, null, null);
        if (cursor != null && !cursor.isAfterLast()) {
            cursor.moveToFirst();
            path = cursor.getString(6);
            cursor.close();
        }
        return path;
    }

    public long deleteVideoEntry(String videoId) {
        String where = DBHelper.COLUMN_VIDEO_VIDEO_ID + "='" + videoId + "'";
        return database.delete(DBHelper.TABLE_NAME_VIDEO, where, null);
    }

    public long createSmsEntry(Sms s) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SMS_ID, s.getId());
        values.put(DBHelper.COLUMN_SMS_THREAD_ID, s.getThread_id());
        values.put(DBHelper.COLUMN_SMS_ADDRESS, s.getAddress());
        values.put(DBHelper.COLUMN_SMS_PERSON, s.getPerson());
        values.put(DBHelper.COLUMN_SMS_TIME, s.getTime());
        values.put(DBHelper.COLUMN_SMS_PROTOCOL, s.getProtocol());
        values.put(DBHelper.COLUMN_SMS_READ, s.getRead());
        values.put(DBHelper.COLUMN_SMS_STATUS, s.getStatus());
        values.put(DBHelper.COLUMN_SMS_TYPE, s.getType());
        values.put(DBHelper.COLUMN_SMS_REPLY_PATH_PRESENT, s.getReplyPathPresent());
        values.put(DBHelper.COLUMN_SMS_SUBJECT, s.getSubject());
        values.put(DBHelper.COLUMN_SMS_MSG, s.getMsg());
        values.put(DBHelper.COLUMN_SMS_SERVICE_CENTER, s.getServiceCenter());
        values.put(DBHelper.COLUMN_SMS_LOCKED, s.getLocked());
        values.put(DBHelper.COLUMN_SMS_ERROR_CODE, s.getErrorCode());
        values.put(DBHelper.COLUMN_SMS_SEEN, s.getSeen());
        values.put(DBHelper.COLUMN_SMS_FOLDER_NAME, s.getFolderName());
        values.put(DBHelper.COLUMN_SMS_CONTACT_NAME, s.getContactName());
        return database.insert(DBHelper.TABLE_NAME_SMS, null, values);
    }

    public long deleteSmsEntry(String id) {
        String where = DBHelper.COLUMN_SMS_ID + "='" + id + "'";
        return database.delete(DBHelper.TABLE_NAME_SMS, where, null);
    }

    public Sms getSmsEntry(String id) {
        String where = DBHelper.COLUMN_SMS_ID + "='" + id + "'";
        Cursor c = database.query(true, DBHelper.TABLE_NAME_SMS, null, where, null,
                null, null, null, null);
        Sms s = new Sms();
        if (c.moveToFirst()) {
            if (!c.isAfterLast()) {
                s.setId(c.getString(0));
                s.setThread_id(c.getString(1));
                s.setAddress(c.getString(2));
                s.setPerson(c.getString(3));
                s.setTime(c.getString(4));
                s.setProtocol(c.getString(5));
                s.setRead(c.getString(6));
                s.setStatus(c.getString(7));
                s.setType(c.getString(8));
                s.setReplyPathPresent(c.getString(9));
                s.setSubject(c.getString(10));
                s.setMsg(c.getString(11));
                s.setServiceCenter(c.getString(12));
                s.setLocked(c.getString(13));
                s.setErrorCode(c.getString(14));
                s.setSeen(c.getString(15));
                s.setFolderName(c.getString(16));
                s.setContactName(c.getString(17));
            }
            c.close();
        }
        return s;
    }
}
