package com.example.socializechatzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.socializechatzone.MainActivity.user;

public class ChatActivity extends AppCompatActivity {

    public static final String UID = "chatUserUID";
    public static final String NAME = "name";
    public static final String LASTSEENMESSAGEKEY = "lastSeenMessageKey";
    public static final int MESSAGE_RECEIVED = 0;
    public static final int MESSAGE_SENT = 1;

    //Declaring views
    ImageButton sendMessageButton;
    TextView friendName, startChatWithTV;
    EditText messageET;
    CircleImageView friendDPIV;
    RecyclerView chatLogRV;
    ConstraintLayout newChatBeginningCL;

    //Declaring variables
    Context context;
    Query chatLogQuery, readUpdate;
    public static FirebaseRecyclerAdapter chatsAdapter;
    FirebaseRecyclerOptions<Message> options;
    LinearLayoutManager manager;
    String key, userName, chatsUID, lastSeenMessageKey, date;
    ArrayList<Message> messages;
    DatabaseReference userMessagesHistoryReference;

    @Override
    protected void onStart() {
        super.onStart();
        chatsAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Getting information from intent
        Intent intent = getIntent();
        key = intent.getStringExtra(UID);
        userName = intent.getStringExtra(NAME);
        lastSeenMessageKey = intent.getStringExtra(LASTSEENMESSAGEKEY);

        getChatsKey();

        userMessagesHistoryReference = FirebaseDatabase.getInstance().getReference()
                .child(getResources().getString(R.string.usersTableName))
                .child(user.getUid())
                .child(getResources().getString(R.string.chats))
                .child(key);

        //Initializing views
        final Toolbar toolbar = findViewById(R.id.toolBar);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        friendName = findViewById(R.id.friendName);
        friendDPIV = findViewById(R.id.friendDPIV);
        messageET = findViewById(R.id.messageET);
        chatLogRV = findViewById(R.id.chatLogRV);
        newChatBeginningCL = findViewById(R.id.newChatBeginningCL);
        startChatWithTV = findViewById(R.id.chatWithTV);

        //Initializing variables
        context = getApplicationContext();
        messages = new ArrayList<>();

        chatLogQuery = FirebaseDatabase.getInstance().getReference()
                .child(getResources().getString(R.string.messagesTableName))
                .child(chatsUID);

        readUpdate = chatLogQuery;

        //Setting up details of chat user
        friendName.setText(userName);
        downloadAndSetDP();

        //Setting up RecyclerView
        chatLogRV.setHasFixedSize(true);
        manager = new LinearLayoutManager(context);
//        manager.setStackFromEnd(true);
        chatLogRV.setLayoutManager(manager);
//        chatLogRV.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readUpdate.removeEventListener(readUpdateListener);
                finish();
            }
        });

        setChatsAdapter();

        if(!lastSeenMessageKey.equals(""))
            seenMessage();

        messageET.addTextChangedListener(watcher);
        sendMessageButton.setOnClickListener(sendMessageListener);

        chatsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
