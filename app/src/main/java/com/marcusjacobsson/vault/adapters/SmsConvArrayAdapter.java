package com.marcusjacobsson.vault.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcusjacobsson.vault.R;
import com.marcusjacobsson.vault.pojos.Sms;
import com.marcusjacobsson.vault.pojos.SmsConversation;
import com.marcusjacobsson.vault.util.IconHelper;
import com.marcusjacobsson.vault.util.PicassoHelper;
import com.marcusjacobsson.vault.util.TimeHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcus Jacobsson on 2015-10-10.
 */
public class SmsConvArrayAdapter extends ArrayAdapter<SmsConversation> {

    private Context ctx;
    private Picasso picasso;
    private ArrayList<SmsConversation> smsConversations;

    public SmsConvArrayAdapter(Context ctx, ArrayList<SmsConversation> smsConversations) {
        super(ctx, R.layout.list_view_sms_conv_item, smsConversations);
        this.ctx = ctx;
        this.smsConversations = smsConversations;
        this.picasso = PicassoHelper.getPicasso();

    }

    static private class ViewHolder {
        ImageView ivIcon;
        TextView tvContactName, tvLastMsg, tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            row = inflater.inflate(R.layout.list_view_sms_conv_item, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) row.findViewById(R.id.iv_lw_sms_conv_icon);
            holder.tvContactName = (TextView) row.findViewById(R.id.tv_lw_sms_conv_contact_name);
            holder.tvLastMsg = (TextView) row.findViewById(R.id.tv_lw_sms_conv_latest_msg);
            holder.tvTime = (TextView) row.findViewById(R.id.tv_lw_sms_conv_time);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        SmsConversation sc = smsConversations.get(position);

        holder.tvContactName.setText(sc.getContactName());
        holder.tvLastMsg.setText(sc.getLastMsg());
        holder.tvTime.setText(TimeHelper.makeTimeString(Long.valueOf(sc.getTime())));

        picasso.load(IconHelper.getIconResourceId(sc.getContactName()))
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(holder.ivIcon);

        return row;
    }
}
