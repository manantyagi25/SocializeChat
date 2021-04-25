package com.example.socializechatzone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.socializechatzone.MainActivity.auth;
import static com.example.socializechatzone.MainActivity.toolbar;
import static com.example.socializechatzone.MainActivity.user;
import static com.example.socializechatzone.MainActivity.viewPager;

public class MessageHolder extends RecyclerView.ViewHolder {

    TextView messageTV, seenTV, timeTV, dateTV;
    private Context context;


    public MessageHolder(@NonNull View itemView) {
        super(itemView);

        this.messageTV = itemView.findViewById(R.id.messageText);
        this.seenTV = itemView.findViewById(R.id.seenTV);
        this.timeTV = itemView.findViewById(R.id.timeTV);
        this.dateTV = itemView.findViewById(R.id.dateTV);
        this.context = itemView.getContext();
    }

    /*public void setKey(String key){
        this.key = key;

    }*/


}