//                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = chatsAdapter.getItemCount();
                    int lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();

                    if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                        chatLogRV.scrollToPosition(positionStart);
                    }
            }
        });
    }

    private void downloadAndSetDP(){
        File path = new File(context.getExternalFilesDir(null).toString(), "/profilePics/");
        File pathToProfilePic = new File(path, key);
        Log.i("Path", pathToProfilePic.toString());
        if (pathToProfilePic.exists()) {
            Bitmap image = BitmapFactory.decodeFile(pathToProfilePic.toString());
            Bitmap resized = Bitmap.createScaledBitmap(image, (int)(image.getWidth()*0.1), (int)(image.getHeight()*0.1), true);
            friendDPIV.setImageBitmap(resized);
        }
        else {
            Log.i("File", "not exists");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(key).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //                        profilePic.setImageBitmap(uri.getPath());
                            Picasso.get().load(uri).into(friendDPIV);

                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bMap, Picasso.LoadedFrom from) {
                                    BitmapDrawable drawable = (BitmapDrawable) friendDPIV.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    try {
                                        File file = new File(context.getExternalFilesDir(null).toString());
                                        File myDir = new File(file, "profilePics");
                                        if (!myDir.exists())
                                            myDir.mkdirs();

                                        myDir = new File(myDir, key);
                                        FileOutputStream outputStream = new FileOutputStream(myDir);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            };

                            Picasso.get().load(uri).into(target);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            friendDPIV.setImageResource(R.drawable.socialize);
                            Toast.makeText(context, "Error downloading profile pic!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setChatsAdapter(){
        options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(chatLogQuery, Message.class)
                .setLifecycleOwner(this)
                .build();

        chatsAdapter = new FirebaseRecyclerAdapter<Message, MessageHolder>(options) {

            @NonNull
            @Override
            public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if( viewType == MESSAGE_RECEIVED) {
                    View view = LayoutInflater.from(context).inflate(R.layout.message_received_design, parent, false);
                    return new MessageHolder(view);
                }
                else{
                    View view = LayoutInflater.from(context).inflate(R.layout.message_sent_design, parent, false);
                    return new MessageHolder(view);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {
                holder.messageTV.setText(model.getMessageText());
                holder.timeTV.setText(getDateAndTime(model.getTimeStamp(), 1));
                if(model.getMessageBy().equals(user.getUid())) {
                    if(position == getItemCount() - 1) {
                        holder.seenTV.setVisibility(View.VISIBLE);
                        if (model.isSeenByReceiver())
                            holder.seenTV.setText(getResources().getString(R.string.seen));
                        else
                            holder.seenTV.setText(getResources().getString(R.string.sent));
                    }
                    else
                        holder.seenTV.setVisibility(View.GONE);
                }

                date = getDateAndTime(model.getTimeStamp(), 2);
                if(position == 0 || !getDateAndTime(getItem(position - 1).getTimeStamp(), 2).equals(date)) {
                    holder.dateTV.setVisibility(View.VISIBLE);
                    holder.dateTV.setText(date);
                }
            }

            @Override
            public int getItemViewType(int position) {
                Message message = getItem(position);
                if(message.getMessageBy().equals(user.getUid()))
                    return MESSAGE_SENT;
                else
                    return MESSAGE_RECEIVED;
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public void onDataChanged() {
                if(getItemCount() == 0){
                    startChatWithTV.setText(context.getResources().getString(R.string.newChatBeginning, userName));
                    newChatBeginningCL.setVisibility(View.VISIBLE);
                }
                else
                    newChatBeginningCL.setVisibility(View.GONE);
            }
        };

        chatLogRV.setAdapter(chatsAdapter);
//        chatLogRV.scrollToPosition(chatsAdapter.getItemCount() - 1);
    }

    private void seenMessage(){
        if(lastSeenMessageKey.equals("new"))
            readUpdate.addValueEventListener(readUpdateListener);
        else
            readUpdate.orderByKey().startAt(lastSeenMessageKey).addValueEventListener(readUpdateListener);
    }

    private void getChatsKey(){
        if(key.compareTo(user.getUid()) > 0)
            chatsUID = user.getUid() + key;
        else
            chatsUID = key + user.getUid();
    }

    private String getDateAndTime(Object time, int type){
        Date date = new Date((Long)time);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        if(type == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }

    View.OnClickListener sendMessageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = messageET.getText().toString();

            if(messageText.length() > 0){

                DatabaseReference senderMessageReference = FirebaseDatabase.getInstance().getReference()
                        .child(getResources().getString(R.string.messagesTableName))
                        .child(chatsUID)
                        .push();

                Message message = new Message(user.getUid(), key, messageText, false);

                //                Message message = new Message(user.getUid(), key, messageText, false);
                senderMessageReference.setValue(message);

                Log.i("Message ref", senderMessageReference.getRef().getKey());
                FriendChatLog log = new FriendChatLog(senderMessageReference.getRef().getKey());
                userMessagesHistoryReference.setValue(log);

                if(lastSeenMessageKey.equals("")){
                    DatabaseReference friendMessagesHistoryReference = FirebaseDatabase.getInstance().getReference()
                            .child(getResources().getString(R.string.usersTableName))
                            .child(key)
                            .child(getResources().getString(R.string.chats))
                            .child(user.getUid());

                    log = new FriendChatLog("new");
                    friendMessagesHistoryReference.setValue(log);
                }

                messageET.getText().clear();
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(messageET.getWindowToken(), 0);
            }
        }
    };

    ValueEventListener readUpdateListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                Message message = dataSnapshot.getValue(Message.class);
                if(message.getMessageBy().equals(key))
                {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("seenByReceiver", true);
                    dataSnapshot.getRef().updateChildren(map);
                    lastSeenMessageKey = dataSnapshot.getKey();
                }
            }
            FriendChatLog log = new FriendChatLog(lastSeenMessageKey);
            userMessagesHistoryReference.setValue(log);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(messageET.getText().length() == 0){
//                sendMessageButton.setEnabled(false);
                sendMessageButton.setColorFilter(context.getResources().getColor(R.color.lightRed,getTheme()));
            }
            else
                sendMessageButton.setColorFilter(context.getResources().getColor(R.color.sendMessageButton, getTheme()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        chatsAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        readUpdate.removeEventListener(readUpdateListener);
        finish();
    }

    @Override
    protected void onDestroy() {
        readUpdate.removeEventListener(readUpdateListener);
        super.onDestroy();
    }
}