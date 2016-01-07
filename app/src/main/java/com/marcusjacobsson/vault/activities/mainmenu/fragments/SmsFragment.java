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
import android.widget.ListView;
import android.widget.Toast;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.adapters.SmsArrayAdapter;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.util.TimeHelper;
import com.marcusjacobsson.vault.util.VaultContentProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Marcus Jacobsson on 2015-10-08.
 */
public class SmsFragment extends Fragment implements AdapterView.OnItemClickListener, VaultContentProvider.OnMoveSmsListener, VaultContentProvider.OnSmsQueryResultListener {

    private ListView lwSms;
    private SmsArrayAdapter adapter;
    private ArrayList<Sms> allSms;
    private ArrayList<Sms> checkedSms;
    private ActionMode mActionMode;
    private VaultContentProvider provider;
    private ArrayList<Object> objSmsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sms, container, false);
        getActivity().setTitle(getActivity().getString(R.string.sms_vault));
        setHasOptionsMenu(true);

        provider = new VaultContentProvider(getActivity());

        allSms = new ArrayList<>();
        checkedSms = new ArrayList<>();

        lwSms = (ListView) v.findViewById(R.id.lw_sms);

        lwSms.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        objSmsList = getSmsObjectArrayList(allSms);
        adapter = new SmsArrayAdapter(getActivity(), objSmsList);
        lwSms.setAdapter(adapter);

        lwSms.setOnItemClickListener(this);

        provider.setOnMoveSmsListener(this);
        provider.setOnSmsQueryResultListener(this);
        provider.getPrivateSms(getActivity());


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sms_menu, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int count = lwSms.getCheckedItemCount();

        if (mActionMode == null) {
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(getActivity().getString(R.string.one_selected_sms));
        } else {
            if (count == 0) {
                mActionMode.finish();
            } else if (count == 1) {
                mActionMode.getMenu().getItem(R.id.action_choose_sms_show).setVisible(true);
                mActionMode.setTitle(getActivity().getString(R.string.one_selected_sms));
            } else if (count > 1) {
                mActionMode.getMenu().getItem(R.id.action_choose_sms_show).setVisible(false);
                mActionMode.setTitle(count + " " + getActivity().getString(R.string.selected_sms));
            }
        }

        if (lwSms.isItemChecked(position)) {
            checkedSms.add((Sms) objSmsList.get(position));
        } else {
            checkedSms.remove((Sms)objSmsList.get(position));
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_sms, menu);
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
                    //TODO: implement
                    mode.finish();
                    return true;

                case R.id.action_choose_sms_show:
                    //TODO: Implement
                    return true;

                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            lwSms.clearChoices();
            for (int i = 0; i < lwSms.getChildCount(); i++) {
                if (lwSms.getChildAt(i) instanceof Checkable)
                    ((Checkable) lwSms.getChildAt(i)).setChecked(false);
            }
            mActionMode = null;
        }
    };

    @Override
    public void onMoveSmsComplete(boolean result, ArrayList<Sms> sms) {
        if (result && sms != null && sms.size() > 0) {
            for (Sms s : sms) {
                adapter.remove(s);
                allSms.remove(s);
            }
            int count = sms.size();
            Toast.makeText(getActivity(), count + " " + getActivity().getString(R.string.sms_moved_to_private), Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.could_not_move_sms), Toast.LENGTH_SHORT).show();
        }
        checkedSms.clear();
    }

    @Override
    public void onSmsQueryResult(ArrayList<Sms> sms) {
        adapter.clear();
        adapter.addAll(sms);
        lwSms.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lwSms.setSelection(adapter.getCount() - 1);
            }
        });

    }

    private ArrayList<Object> getSmsObjectArrayList(ArrayList<Sms> sms) {

        ArrayList<Object> smsObjectArrayList = new ArrayList<>();

        //Sort by time
        Collections.sort(sms, new Comparator<Sms>() {
            @Override
            public int compare(Sms lhs, Sms rhs) {
                return lhs.getTime().compareTo(rhs.getTime());
            }
        });

        //Format time string for each sms
        for (Sms s : sms) {
            s.setTime(TimeHelper.makeTimeString(Long.valueOf(s.getTime())));
        }

        smsObjectArrayList.addAll(sms);

        ArrayList<String> headers = new ArrayList<>();

        //Find the unique times to be used as headers
        for (Sms s : sms) {
            if (!headers.contains(s.getTime())) {
                headers.add(s.getTime());
            }
        }

        for (String header : headers) {
            smsObjectArrayList.add(getIndexOfFirstSms(smsObjectArrayList, header), header);
        }

        return smsObjectArrayList;
    }

    private int getIndexOfFirstSms(ArrayList<Object> smsObjectArrayList, String header) {
        int index = 0;

        for (Object o : smsObjectArrayList) {

            if (o instanceof Sms) {
                if (header.equals(((Sms) o).getTime())) {
                    index = smsObjectArrayList.indexOf(o);
                }
            }
        }

        return index;
    }
}
