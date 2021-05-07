package com.example.socializechatzone;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.socializechatzone.MainActivity.tabLayout;
import static com.example.socializechatzone.MainActivity.toolbar;
import static com.example.socializechatzone.MainActivity.user;

public class ChatsListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FloatingActionButton newChatButton;
    RecyclerView chatsRV;
    ArrayList<String> names, keys, lastMessage;
    ConstraintLayout noMessageHistoryFoundCL;
    ProgressBar progressBar;
    Context context;

    Query messagesHistoryQuery;

    public static FirebaseRecyclerAdapter chatLogAdapter;
    FirebaseRecyclerOptions<FriendChatLog> options;

    private String mParam1;
    private String mParam2;

    public ChatsListFragment() {
    }

    public static ChatsListFragment newInstance(String param1, String param2) {
        ChatsListFragment fragment = new ChatsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatLogAdapter.startListening();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        names = new ArrayList<>();
        keys = new ArrayList<>();
        lastMessage = new ArrayList<>();
        context = getContext();

        messagesHistoryQuery = FirebaseDatabase.getInstance().getReference()
                .child(getResources().getString(R.string.usersTableName))
                .child(user.getUid())
                .child(getResources().getString(R.string.chats));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        newChatButton = view.findViewById(R.id.newChatFAB);
        chatsRV = view.findViewById(R.id.chatsRV);
        noMessageHistoryFoundCL = view.findViewById(R.id.noMessagesFoundCL);
        progressBar = view.findViewById(R.id.chatHistoryPB);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        chatsRV.setLayoutManager(manager);

        setChatsListAdapter();
        newChatButton.setOnClickListener(newChatListener);
    }
    
    private void setChatsListAdapter(){
        options = new FirebaseRecyclerOptions.Builder<FriendChatLog>()
                .setQuery(messagesHistoryQuery, FriendChatLog.class)
                .setLifecycleOwner(this)
                .build();
        
        chatLogAdapter = new FirebaseRecyclerAdapter<FriendChatLog, ChatLogHolder>(options) {

            @NonNull
            @Override
            public ChatLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.chatlist_item_layout_design, parent, false);
                return new ChatLogHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatLogHolder holder, int position, @NonNull FriendChatLog friendChatLog) {
                holder.setKeyAndUID(friendChatLog.getLastSeenMessageKey(), getRef(position).getKey());
            }

            @Override
            public void onDataChanged() {
                if(getItemCount() == 0){
                    noMessageHistoryFoundCL.setVisibility(View.VISIBLE);
                    chatsRV.setVisibility(View.GONE);
                }
                else {
                    noMessageHistoryFoundCL.setVisibility(View.GONE);
                    chatsRV.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        };

        chatsRV.setAdapter(chatLogAdapter);
    }

    View.OnClickListener newChatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tabLayout.getTabAt(1).select();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        chatLogAdapter.stopListening();
    }
}