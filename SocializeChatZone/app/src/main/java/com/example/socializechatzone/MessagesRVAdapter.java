package com.example.socializechatzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesRVAdapter extends RecyclerView.Adapter<MessageHolder> {

    private Context mContext;
    ArrayList<String> names;
    ArrayList<String> keys;

    public MessagesRVAdapter(Context mContext, ArrayList<String> names, ArrayList<String> keys) {
        this.mContext = mContext;
        this.names = names;
        this.keys = keys;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_layout, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

//        holder.dp.setImageResource(R.drawable.fb);
        holder.messageTV.setText(names.get(position));
        //holder.setKey(keys.get(position), names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


}
