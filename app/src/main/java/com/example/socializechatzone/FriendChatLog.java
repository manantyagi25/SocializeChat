package com.example.socializechatzone;

public class FriendChatLog {

    String lastSeenMessageKey;

    public String getLastSeenMessageKey() {
        return lastSeenMessageKey;
    }

    public void setLastSeenMessageKey(String lastSeenMessageKey) {
        this.lastSeenMessageKey = lastSeenMessageKey;
    }

    public FriendChatLog(){

    }

    public FriendChatLog(String lastSeenMessageKey) {
        this.lastSeenMessageKey = lastSeenMessageKey;
    }

    @Override
    public String toString() {
        return "FriendChatLog{" +
                "key='" + lastSeenMessageKey + '\'' +
                '}';
    }
}
