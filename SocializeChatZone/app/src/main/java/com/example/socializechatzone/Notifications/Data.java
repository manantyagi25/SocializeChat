package com.example.socializechatzone.Notifications;

public class Data {
    private String user, body, title, sent;
    private int icon;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Data(){

    }

    public Data(String user, String body, String title, String sent, int icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Data{" +
                "user='" + user + '\'' +
                ", body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", sent='" + sent + '\'' +
                ", icon=" + icon +
                '}';
    }
}
