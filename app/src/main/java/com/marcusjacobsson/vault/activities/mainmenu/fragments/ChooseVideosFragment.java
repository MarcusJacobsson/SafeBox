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
 * Created by Marcus Jacobsson on 2015-08-15.
 */
public class ChooseVideosFragment extends Fragment implements AdapterView.OnItemClickListener, VaultContentProvider.OnVideoQueryResultListener, VaultContentProvider.OnMoveVideosListener {

    private ListView listView;
    private ArrayAdapter<Video> adapter;
    private ArrayList<Video> allVideos;
    private ArrayList<Video> checkedVideos;
    private ActionMode mActionMode;
    private VaultContentProvider provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_videos, container, false);
        getActivity().setTitle(getActivity().getString(R.string.choose_videos));

        allVideos = new ArrayList<>();
        checkedVideos = new ArrayList<>();
        listView = (ListView) v.findViewById(R.id.lw_videos);
        adapter = new VideoArrayAdapter(getActivity(), allVideos);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);

        provider = new VaultContentProvider(getActivity());
        provider.setOnVideoQueryResultListener(this);
        provider.setOnMoveVideosListener(this);
        provider.getPublicVideos(getActivity());

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int count = listView.getCheckedItemCount();

        if(mActionMode == null){
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(getActivity().getString(R.string.one_selected_video));
        }else{
            if(count == 0){
                mActionMode.finish();
            }else if(count == 1){
                mActionMode.setTitle(getActivity().getString(R.string.one_selected_video));
            }else if(count > 1){
                mActionMode.setTitle(count + " " + getActivity().getString(R.string.selected_videos));
            }
        }

        if(listView.isItemChecked(position)){
            checkedVideos.add(allVideos.get(position));
        }else{
            checkedVideos.remove(checkedVideos.indexOf(allVideos.get(position)));
        }
    }

    @Override
    public void onVideoQueryResult(ArrayList<Video> videos) {
        this.allVideos = videos;
        adapter.clear();
        adapter.addAll(videos);
    }

    @Override
    public void onMoveVideosComplete(boolean result, ArrayList<Video> videos) {
        if(result && videos != null && videos.size() > 0){
            for(Video video : videos){
                adapter.remove(video);
                allVideos.remove(video);
            }
            int count = videos.size();
            Toast.makeText(getActivity(), count + " " + getActivity().getString(R.string.videos_moved_to_private), Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();

        }else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.could_not_move_videos), Toast.LENGTH_SHORT).show();
        }
        checkedVideos.clear();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_videos, menu);
            menu.findItem(R.id.action_choose_videos_show).setVisible(false);
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

                    provider.moveVideos(getActivity(), VaultContentProvider.MOVE_PUBLIC_TO_PRIVATE, checkedVideos);

                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listView.clearChoices();
            for (int i = 0; i < listView.getChildCount(); i++) {
                ((Checkable) listView.getChildAt(i)).setChecked(false);
            }
            mActionMode = null;
        }
    };
}
