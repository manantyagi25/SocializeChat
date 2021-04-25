package com.example.socializechatzone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.socializechatzone.MainActivity.user;

/*import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;*/

public class ChatLogHolder extends RecyclerView.ViewHolder {

    //Declaring views
    CircleImageView dp;
    TextView nameTV, lastMessageTV, unreadCounter;
    ConstraintLayout unreadMessageCounterCL;
    String friendUID, name, lastSeenMessageKey, chatsUID, firstName;

    //Declaring variables
    private Context context;
    Query chatLogQuery;
    DatabaseReference nameReference;


    public ChatLogHolder(@NonNull View itemView) {
        super(itemView);

        this.dp = itemView.findViewById(R.id.userDP);
        this.nameTV = itemView.findViewById(R.id.chatUserName);
        this.lastMessageTV = itemView.findViewById(R.id.messageTV);
        this.unreadCounter = itemView.findViewById(R.id.unreadCounter);
        this.unreadMessageCounterCL = itemView.findViewById(R.id.unreadMessagesCounterCL);
        this.context = itemView.getContext();

        itemView.setOnClickListener(listener);
    }

    private void downloadAndSetDP(){
        File path = new File(context.getExternalFilesDir(null).toString(), "/profilePics/");
        File pathToProfilePic = new File(path, friendUID);
        Log.i("Path", pathToProfilePic.toString());
        if (pathToProfilePic.exists()) {
            Bitmap image = BitmapFactory.decodeFile(pathToProfilePic.toString());
            Bitmap resized = Bitmap.createScaledBitmap(image, (int)(image.getWidth()*0.1), (int)(image.getHeight()*0.1), true);
            dp.setImageBitmap(resized);
        }
        else {
            Log.i("File", "not exists");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(friendUID).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //                        profilePic.setImageBitmap(uri.getPath());
                            Picasso.get().load(uri).into(dp);

                            /*com.squareup.picasso.Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap1, Picasso.LoadedFrom from) {

                                    BitmapDrawable drawable = (BitmapDrawable) dp.getDrawable();
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
                            };*/

                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bMap, Picasso.LoadedFrom from) {
                                    BitmapDrawable drawable = (BitmapDrawable) dp.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    try {
                                        File file = new File(context.getExternalFilesDir(null).toString());
                                        File myDir = new File(file, "profilePics");
                                        if (!myDir.exists())
                                            myDir.mkdirs();

                                        myDir = new File(myDir, friendUID);
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
                            dp.setImageResource(R.drawable.socialize);
                            Toast.makeText(context, "Error downloading profile pic!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void setKeyAndUID(String lastSeenMessageKey, String friendUID){
        this.lastSeenMessageKey = lastSeenMessageKey;
        this.friendUID = friendUID;

        getChatsKey();                                                                              //Getting key to fetch user-friend message history

        nameReference = FirebaseDatabase.getInstance().getReference()
                .child(context.getResources().getString(R.string.usersTableName))
                .child(friendUID)
                .child(context.getResources().getString(R.string.fullName));                       //Reading friend's name
        nameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue(String.class);
                firstName = name.substring(0, name.indexOf(" "));
                nameTV.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        downloadAndSetDP();

        chatLogQuery = FirebaseDatabase.getInstance().getReference()
                .child(context.getResources().getString(R.string.messagesTableName))
                .child(chatsUID);

        chatLogQuery.orderByKey().limitToLast(1).addValueEventListener(getLastMessageListener);     // Reading last message
        chatLogQuery.orderByKey().startAt(lastSeenMessageKey).addValueEventListener(unreadCountListener);        //Getting number of unread messages
    }

    private void getChatsKey(){
        if(friendUID.compareTo(user.getUid()) > 0)
            chatsUID = user.getUid() + friendUID;
        else
            chatsUID = friendUID + user.getUid();
    }

    ValueEventListener getLastMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                Message message = dataSnapshot.getValue(Message.class);
                if(message.getMessageBy().equals(user.getUid()))
                    lastMessageTV.setText(context.getResources().getString(R.string.messageByUser, message.getMessageText()));
                else
                    lastMessageTV.setText(context.getResources().getString(R.string.messageByFriend, firstName, message.getMessageText()));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener unreadCountListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int count = 0;
            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                Message message = dataSnapshot.getValue(Message.class);
                if(message.getMessageBy().equals(friendUID) && !message.isSeenByReceiver())
                    ++count;
            }

            if(count == 0){
                unreadMessageCounterCL.setVisibility(View.GONE);
            }
            else {
                unreadMessageCounterCL.setVisibility(View.VISIBLE);
                unreadCounter.setText(String.valueOf(count));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { //Start chat
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(ChatActivity.UID, friendUID);
            intent.putExtra(ChatActivity.NAME, name);
            intent.putExtra(ChatActivity.LASTSEENMESSAGEKEY, lastSeenMessageKey);
            context.startActivity(intent);
        }
    };
}
