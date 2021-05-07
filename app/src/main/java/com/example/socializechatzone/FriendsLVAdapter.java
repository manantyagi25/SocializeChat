package com.example.socializechatzone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsLVAdapter extends RecyclerView.Adapter<FriendsLVHolder> {

    private Context mContext;
    ArrayList<String> names;
    ArrayList<String> keys;
    ArrayList<Long> onlineVia;

    public FriendsLVAdapter(Context mContext, ArrayList<String> names, ArrayList<String> keys, ArrayList<Long> onlineVia) {
        this.mContext = mContext;
        this.names = names;
        this.keys = keys;
        this.onlineVia = onlineVia;
    }

    @NonNull
    @Override
    public FriendsLVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friends_list_item_layout_2, parent, false);
        return new FriendsLVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsLVHolder holder, int position) {

//        holder.dp.setImageResource(R.drawable.fb);
        holder.nameTV.setText(names.get(position));
        holder.setKey(keys.get(position), names.get(position), onlineVia.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


}
