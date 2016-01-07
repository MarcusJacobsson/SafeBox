package com.marcusjacobsson.vault.util;

import android.content.Context;

import com.squareup.picasso.Picasso;

/**
 * Created by Marcus Jacobsson on 2015-10-10.
 */
public class PicassoHelper {

    private static Picasso picasso;

    public static void init(Context ctx){
        picasso = Picasso.with(ctx);
    }

    public static Picasso getPicasso(){
        return picasso;
    }
}
