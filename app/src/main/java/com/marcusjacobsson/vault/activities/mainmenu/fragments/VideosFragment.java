package com.marcusjacobsson.vault.activities.mainmenu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.adapters.VideoArrayAdapter;
import com.marcusjacobsson.vault.pojos.Video;
import com.marcusjacobsson.vault.util.VaultContentProvider;

import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-08-10.
 */
public class VideosFragment extends Fragment implements AdapterView.OnItemClickListener,
        VaultContentProvider.OnVideoQueryResultListener, VaultContentProvider.OnMoveVideosListener {

    private ListView lwVideos;
    private VideoArrayAdapter adapter;
    private ArrayList<Video> videos;
    private ArrayList<Video> checkedVideos;
    private ActionMode mActionMode;
    private VaultContentProvider provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videos, container, false);
        getActivity().setTitle(getActivity().getString(R.string.video_vault));
        setHasOptionsMenu(true);

        provider = new VaultContentProvider(getActivity());

        videos = new ArrayList<>();
        checkedVideos = new ArrayList<>();

        lwVideos = (ListView) v.findViewById(R.id.lw_videos);

        lwVideos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new VideoArrayAdapter(getActivity(), videos);
        lwVideos.setAdapter(adapter);

        lwVideos.setOnItemClickListener(this);

        provider.setOnVideoQueryResultListener(this);
        provider.setOnMoveVideosListener(this);
        provider.getPrivateVideos(getActivity());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.videos_menu, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int count = lwVideos.getCheckedItemCount();

        if (mActionMode == null) {
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(getActivity().getString(R.string.one_selected_video));
        } else {
            if (count == 0) {
                mActionMode.finish();
            } else if (count == 1) {
                mActionMode.getMenu().getItem(R.id.action_choose_videos_show).setVisible(true);
                mActionMode.setTitle(getActivity().getString(R.string.one_selected_video));
            } else if (count > 1) {
                mActionMode.getMenu().getItem(R.id.action_choose_videos_show).setVisible(false);
                mActionMode.setTitle(count + " " + getActivity().getString(R.string.selected_videos));
            }
        }

        if(lwVideos.isItemChecked(position)){
            checkedVideos.add(videos.get(position));
        }else{
            checkedVideos.remove(checkedVideos.indexOf(videos.get(position)));
        }

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_videos, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_choose_videos_done:

                    provider.moveVideos(getActivity(), VaultContentProvider.MOVE_PRIVATE_TO_PUBLIC, checkedVideos);

                    mode.finish();
                    return true;

                case R.id.action_choose_videos_show:
                    String path = checkedVideos.get(0).getUrl();
                    System.out.println(path);

                    ShowVideoFragment showVideoFragment = new ShowVideoFragment();
                    Bundle args = new Bundle();
                    args.putString("videoPath", path);
                    showVideoFragment.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, showVideoFragment)
                            .addToBackStack(null)
                            .commit();

                    lwVideos.setItemChecked(lwVideos.getCheckedItemPosition(), false);
                    checkedVideos.clear();
                    mActionMode.finish();
                    return true;

                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            lwVideos.clearChoices();
            for (int i = 0; i < lwVideos.getChildCount(); i++) {
                ((Checkable) lwVideos.getChildAt(i)).setChecked(false);
            }
            mActionMode = null;
        }
    };

    @Override
    public void onVideoQueryResult(ArrayList<Video> videos) {
        adapter.clear();
        adapter.addAll(videos);
    }

    @Override
    public void onMoveVideosComplete(boolean result, ArrayList<Video> videos) {
        if(result && videos != null && videos.size() > 0){
            for(Video video : videos){
                adapter.remove(video);
            }
            int count = videos.size();
            Toast.makeText(getActivity(), count + " " + getActivity().getString(R.string.videos_moved_to_public), Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();

        }else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.could_not_move_videos), Toast.LENGTH_SHORT).show();
        }

        checkedVideos.clear();
    }
}
