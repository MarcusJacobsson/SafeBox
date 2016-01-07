package com.marcusjacobsson.vault.activities.mainmenu.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.adapters.SmsConvArrayAdapter;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.pojos.SmsConversation;
import com.marcusjacobsson.vault.util.VaultContentProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Marcus Jacobsson on 2015-10-10.
 */
public class ShowSmsConvFragment extends Fragment implements AdapterView.OnItemClickListener, VaultContentProvider.OnSmsQueryResultListener {

    private ListView listView;
    private ArrayAdapter<SmsConversation> adapter;
    private ArrayList<SmsConversation> allSmsConversations;
    private VaultContentProvider provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sms, container, false);
        getActivity().setTitle(getActivity().getString(R.string.sms_conversations));

        allSmsConversations = new ArrayList<>();
        listView = (ListView) v.findViewById(R.id.lw_sms);
        adapter = new SmsConvArrayAdapter(getActivity(), allSmsConversations);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);

        provider = new VaultContentProvider(getActivity());
        provider.setOnSmsQueryResultListener(this);
        provider.getPublicSms(getActivity());

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<Sms> sms = allSmsConversations.get(position).getSmsArrayList();

        ChooseSmsFragment fragment = new ChooseSmsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("sms", sms);
        fragment.setArguments(args);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onSmsQueryResult(ArrayList<Sms> sms) {
        adapter.clear();
        adapter.addAll(buildConversationList(sms));
    }

    private ArrayList<SmsConversation> buildConversationList(ArrayList<Sms> smsArrayList) {

        ArrayList<SmsConversation> smsConversations = new ArrayList<>();
        ArrayList<String> uniqueNameList = new ArrayList<>();

        //Find out the unique names
        for (Sms s : smsArrayList) {
            String name = s.getContactName();

            //Name could be unknown if it's not a contact. If so, use the phone number as contact name
            if(name == null || name.equals("")){
                name = s.getAddress();
            }

            if (!uniqueNameList.contains(name)) {
                uniqueNameList.add(name);
            }
        }

        //Create a conversation for each unique name
        for (String name : uniqueNameList) {
            ArrayList<Sms> conversationSms = new ArrayList<>();
            SmsConversation smsConversation = new SmsConversation();
            smsConversation.setContactName(name);
            smsConversation.setSmsArrayList(conversationSms);
            smsConversations.add(smsConversation);
        }

        //Map each sms to it's conversation
        for (Sms s : smsArrayList) {

            for (SmsConversation sc : smsConversations) {
                if (s.getContactName() != null && s.getContactName().equals(sc.getContactName())) {
                    sc.getSmsArrayList().add(s);
                }
                //If contact name is null, the phone number is not added as a contact. Compare with address (phone number) instead.
                else if(s.getContactName() == null){
                    if(s.getAddress().equals(sc.getContactName())){
                        sc.getSmsArrayList().add(s);
                    }
                }
            }
        }

        //Sort the sms by time
        for (SmsConversation sc : smsConversations) {
            ArrayList<Sms> smses = sc.getSmsArrayList();

            Collections.sort(smses, new Comparator<Sms>() {
                @Override
                public int compare(Sms lhs, Sms rhs) {

                    return rhs.getTime().compareTo(lhs.getTime());

                }
            });

            Sms latest = smses.get(0);
            sc.setTime(latest.getTime());
            sc.setLastMsg(latest.getMsg());
        }

        return smsConversations;
    }
}
