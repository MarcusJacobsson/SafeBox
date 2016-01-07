package com.marcusjacobsson.vault.activities.mainmenu.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-08-10.
 */
public class PhotosFragment extends Fragment implements
        AdapterView.OnItemClickListener, VaultContentProvider.OnImageQueryResultListener,
        VaultContentProvider.OnMoveImagesListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Image> images;
    private ActionMode mActionMode;
    private ArrayList<Image> checkedImages;
    private VaultContentProvider provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos, container, false);
        getActivity().setTitle(getActivity().getString(R.string.photo_vault));
        setHasOptionsMenu(true);

        images = new ArrayList<>();
        checkedImages = new ArrayList<>();
        provider = new VaultContentProvider(getActivity());
        provider.setOnMoveImagesListener(this);

        gridView = (GridView) v.findViewById(R.id.gv_photos);

        gridView.setOnItemClickListener(this);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridAdapter = new GridViewAdapter(getActivity(), images);
        gridView.setAdapter(gridAdapter);

        VaultContentProvider provider = new VaultContentProvider(getActivity());
        provider.setOnImageQueryResultListener(this);
        provider.getPrivateImages(getActivity());


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.photos_menu, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int count = gridView.getCheckedItemCount();

        if (mActionMode == null) {
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(getActivity().getString(R.string.one_selected_image));
        } else {
            if (count == 0) {
                mActionMode.finish();
            } else if (count == 1) {
                mActionMode.getMenu().findItem(R.id.action_choose_images_show).setVisible(true);
                mActionMode.setTitle(getActivity().getString(R.string.one_selected_image));
            } else if (count > 1) {
                mActionMode.getMenu().findItem(R.id.action_choose_images_show).setVisible(false);
                mActionMode.setTitle(count + " " + getActivity().getString(R.string.selected_images));
            }
        }

        if (gridView.isItemChecked(position)) {
            checkedImages.add(images.get(position));
        } else {
            checkedImages.remove(checkedImages.indexOf(images.get(position)));
        }
    }

    @Override
    public void onImageQueryResult(ArrayList<Image> objects) {
        gridAdapter.clear();
        gridAdapter.addAll(objects);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();

            inflater.inflate(R.menu.context_menu_photos, menu);

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

                    provider.moveImages(getActivity(), VaultContentProvider.MOVE_PRIVATE_TO_PUBLIC, checkedImages);

                    mode.finish();
                    return true;

                case R.id.action_choose_images_show:
                    String path = checkedImages.get(0).getUrl();
                    System.out.println(path);

                    ShowPhotoFragment showPhotoFragment = new ShowPhotoFragment();
                    Bundle args = new Bundle();
                    args.putString("photoPath", path);
                    showPhotoFragment.setArguments(args);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, showPhotoFragment)
                            .addToBackStack(null)
                            .commit();

                    gridView.setItemChecked(gridView.getCheckedItemPosition(), false);
                    checkedImages.clear();
                    mActionMode.finish();

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
        if (result && images != null && images.size() > 0) {
            for (Image image : images) {
                gridAdapter.remove(image);
            }
            int count = images.size();
            Toast.makeText(getActivity(), count + " " + getActivity().getString(R.string.images_moved_to_public), Toast.LENGTH_LONG).show();
            gridAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.could_not_move_images), Toast.LENGTH_SHORT).show();
        }

    }
}
