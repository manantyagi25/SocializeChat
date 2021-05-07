package com.example.socializechatzone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.socializechatzone.MainActivity.auth;
import static com.example.socializechatzone.MainActivity.preferences;
import static com.example.socializechatzone.MainActivity.user;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String key = user.getUid();
    private static final int IMAGE_REQUEST_CODE = 1;

    //Declaring views
    Button logOutButton, switchAppButton, darkModeButton;
    CircleImageView userDP;
    TextView userName;

    //Declaring variables
    Context context;
    StorageReference storageReference;
    private Uri imageURL;
    private StorageTask uploadTask;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        userDP = view.findViewById(R.id.userDP);
        userName = view.findViewById(R.id.userName);
        logOutButton = view.findViewById(R.id.logOutButton);
        switchAppButton = view.findViewById(R.id.switchAppButton);
        darkModeButton = view.findViewById(R.id.darkModeButton);

        downloadAndSetDP();
        userName.setText(user.getDisplayName());

        logOutButton.setOnClickListener(logOutListener);
        switchAppButton.setOnClickListener(switchAppListener);
        darkModeButton.setOnClickListener(darkModeSwitchListener);
        userDP.setOnClickListener(imageUploadListener);
    }

    private void downloadAndSetDP(){
        File path = new File(context.getExternalFilesDir(null).toString(), "/profilePics/");
        File pathToProfilePic = new File(path, key);
        Log.i("Path", pathToProfilePic.toString());
        if (pathToProfilePic.exists()) {
            Bitmap image = BitmapFactory.decodeFile(pathToProfilePic.toString());
            //Bitmap resized = Bitmap.createScaledBitmap(image, (int)(image.getWidth()*0.1), (int)(image.getHeight()*0.1), true);
            userDP.setImageBitmap(image);
        }
        else {
            Log.i("File", "not exists");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(key).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //                        profilePic.setImageBitmap(uri.getPath());
                            Picasso.get().load(uri).into(userDP);

                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bMap, Picasso.LoadedFrom from) {
                                    BitmapDrawable drawable = (BitmapDrawable) userDP.getDrawable();
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
                            userDP.setImageResource(R.drawable.socialize);
                            Toast.makeText(context, "Error downloading profile pic!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    View.OnClickListener logOutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(context)
                    .setTitle(getResources().getString(R.string.confirmLogoutTitle))
                    .setMessage(getResources().getString(R.string.confirmLogoutMessage))
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            auth.signOut();
                            Intent intent = new Intent(getContext(), LoginActivity.class);

                            //To make sure going back is not possible
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    };

    View.OnClickListener switchAppListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            if(isAppInstalled(context)){
                Intent intent = context.getPackageManager()
                        .getLaunchIntentForPackage(getResources().getString(R.string.socializeAppPackageName));

                if(intent != null){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(context, getResources().getString(R.string.mainAppNotFound), Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener darkModeSwitchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(preferences.getBoolean("isDark", false)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                preferences.edit().putBoolean("isDark", false).apply();
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                preferences.edit().putBoolean("isDark", true).apply();
            }
            Log.i("isDark", String.valueOf(preferences.getBoolean("isDark", false)));
        }
    };

    View.OnClickListener imageUploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openImage();
        }
    };

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Uploading your shiny new photo...");
        dialog.show();

        if(imageURL != null){
            uploadTask = storageReference.putFile(imageURL);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();

                    return storageReference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                String mUri = downloadUri.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child(getResources().getString(R.string.usersTableName))
                                        .child(user.getUid());

                                HashMap<String, Object> map = new HashMap<>();
                                map.put(getResources().getString(R.string.imageURL), mUri);
                                reference.updateChildren(map);
                            }
                            else {
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }
        else {
            Toast.makeText(context, "No image chosen for upload!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST_CODE && requestCode == RESULT_OK && data != null){
            imageURL = data.getData();

            if(uploadTask != null && uploadTask.isInProgress())
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show();
            else
                uploadImage();
        }
    }

    private boolean isAppInstalled(Context context){
        PackageManager manager = context.getPackageManager();
        try{
            manager.getPackageInfo(getResources().getString(R.string.socializeAppPackageName), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}