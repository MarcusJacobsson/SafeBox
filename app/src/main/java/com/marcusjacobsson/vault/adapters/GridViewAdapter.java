package com.marcusjacobsson.vault.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.pojos.Image;
import com.marcusjacobsson.vault.util.PicassoHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-08-10.
 */
public class GridViewAdapter extends ArrayAdapter<Image> {

    private Context ctx;
    private ArrayList<Image> objects;
    private Picasso picasso;
    //private ImageRequestHandler imageRequestHandler;

    public GridViewAdapter(Context ctx, ArrayList<Image> objects) {
        super(ctx, R.layout.grid_view_item, objects);
        this.ctx = ctx;
        this.objects = objects;
        //this.imageRequestHandler = new ImageRequestHandler();
        /*this.picasso = new Picasso.Builder(ctx.getApplicationContext())
                .addRequestHandler(imageRequestHandler)
                .build();*/
        this.picasso = PicassoHelper.getPicasso();
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_view_item, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Image image = objects.get(position);
        holder.imageTitle.setText(image.getTitle());

       /* picasso.load(imageRequestHandler.SCHEME_IMAGE + ":" + image.getUrl())
                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit()
                .into(holder.image);*/

        picasso.load(new File(image.getUrl()))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit()
                .into(holder.image);

        return row;
    }

    /*private class ImageRequestHandler extends RequestHandler {
        public String SCHEME_IMAGE = "image";

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_IMAGE.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(data.uri.getPath()), 512, 512, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }

    }*/
}
