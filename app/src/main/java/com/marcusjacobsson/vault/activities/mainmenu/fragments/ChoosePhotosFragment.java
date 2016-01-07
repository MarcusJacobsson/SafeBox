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
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.adapters.GridViewAdapter;
import com.marcusjacobsson.vault.pojos.Image;
import com.marcusjacobsson.vault.util.VaultContentProvider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-08-11.
 */
public class ChoosePhotosFragment extends Fragment implements VaultContentProvider.OnImageQueryResultListener,
        AdapterView.OnItemClickListener, VaultContentProvider.OnMoveImagesListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Image> images;
    private ArrayList<Image> checkedImages;
    private ActionMode mActionMode;
    private VaultContentProvider provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos, container, false);
        getActivity().setTitle(getActivity().getString(R.string.choose_photos));

        images = new ArrayList<>();
        checkedImages = new ArrayList<>();
        gridView = (GridView) v.findViewById(R.id.gv_photos);

        gridAdapter = new GridViewAdapter(getActivity(), images);
        gridView.setAdapter(gridAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridView.setOnItemClickListener(this);

        provider = new VaultContentProvider(getActivity());
        provider.setOnImageQueryResultListener(this);
        provider.setOnMoveImagesListener(this);
        provider.getPublicImages(getActivity());

        return v;
    }

    @Override
    public void onImageQueryResult(ArrayList<Image> objects) {
        this.images = objects;
        gridAdapter.clear();
        gridAdapter.addAll(objects);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int count = gridView.getCheckedItemCount();

        if(mActionMode == null){
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(getActivity().getString(R.string.one_selected_image));
        }else{
            if(count == 0){
                mActionMode.finish();
            }else if(count == 1){
                mActionMode.setTitle(getActivity().getString(R.string.one_selected_image));
            }else if(count > 1){
                mActionMode.setTitle(count + " " + getActivity().getString(R.string.selected_images));
            }
        }

        if(gridView.isItemChecked(position)){
            checkedImages.add(images.get(position));
        }else{
            checkedImages.remove(checkedImages.indexOf(images.get(position)));
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_photos, menu);
            menu.findItem(R.id.action_choose_images_show).setVisible(false);
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
                case R.id.action_choose_images_done:

                    provider.moveImages(getActivity(), VaultContentProvider.MOVE_PUBLIC_TO_PRIVATE, checkedImages);

                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gridView.clearChoices();
            for (int i = 0; i < gridView.getChildCount(); i++) {
                ((Checkable) gridView.getChildAt(i)).setChecked(false);
            }
            mActionMode = null;
        }
    };

    @Override
    public void onMoveImagesComplete(boolean result, ArrayList<Image> images) {
        if(result && images != null && images.size() > 0){
            for(Image image : images){
                gridAdapter.remove(image);
            }
            int count = images.size();
            Toast.makeText(getActivity(), count + " " + getActivity().getString(R.string.images_moved_to_private), Toast.LENGTH_LONG).show();
            gridAdapter.notifyDataSetChanged();
            checkedImages.clear();
        }else{
            Toast.makeText(getActivity(), getActivity().getString(R.string.could_not_move_images), Toast.LENGTH_SHORT).show();
        }
    }
}
