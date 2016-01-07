package com.marcusjacobsson.vault.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marcus Jacobsson on 2015-10-08.
 */
public class Sms implements Parcelable{

    private String id;
    private String thread_id;
    private String address;
    private String person;
    private String time; //date
    private String protocol;
    private String read;
    private String status;
    private String type;
    private String replyPathPresent;
    private String subject;
    private String msg; //body
    private String serviceCenter;
    private String locked;
    private String errorCode;
    private String seen;
    private String folderName;
    private String contactName;

    public Sms(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplyPathPresent() {
        return replyPathPresent;
    }

    public void setReplyPathPresent(String replyPathPresent) {
        this.replyPathPresent = replyPathPresent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Sms(Parcel in){
        this.id = in.readString();
        this.thread_id = in.readString();
        this.address = in.readString();
        this.person = in.readString();
        this.time = in.readString();
        this.protocol = in.readString();
        this.read = in.readString();
        this.status = in.readString();
        this.type = in.readString();
        this.replyPathPresent = in.readString();
        this.subject = in.readString();
        this.msg = in.readString();
        this.serviceCenter = in.readString();
        this.locked = in.readString();
        this.errorCode = in.readString();
        this.seen = in.readString();
        this.folderName = in.readString();
        this.contactName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(thread_id);
        dest.writeString(address);
        dest.writeString(person);
        dest.writeString(time);
        dest.writeString(protocol);
        dest.writeString(read);
        dest.writeString(status);
        dest.writeString(type);
        dest.writeString(replyPathPresent);
        dest.writeString(subject);
        dest.writeString(msg);
        dest.writeString(serviceCenter);
        dest.writeString(locked);
        dest.writeString(errorCode);
        dest.writeString(seen);
        dest.writeString(folderName);
        dest.writeString(contactName);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };

}
