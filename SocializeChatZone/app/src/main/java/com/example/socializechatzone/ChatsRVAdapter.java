package com.example.socializechatzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsRVAdapter extends RecyclerView.Adapter<ChatHolder> {

    private Context mContext;
    ArrayList<String> names, keys, lastMessage;

    public ChatsRVAdapter(Context mContext, ArrayList<String> names, ArrayList<String> keys, ArrayList<String> lastMessage) {
        this.mContext = mContext;
        this.names = names;
        this.keys = keys;
        this.lastMessage = lastMessage;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chatlist_item_layout_design, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

//        holder.dp.setImageResource(R.drawable.fb);
        holder.nameTV.setText(names.get(position));
        holder.lastMessageTV.setText(lastMessage.get(position));
        holder.setKey(keys.get(position), names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


}
