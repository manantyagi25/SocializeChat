package com.example.socializechatzone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.socializechatzone.MainActivity.user;

public class OnlineFriendsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Query friendsQuery;
    ArrayList<String> friendsUID, friendsKey, userFriends;
    ArrayList<Long> onlineVia;
    TextView noFriendsFoundTV, noFriendsOnlineTV, noSearchResultsFoundTV;
    FriendsLVAdapter friendsLVAdapter;
    RecyclerView friendsRV;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText searchNameET;
    ImageButton cancelSearchButton;

    public OnlineFriendsFragment() {
        // Required empty public constructor
    }

    public static OnlineFriendsFragment newInstance(String param1, String param2) {
        OnlineFriendsFragment fragment = new OnlineFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        friendsQuery = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.usersTableName))
                .child(user.getUid()).child(getResources().getString(R.string.friends));

        friendsUID = new ArrayList<>();
        friendsKey = new ArrayList<>();
        userFriends = new ArrayList<>();
        onlineVia = new ArrayList<>();

        friendsLVAdapter = new FriendsLVAdapter(getContext(), userFriends, friendsKey, onlineVia);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_online_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noFriendsFoundTV = view.findViewById(R.id.noFriendsFoundTV);
        noFriendsOnlineTV = view.findViewById(R.id.noFriendsOnlineTV);
        noSearchResultsFoundTV = view.findViewById(R.id.noSearchResultsFoundTV);
        friendsRV = view.findViewById(R.id.friendsListRV);
        swipeRefreshLayout = view.findViewById(R.id.friendsListSRL);
        searchNameET = view.findViewById(R.id.searchNameET);
        cancelSearchButton = view.findViewById(R.id.cancelSearchButton);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        friendsRV.setLayoutManager(manager);
        friendsRV.setAdapter(friendsLVAdapter);

        fetchOnlineFriends();

        searchNameET.addTextChangedListener(watcher);

        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        cancelSearchButton.setOnClickListener(cancelSearchListener);
    }

    View.OnClickListener cancelSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancelSearch();
            searchNameET.setText(null);
            noSearchResultsFoundTV.setVisibility(View.GONE);
        }
    };

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            friendsUID.clear();
            friendsKey.clear();
            userFriends.clear();
            onlineVia.clear();
            friendsLVAdapter.notifyDataSetChanged();
            fetchOnlineFriends();
        }
    };

    private void fetchOnlineFriends(){
        friendsKey.clear();
        userFriends.clear();
        onlineVia.clear();
        friendsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Friends friends = snapshot.getValue(Friends.class);
                    friendsUID = friends.getFriendIDs();

                    for (final String uid : friendsUID){
                        final DatabaseReference onlineStatus = FirebaseDatabase.getInstance().getReference()
                                .child(getResources().getString(R.string.usersTableName))
                                .child(uid).child(getResources().getString(R.string.onlineStatus));
                        onlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Long isOnline = (Long) snapshot.getValue();
                                    noFriendsOnlineTV.setVisibility(View.GONE);
                                    if(isOnline == 1 || isOnline == 2){
                                        friendsKey.add(0, uid);
                                        onlineVia.add(0, isOnline);
                                        DatabaseReference friendData = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                        friendData.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User user = snapshot.getValue(User.class);
                                                String name = user.getFullName();
                                                userFriends.add(0, name);
                                                friendsLVAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else {
                                        friendsKey.add(uid);
                                        onlineVia.add(isOnline);
                                        DatabaseReference friendData = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                        friendData.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User user = snapshot.getValue(User.class);
                                                String name = user.getFullName();
                                                userFriends.add(name);
                                                friendsLVAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                else {
                    noFriendsFoundTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(friendsKey.size() == 0){
            noFriendsFoundTV.setVisibility(View.GONE);
            noFriendsOnlineTV.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void searchFriends(String entered){
        ArrayList<String> friendKeys, foundFriends;
        ArrayList<Long> onlineViaApp;
        boolean anyFound = false;

        friendKeys = new ArrayList<>();
        foundFriends = new ArrayList<>();
        onlineViaApp = new ArrayList<>();
        FriendsLVAdapter adapter = new FriendsLVAdapter(getContext(), foundFriends, friendKeys, onlineViaApp);

        Pattern pattern = Pattern.compile(entered, Pattern.CASE_INSENSITIVE);
        for(int i = 0; i<userFriends.size() ; ++i){
            Matcher matcher = pattern.matcher(userFriends.get(i));
            boolean isFound = matcher.find();
            if (isFound) {
                anyFound = true;
                foundFriends.add(userFriends.get(i));
                friendKeys.add(friendsKey.get(i));
                onlineViaApp.add(onlineVia.get(i));
                adapter.notifyDataSetChanged();
            }
        }

        if(anyFound) {
            friendsRV.setVisibility(View.VISIBLE);
            friendsRV.setAdapter(adapter);
            noSearchResultsFoundTV.setVisibility(View.GONE);
        }
        else {
            friendsRV.setVisibility(View.GONE);
            noSearchResultsFoundTV.setText(getResources().getString(R.string.noResultsFound, entered));
            noSearchResultsFoundTV.setVisibility(View.VISIBLE);
        }
    }

    private void cancelSearch(){
        friendsRV.setVisibility(View.VISIBLE);
        friendsRV.setAdapter(friendsLVAdapter);
        cancelSearchButton.setVisibility(View.GONE);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (searchNameET.getText().length() == 0){
                cancelSearchButton.setVisibility(View.GONE);
                cancelSearch();
            }
            else {
                cancelSearchButton.setVisibility(View.VISIBLE);
                searchFriends(searchNameET.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}