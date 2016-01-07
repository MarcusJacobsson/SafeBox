package com.marcusjacobsson.vault.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.util.TimeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Marcus Jacobsson on 2015-10-08.
 */
public class SmsArrayAdapter extends ArrayAdapter<Object> {

    private Context ctx;
    private ArrayList<Object> smsList;
    private static final int TYPE_MSG = 0;
    private static final int TYPE_HEADER = 1;

    public SmsArrayAdapter(Context ctx, ArrayList<Object> smsList) {
        super(ctx, R.layout.list_view_sms_item, smsList);
        this.smsList = smsList;
        this.ctx = ctx;
    }

    private static class ViewHolderMsg {
        LinearLayout llMsg;
        TextView tvMsg;
    }

    private static class ViewHolderHeader {
        TextView tvHeader;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return smsList.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Sms) {
            return TYPE_MSG;
        }
        return TYPE_HEADER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_MSG);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        View row = convertView;
        ViewHolderMsg holderMsg = null;
        ViewHolderHeader holderHeader = null;
        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();

        switch (type) {
            case TYPE_MSG:
                if (row == null) {
                    holderMsg = new ViewHolderMsg();
                    row = inflater.inflate(R.layout.list_view_sms_item, parent, false);
                    holderMsg.tvMsg = (TextView) row.findViewById(R.id.tv_lw_sms_msg);
                    holderMsg.llMsg = (LinearLayout) row.findViewById(R.id.ll_lw_sms_msg_layout);
                    row.setTag(holderMsg);
                } else {
                    holderMsg = (ViewHolderMsg) row.getTag();
                }
                break;

            case TYPE_HEADER:
                if(row == null){
                    holderHeader = new ViewHolderHeader();
                    row = inflater.inflate(R.layout.list_view_sms_header, parent, false);
                    holderHeader.tvHeader = (TextView) row.findViewById(R.id.tv_lw_sms_header);
                    row.setTag(holderHeader);
                }else{
                    holderHeader = (ViewHolderHeader) row.getTag();
                }
                break;
        }

        switch (type){
            case TYPE_MSG:
                Sms sms = (Sms)smsList.get(position);
                holderMsg.tvMsg.setText(sms.getMsg());
                if (sms.getFolderName().equals("sent")) {
                    holderMsg.llMsg.setPadding(40, 0, 0, 0);
                    holderMsg.llMsg.setGravity(Gravity.END);
                } else {
                    holderMsg.llMsg.setPadding(0, 0, 40, 0);
                }
                break;

            case TYPE_HEADER:
                String header = (String)smsList.get(position);
                holderHeader.tvHeader.setText(header);
                break;
        }



        return row;
    }

}
