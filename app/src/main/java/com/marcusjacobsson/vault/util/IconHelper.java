package com.marcusjacobsson.vault.util;

import android.content.Context;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-10-11.
 */
public class IconHelper {

    public static int getIconResourceId(String contactName) {

        if (contactName != null && !contactName.equals("")) {

            String firstLetter = contactName.substring(0, 1);

            switch (firstLetter.toLowerCase()) {
                case "a":
                    return R.mipmap.a;

                case "b":
                    return R.mipmap.b;

                case "c":
                    return R.mipmap.c;

                case "d":
                    return R.mipmap.d;

                case "e":
                    return R.mipmap.e;

                case "f":
                    return R.mipmap.f;

                case "g":
                    return R.mipmap.g;

                case "h":
                    return R.mipmap.h;

                case "i":
                    return R.mipmap.i;

                case "j":
                    return R.mipmap.j;

                case "k":
                    return R.mipmap.k;

                case "l":
                    return R.mipmap.l;

                case "m":
                    return R.mipmap.m;

                case "n":
                    return R.mipmap.n;

                case "o":
                    return R.mipmap.o;

                case "p":
                    return R.mipmap.p;

                case "q":
                    return R.mipmap.q;

                case "r":
                    return R.mipmap.r;

                case "s":
                    return R.mipmap.s;

                case "t":
                    return R.mipmap.t;

                case "u":
                    return R.mipmap.u;

                case "v":
                    return R.mipmap.v;

                case "w":
                    return R.mipmap.w;

                case "x":
                    return R.mipmap.x;

                case "y":
                    return R.mipmap.y;

                case "z":
                    return R.mipmap.z;

                default:
                    return R.mipmap.unknown;

            }
        } else {
            return R.mipmap.unknown;
        }
    }
}
