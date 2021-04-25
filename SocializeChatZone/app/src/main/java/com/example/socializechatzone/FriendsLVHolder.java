package com.example.socializechatzone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
/*import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;*/

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.socializechatzone.MainActivity.tabLayout;
import static com.example.socializechatzone.MainActivity.toolbar;
import static com.example.socializechatzone.MainActivity.user;
import static com.example.socializechatzone.MainActivity.viewPager;

public class FriendsLVHolder extends RecyclerView.ViewHolder {

    CircleImageView dp;
    TextView nameTV, onlineViaTV;
    private String key, name;
    private Context context;


    public FriendsLVHolder(@NonNull View itemView) {
        super(itemView);

        this.dp = itemView.findViewById(R.id.userDP);
        this.nameTV = itemView.findViewById(R.id.friendName);
        this.context = itemView.getContext();
        this.onlineViaTV = itemView.findViewById(R.id.onlineViaTV);

        itemView.setOnClickListener(listener);
    }

    public void setKey(String uid, String name, Long via){
        this.key = uid;
        this.name = name;

        downloadAndSetDP();
        if(via == 1){
            onlineViaTV.setText(context.getResources().getString(R.string.onlineViaSocialize));
        }
        else if(via == 2){
            onlineViaTV.setText(context.getResources().getString(R.string.onlineViaChatZone));
        }

    }

    private void downloadAndSetDP(){
        File path = new File(context.getExternalFilesDir(null).toString(), "/profilePics/");
        File pathToProfilePic = new File(path, key);
        Log.i("Path", pathToProfilePic.toString());
        if (pathToProfilePic.exists()) {
            Bitmap image = BitmapFactory.decodeFile(pathToProfilePic.toString());
            Bitmap resized = Bitmap.createScaledBitmap(image, (int)(image.getWidth()*0.1), (int)(image.getHeight()*0.1), true);
            dp.setImageBitmap(resized);
        }
        else {
            Log.i("File", "not exists");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(key).getDownloadUrl()
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
                            dp.setImageResource(R.drawable.socialize);
                            Toast.makeText(context, "Error downloading profile pic!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { //Start chat
            /*tabLayout.getTabAt(0).select();
            ChatFragment chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString(ChatFragment.UID, key);
            args.putString(ChatFragment.NAME, name);
            chatFragment.setArguments(args);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).addToBackStack(null).commit();*/
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(ChatActivity.UID, key);
            intent.putExtra(ChatActivity.NAME, name);
            intent.putExtra(ChatActivity.LASTSEENMESSAGEKEY, "");
            context.startActivity(intent);
        }
    };

}
