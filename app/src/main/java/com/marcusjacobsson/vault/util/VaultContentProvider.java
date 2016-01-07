package com.marcusjacobsson.vault.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.database.DataSource;
import com.marcusjacobsson.vault.pojos.Image;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.pojos.Video;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-08-11.
 */
public class VaultContentProvider {

    private static final String LOG_TAG = "VaultContentProvider";

    private static final String IMAGES_PRIVATE_DIR_NAME = "vault_images";
    private static final String VIDEOS_PRIVATE_DIR_NAME = "vault_videos";

    public static final int MOVE_PRIVATE_TO_PUBLIC = 0;
    public static final int MOVE_PUBLIC_TO_PRIVATE = 1;

    private DataSource dataSource;
    private ProgressDialog progressDialog;

    private GetPublicImagesTask getPublicImagesTask;
    private GetPrivateImagesTask getPrivateImagesTask;
    private GetPublicVideosTask getPublicVideosTask;
    private GetPrivateVideosTask getPrivateVideosTask;
    private GetPublicSmsTask getPublicSmsTask;
    private GetPrivateSmsTask getPrivateSmsTask;
    private MoveImagesTask moveImagesTask;
    private MoveVideosTask moveVideosTask;
    private MoveSmsTask moveSmsTask;

    private OnImageQueryResultListener onImageQueryResultListener;
    private OnMoveImagesListener onMoveImagesListener;
    private OnVideoQueryResultListener onVideoQueryResultListener;
    private OnMoveVideosListener onMoveVideosListener;
    private OnSmsQueryResultListener onSmsQueryResultListener;
    private OnMoveSmsListener onMoveSmsListener;

    public interface OnImageQueryResultListener {
        void onImageQueryResult(ArrayList<Image> objects);
    }

    public interface OnMoveImagesListener {
        void onMoveImagesComplete(boolean result, ArrayList<Image> images);
    }

    public interface OnVideoQueryResultListener {
        void onVideoQueryResult(ArrayList<Video> videos);
    }

    public interface OnMoveVideosListener {
        void onMoveVideosComplete(boolean result, ArrayList<Video> images);
    }

    public interface OnSmsQueryResultListener {
        void onSmsQueryResult(ArrayList<Sms> sms);
    }

    public interface OnMoveSmsListener {
        void onMoveSmsComplete(boolean result, ArrayList<Sms> sms);
    }

    public void setOnImageQueryResultListener(OnImageQueryResultListener onImageQueryResultListener) {
        this.onImageQueryResultListener = onImageQueryResultListener;
    }

    public void setOnMoveImagesListener(OnMoveImagesListener onMoveImagesListener) {
        this.onMoveImagesListener = onMoveImagesListener;
    }

    public void setOnVideoQueryResultListener(OnVideoQueryResultListener onVideoQueryResultListener) {
        this.onVideoQueryResultListener = onVideoQueryResultListener;
    }

    public void setOnMoveVideosListener(OnMoveVideosListener onMoveVideosListener) {
        this.onMoveVideosListener = onMoveVideosListener;
    }

    public void setOnSmsQueryResultListener(OnSmsQueryResultListener onSmsQueryResultListener) {
        this.onSmsQueryResultListener = onSmsQueryResultListener;
    }

    public void setOnMoveSmsListener(OnMoveSmsListener onMoveSmsListener) {
        this.onMoveSmsListener = onMoveSmsListener;
    }

