package com.example.socializechatzone;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Message {

    String messageBy;
    String messageTo;
    String messageText;
    boolean seenByReceiver;
    Object timeStamp = ServerValue.TIMESTAMP;

    public String getMessageBy() {
        return messageBy;
    }

    public void setMessageBy(String messageBy) {
        this.messageBy = messageBy;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isSeenByReceiver() {
        return seenByReceiver;
    }

    public void setSeenByReceiver(boolean seenByReceiver) {
        this.seenByReceiver = seenByReceiver;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    @Exclude
    public Long timeStamp(){
        if(timeStamp instanceof Long)
            return (Long) timeStamp;
        else
            return null;
    }

    public Message(){

    }

    public Message(String messageBy, String messageTo, String messageText, boolean seenByReceiver) {
        this.messageBy = messageBy;
        this.messageTo = messageTo;
        this.messageText = messageText;
        this.seenByReceiver = seenByReceiver;
    }

    /*@Override
    public String toString() {
        return "Message{" +
                "messageBy='" + messageBy + '\'' +
                ", messageTo='" + messageTo + '\'' +
                ", messageText='" + messageText + '\'' +
                ", seenByReceiver=" + seenByReceiver +
                '}';
    }*/

    @Override
    public String toString() {
        return "Message{" +
                "messageBy='" + messageBy + '\'' +
                ", messageTo='" + messageTo + '\'' +
                ", messageText='" + messageText + '\'' +
                ", seenByReceiver=" + seenByReceiver +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
