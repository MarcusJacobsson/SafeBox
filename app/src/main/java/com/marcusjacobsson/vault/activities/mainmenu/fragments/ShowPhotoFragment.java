package com.marcusjacobsson.vault.activities.mainmenu.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-09-09.
 */
public class ShowPhotoFragment extends Fragment {

    private ImageView ivPhoto;
    private String photoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_photo, container, false);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_show_image);

        if(savedInstanceState != null){
            this.photoPath = savedInstanceState.getString("savedPhotoPath");
        }
        ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));

        return v;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.photoPath = args.getString("photoPath");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("savedPhotoPath", photoPath);
        super.onSaveInstanceState(outState);
    }
}
