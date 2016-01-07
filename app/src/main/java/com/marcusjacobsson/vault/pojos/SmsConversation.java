package com.marcusjacobsson.vault.pojos;

import java.util.ArrayList;

/**
 * Created by Marcus Jacobsson on 2015-10-10.
 */
public class SmsConversation {

    private String contactName;
    private String lastMsg;
    private String time;
    private String iconPath;
    private ArrayList<Sms> smsArrayList;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public ArrayList<Sms> getSmsArrayList() {
        return smsArrayList;
    }

    public void setSmsArrayList(ArrayList<Sms> smsArrayList) {
        this.smsArrayList = smsArrayList;
    }
}
