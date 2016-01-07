package com.marcusjacobsson.vault.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.pojos.Video;
import com.marcusjacobsson.vault.util.PicassoHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marcus Jacobsson on 2015-08-15.
 */
public class VideoArrayAdapter extends ArrayAdapter<Video> {

    private ArrayList<Video> videos;
    private Context ctx;
    private Picasso picasso;

    public VideoArrayAdapter(Context ctx, ArrayList<Video> videos) {
        super(ctx, R.layout.list_view_videos_item, videos);
        this.videos = videos;
        this.ctx = ctx;
        this.picasso = PicassoHelper.getPicasso();
    }

    static private class ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView duration;
        TextView size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            row = inflater.inflate(R.layout.list_view_videos_item, parent, false);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) row.findViewById(R.id.iv_lw_videos_thumbnail);
            holder.title = (TextView) row.findViewById(R.id.tv_lw_videos_title);
            holder.duration = (TextView) row.findViewById(R.id.tv_lw_videos_duration);
            holder.size = (TextView) row.findViewById(R.id.tv_lw_videos_size);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Video video = videos.get(position);

        picasso.load(new File(video.getThumbnailPath()))
                .fit()
                .into(holder.thumbnail);

        holder.title.setText(video.getTitle());
        holder.duration.setText(convertMilliSecondsToHMmSs(video.getDuration()));
        holder.size.setText(readableFileSize(video.getSize()));

        return row;

    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
    }

    public static String convertMilliSecondsToHMmSs(String ms) {
        long millis = Long.valueOf(ms);
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String readableFileSize(String stringSize) {
        long size = Long.valueOf(stringSize);
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


}

