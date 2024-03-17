package ca.dmi.uqtr.applicationchat.bdlocal.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

import ca.dmi.uqtr.applicationchat.BR;


@Entity(tableName = "messages")
public class Message extends BaseObservable {
    @PrimaryKey
    @NonNull
    private String messageId;

    @ColumnInfo(name = "sender_id")
    private String senderId;

    private String senderName;
    @ColumnInfo(name = "destinater")
    private String destinater;


    private int type;
    private Date time;

    private boolean send = true;

    private String test;

    private String imageUrl;
    private  String audioUrl;
    public Message() {
    }
    @Ignore
    public Message(String  test,Date time,String destinater , String senderId) {
        this.destinater=destinater;
        this.senderId=senderId;
        this.messageId= UUID.randomUUID().toString();
        this.time = time;
        this.test=test;
    }

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    @Bindable
    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
        notifyPropertyChanged(BR.audioUrl);
    }

    @Bindable
    public String getDestinater() {
        return destinater;
    }

    public void setDestinater(String destinater) {
        this.destinater = destinater;
        notifyPropertyChanged(BR.destinater);
    }

    @Bindable
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
    }

    @Bindable
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }



    @Bindable
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        notifyPropertyChanged(BR.senderId);
    }


    @Bindable
    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
        notifyPropertyChanged(BR.send);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
    @Bindable
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
        notifyPropertyChanged(BR.senderName);
    }
}