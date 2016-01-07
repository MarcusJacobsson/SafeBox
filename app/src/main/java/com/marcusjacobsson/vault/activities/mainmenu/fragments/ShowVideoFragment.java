package com.marcusjacobsson.vault.activities.mainmenu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-09-09.
 */
public class ShowVideoFragment extends Fragment {

    private VideoView vvVideo;
    private String videoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_video, container, false);
        vvVideo = (VideoView) v.findViewById(R.id.vv_show_video);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(vvVideo);
        vvVideo.setMediaController(mediaController);

        if (savedInstanceState != null) {
            this.videoPath = savedInstanceState.getString("savedVideoPath");
            vvVideo.setVideoPath(videoPath);
            vvVideo.seekTo(savedInstanceState.getInt("savedPosition"));
            vvVideo.start();
        } else {
            vvVideo.setVideoPath(videoPath);
            vvVideo.start();
        }

        return v;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.videoPath = args.getString("videoPath");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("savedVideoPath", videoPath);
        outState.putInt("savedPosition", vvVideo.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }
}
