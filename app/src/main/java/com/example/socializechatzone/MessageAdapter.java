package com.example.socializechatzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.socializechatzone.MainActivity.user;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    public static final int MESSAGE_RECEIVED = 0;
    public static final int MESSAGE_SENT = 1;

    private Context mContext;
    ArrayList<Message> messages;

    public MessageAdapter(Context mContext, ArrayList<Message> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if( viewType == MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_received_design, parent, false);
            return new MessageHolder(view);
        }
        else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_sent_design, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        holder.messageTV.setText(messages.get(position).getMessageText());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getMessageBy().equals(user.getUid()))
            return MESSAGE_SENT;
        else
            return MESSAGE_RECEIVED;
    }
}