    public VaultContentProvider(Context ctx) {
        dataSource = new DataSource(ctx);

        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(ctx.getString(R.string.hash_progress_bar_message));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (getPublicImagesTask != null) {
                    getPublicImagesTask.cancel(true);
                } else if (getPrivateImagesTask != null) {
                    getPrivateImagesTask.cancel(true);
                } else if (moveImagesTask != null) {
                    moveImagesTask.cancel(true);
                } else if (getPublicVideosTask != null) {
                    getPublicVideosTask.cancel(true);
                } else if (getPrivateVideosTask != null) {
                    getPrivateVideosTask.cancel(true);
                } else if (getPrivateSmsTask != null) {
                    getPrivateSmsTask.cancel(true);
                } else if (getPublicSmsTask != null) {
                    getPublicSmsTask.cancel(true);
                } else if (moveSmsTask != null) {
                    moveSmsTask.cancel(true);
                }
            }
        });
    }

    public void getPublicImages(Context ctx) {
        getPublicImagesTask = new GetPublicImagesTask(ctx);
        getPublicImagesTask.execute();
    }

    public void getPrivateImages(Context ctx) {
        getPrivateImagesTask = new GetPrivateImagesTask(ctx);
        getPrivateImagesTask.execute();
    }

    public void moveImages(Context ctx, int choice, ArrayList<Image> images) {
        moveImagesTask = new MoveImagesTask(ctx, choice, images);
        moveImagesTask.execute();
    }

    public void getPublicVideos(Context ctx) {
        getPublicVideosTask = new GetPublicVideosTask(ctx);
        getPublicVideosTask.execute();
    }

    public void getPrivateVideos(Context ctx) {
        getPrivateVideosTask = new GetPrivateVideosTask(ctx);
        getPrivateVideosTask.execute();
    }

    public void moveVideos(Context ctx, int choice, ArrayList<Video> videos) {
        moveVideosTask = new MoveVideosTask(ctx, choice, videos);
        moveVideosTask.execute();
    }

    public void getPrivateSms(Context ctx) {
        getPrivateSmsTask = new GetPrivateSmsTask(ctx);
        getPrivateSmsTask.execute();
    }

    public void getPublicSms(Context ctx) {
        getPublicSmsTask = new GetPublicSmsTask(ctx);
        getPublicSmsTask.execute();
    }

    public void moveSms(Context ctx, int choice, ArrayList<Sms> sms) {
        moveSmsTask = new MoveSmsTask(ctx, choice, sms);
        moveSmsTask.execute();
    }

    private class GetPublicImagesTask extends AsyncTask<String, String, ArrayList<Image>> {

        private Context ctx;

        public GetPublicImagesTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Image> doInBackground(String... params) {
            ArrayList<Image> objects = new ArrayList<>();

            String[] mProjection = {MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA};

            ContentResolver cr = ctx.getContentResolver();
            Cursor mCursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mProjection,
                    null,
                    null,
                    MediaStore.Images.Media.DEFAULT_SORT_ORDER);

            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                Image tmpImage = new Image();
                tmpImage.setTitle(mCursor.getString(0));
                tmpImage.setUrl(mCursor.getString(1));
                objects.add(tmpImage);
                mCursor.moveToNext();
            }

            mCursor.close();
            return objects;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> objects) {
            super.onPostExecute(objects);
            progressDialog.dismiss();

            if (onImageQueryResultListener != null) {
                onImageQueryResultListener.onImageQueryResult(objects);
            } else {
                System.out.println("Måste implementera OnImageQueryResultListener!");
            }

            getPublicImagesTask = null;
        }
    }

    private class GetPrivateImagesTask extends AsyncTask<String, String, ArrayList<Image>> {

        private Context ctx;

        public GetPrivateImagesTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Image> doInBackground(String... params) {
            ArrayList<Image> images = new ArrayList<>();

            //Get (or create) the videos directory
            File vaultDir = ctx.getDir(IMAGES_PRIVATE_DIR_NAME, Context.MODE_PRIVATE);
            String dirPath = vaultDir.getAbsolutePath();

            //Get an array of file names
            String[] fileList = vaultDir.list();

            for (String filename : fileList) {
                Image tmpImage = new Image();
                tmpImage.setUrl(dirPath + File.separator + filename);
                tmpImage.setTitle(filename);
                images.add(tmpImage);
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Image> objects) {
            super.onPostExecute(objects);
            progressDialog.dismiss();

            if (onImageQueryResultListener != null) {
                onImageQueryResultListener.onImageQueryResult(objects);
            } else {
                System.out.println("Måste implementera OnImageQueryResultListener!");
            }

            getPrivateImagesTask = null;
        }
    }

    private class GetPublicVideosTask extends AsyncTask<String, String, ArrayList<Video>> {

        private Context ctx;

        public GetPublicVideosTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Video> doInBackground(String... params) {
            ArrayList<Video> objects = new ArrayList<>();

            String[] mProjection = {MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA, MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DURATION, MediaStore.Video.Media._ID};

            ContentResolver cr = ctx.getContentResolver();
            Cursor mCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mProjection,
                    null,
                    null,
                    MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            if (mCursor != null) {
                mCursor.moveToFirst();

                while (!mCursor.isAfterLast()) {
                    Video tmpVideo = new Video();
                    tmpVideo.setTitle(mCursor.getString(0));
                    tmpVideo.setUrl(mCursor.getString(1));
                    tmpVideo.setSize(mCursor.getString(2));
                    tmpVideo.setDuration(mCursor.getString(3));
                    tmpVideo.setId(mCursor.getString(4));
                    tmpVideo.setThumbnailPath(getVideoThumbnailPath(ctx, mCursor.getString(4)));
                    objects.add(tmpVideo);
                    mCursor.moveToNext();
                }

                mCursor.close();
            }

            return objects;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> objects) {
            super.onPostExecute(objects);
            progressDialog.dismiss();

            if (onVideoQueryResultListener != null) {
                onVideoQueryResultListener.onVideoQueryResult(objects);
            } else {
                System.out.println("Måste implementera onVideoQueryResultListener!");
            }

            getPublicVideosTask = null;
        }
    }

    private class GetPrivateVideosTask extends AsyncTask<String, String, ArrayList<Video>> {

        private Context ctx;

        public GetPrivateVideosTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Video> doInBackground(String... params) {
            ArrayList<Video> videos = new ArrayList<>();

            File vaultDir = ctx.getDir(VIDEOS_PRIVATE_DIR_NAME, Context.MODE_PRIVATE);
            String dirPath = vaultDir.getAbsolutePath();

            String[] fileList = vaultDir.list();

            try {
                dataSource.open();
                for (String fileName : fileList) {
                    Video video = dataSource.getVideoEntry(fileName);
                    video.setUrl(dirPath + File.separator + video.getTitle());
                    videos.add(video);
                }
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            super.onPostExecute(videos);
            progressDialog.dismiss();

            if (onVideoQueryResultListener != null) {
                onVideoQueryResultListener.onVideoQueryResult(videos);
            } else {
                System.out.println("Måste implementera onVideoQueryResultListener!");
            }

            getPrivateVideosTask = null;
        }
    }

    private class GetPublicSmsTask extends AsyncTask<String, String, ArrayList<Sms>> {

        private Context ctx;

        public GetPublicSmsTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Sms> doInBackground(String... params) {
            ArrayList<Sms> smses = new ArrayList<>();
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = ctx.getContentResolver();

            Cursor c = cr.query(message, null, null, null, null);

            if (c != null && c.moveToFirst()) {
                int totalSMS = c.getCount();
                for (int i = 0; i < totalSMS; i++) {

                    Sms tmpSms = new Sms();
                    tmpSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    tmpSms.setThread_id(c.getString(c.getColumnIndexOrThrow("thread_id")));
                    tmpSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                    tmpSms.setPerson(c.getString(c.getColumnIndexOrThrow("person")));
                    tmpSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    tmpSms.setProtocol(c.getString(c.getColumnIndexOrThrow("protocol")));
                    tmpSms.setRead(c.getString(c.getColumnIndex("read")));
                    tmpSms.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
                    tmpSms.setType(c.getString(c.getColumnIndexOrThrow("type")));
                    tmpSms.setReplyPathPresent(c.getString(c.getColumnIndexOrThrow("reply_path_present")));
                    tmpSms.setSubject(c.getString(c.getColumnIndexOrThrow("subject")));
                    tmpSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    tmpSms.setServiceCenter(c.getString(c.getColumnIndexOrThrow("service_center")));
                    tmpSms.setLocked(c.getString(c.getColumnIndexOrThrow("locked")));
                    tmpSms.setErrorCode(c.getString(c.getColumnIndexOrThrow("error_code")));
                    tmpSms.setSeen(c.getString(c.getColumnIndexOrThrow("seen")));

                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        tmpSms.setFolderName("inbox");
                    } else {
                        tmpSms.setFolderName("sent");
                    }
                    tmpSms.setContactName(getContactName(ctx, tmpSms.getAddress()));

                    smses.add(tmpSms);
                    c.moveToNext();
                }
                c.close();
            }
            return smses;
        }

        @Override
        protected void onPostExecute(ArrayList<Sms> sms) {
            super.onPostExecute(sms);
            progressDialog.dismiss();

            if (onSmsQueryResultListener != null) {
                onSmsQueryResultListener.onSmsQueryResult(sms);
            } else {
                System.out.println("Måste implementera onSmsQueryResultListener!");
            }

            getPublicSmsTask = null;
        }
    }

    private class GetPrivateSmsTask extends AsyncTask<String, String, ArrayList<Sms>> {

        private Context ctx;

        public GetPrivateSmsTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ArrayList<Sms> doInBackground(String... params) {
            //TODO: implementera
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<Sms> smses) {
            super.onPostExecute(smses);
            progressDialog.dismiss();

            if (onSmsQueryResultListener != null) {
                onSmsQueryResultListener.onSmsQueryResult(smses);
            } else {
                System.out.println("Måste implementera onSmsQueryResultListener!");
            }

            getPrivateSmsTask = null;
        }
    }

    private class MoveImagesTask extends AsyncTask<Integer, String, Boolean> {

        private Context ctx;
        private int choice;
        private ArrayList<Image> images;

        public MoveImagesTask(Context ctx, int choice, ArrayList<Image> images) {
            this.ctx = ctx;
            this.choice = choice;
            this.images = images;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            File privateImagesDir = ctx.getDir(IMAGES_PRIVATE_DIR_NAME, Context.MODE_PRIVATE);
            String privateImagesDirPath = privateImagesDir.getAbsolutePath();

            if (choice == MOVE_PRIVATE_TO_PUBLIC) {

                try {
                    dataSource.open();

                    for (Image image : images) {
                        String oldPath = dataSource.getImageEntryOldPath(image.getTitle());
                        File destination = new File(oldPath);
                        File source = new File(privateImagesDirPath + File.separator + image.getTitle());
                        FileUtils.moveFile(source, destination);

                        //refresh content provider
                        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destination)));

                        dataSource.deleteImageEntry(image.getTitle());
                    }
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;

            } else if (choice == MOVE_PUBLIC_TO_PRIVATE) {

                try {
                    dataSource.open();

                    for (Image image : images) {
                        dataSource.createImageEntry(image.getTitle(), image.getUrl());

                        File source = new File(image.getUrl());
                        File destination = new File(privateImagesDirPath + File.separator + image.getTitle());

                        FileUtils.copyFile(source, destination);
                        String selection = MediaStore.Images.Media.DATA + "='" + source.getAbsolutePath() + "'";
                        ctx.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, null);

                    }

                    dataSource.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (onMoveImagesListener != null) {
                onMoveImagesListener.onMoveImagesComplete(result, images);
            } else {
                System.out.println("Must implement onMoveImagesListener!");
            }

            moveImagesTask = null;
        }
    }

    private class MoveVideosTask extends AsyncTask<Integer, String, Boolean> {

        private Context ctx;
        private int choice;
        private ArrayList<Video> videos;

        public MoveVideosTask(Context ctx, int choice, ArrayList<Video> videos) {
            this.ctx = ctx;
            this.choice = choice;
            this.videos = videos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            File privateVideosDir = ctx.getDir(VIDEOS_PRIVATE_DIR_NAME, Context.MODE_PRIVATE);
            String privateVideosDirPath = privateVideosDir.getAbsolutePath();

            if (choice == MOVE_PRIVATE_TO_PUBLIC) {

                try {
                    dataSource.open();

                    for (Video video : videos) {
                        String oldPath = dataSource.getVideoEntryOldPath(video.getTitle());
                        File destination = new File(oldPath);
                        File source = new File(privateVideosDirPath + File.separator + video.getTitle());
                        FileUtils.moveFile(source, destination);

                        //refresh content provider
                        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destination)));

                        dataSource.deleteVideoEntry(video.getId());
                    }
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;

            } else if (choice == MOVE_PUBLIC_TO_PRIVATE) {

                try {
                    dataSource.open();

                    for (Video video : videos) {
                        dataSource.createVideoEntry(video.getId(), video.getThumbnailPath(),
                                video.getDuration(), video.getSize(), video.getTitle(), video.getUrl());

                        File source = new File(video.getUrl());
                        File destination = new File(privateVideosDirPath + File.separator + video.getTitle());

                        FileUtils.copyFile(source, destination);
                        String selection = MediaStore.Video.Media.DATA + "='" + source.getAbsolutePath() + "'";
                        ctx.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection, null);

                    }

                    dataSource.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (onMoveVideosListener != null) {
                onMoveVideosListener.onMoveVideosComplete(result, videos);
            } else {
                System.out.println("Must implement onMoveVideosListener!");
            }

            moveVideosTask = null;
        }
    }

    private class MoveSmsTask extends AsyncTask<Integer, String, Boolean> {

        private Context ctx;
        private int choice;
        private ArrayList<Sms> sms;

        public MoveSmsTask(Context ctx, int choice, ArrayList<Sms> sms) {
            this.ctx = ctx;
            this.choice = choice;
            this.sms = sms;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            if (choice == MOVE_PRIVATE_TO_PUBLIC) {

                //TODO: implement

            } else if (choice == MOVE_PUBLIC_TO_PRIVATE) {
                try {
                    dataSource.open();
                    for (Sms s : sms) {
                        dataSource.createSmsEntry(s);
                    }
                    deleteSms(ctx, sms);
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (onMoveSmsListener != null) {
                onMoveSmsListener.onMoveSmsComplete(result, sms);
            } else {
                System.out.println("Must implement onMoveSmsListener!");
            }

            moveSmsTask = null;
        }
    }

    private String getVideoThumbnailPath(Context ctx, String videoId) {
        String[] mProjection = {MediaStore.Video.Thumbnails.DATA};
        String mSelection = MediaStore.Video.Thumbnails.VIDEO_ID + "='" + videoId + "'";

        ContentResolver cr = ctx.getContentResolver();
        MediaStore.Video.Thumbnails.getThumbnail(cr, Long.valueOf(videoId), MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor mCursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                mProjection,
                mSelection,
                null,
                null);

        String path = "";
        if (mCursor.moveToFirst())
            path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

        mCursor.close();
        return path;
    }

    private static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private void deleteSms(Context ctx, ArrayList<Sms> sms) {
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            Log.d(LOG_TAG, "KitKat, using SmsWriteOpUtil.");

            if(!SmsWriteOpUtil.isWriteEnabled(ctx)) {
                SmsWriteOpUtil.setWriteEnabled(ctx, true);
            }

            for (Sms s : sms) {
                Uri smsUri = Uri.parse("content://sms/" + s.getId());
                int rows = ctx.getContentResolver().delete(smsUri, null, null);
                if (rows == 0) {
                    Log.d(LOG_TAG, "Could not delete sms: " + s.getId());
                } else {
                    Log.d(LOG_TAG, "Deleted sms: " + s.getId());
                }
            }
        } else if(Build.VERSION.SDK_INT >= 21){
            Log.d(LOG_TAG, "Lollipop or newer, asking for permission.");

            //Query the default sms app
            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(ctx);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("defaultSmsApp", defaultSmsApp);
            edit.apply();

            //Ask the user to change the default sms app to vault (then restore it when the move is complete)
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, ctx.getPackageName());
            ctx.startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Older version than KitKat, deleting directly.");
            for (Sms s : sms) {
                Uri smsUri = Uri.parse("content://sms/" + s.getId());
                int rows = ctx.getContentResolver().delete(smsUri, null, null);
                if (rows == 0) {
                    Log.d(LOG_TAG, "Could not delete sms: " + s.getId());
                } else {
                    Log.d(LOG_TAG, "Deleted sms: " + s.getId());
                }
            }
        }
    }
}
