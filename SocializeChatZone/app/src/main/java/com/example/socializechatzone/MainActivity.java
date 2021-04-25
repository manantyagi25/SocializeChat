package com.example.socializechatzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.internal.$Gson$Preconditions;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth auth;
    public static FirebaseUser user;
    public static Toolbar toolbar;
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    FirstTabFragment firstTabFragment;
    private OnlineFriendsFragment onlineFriendsFragment;
    private SettingsFragment settingsFragment;
    public static SharedPreferences preferences;

    int state = 1;
    ImageButton imageButton;
//    Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Firebase variables
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        preferences = getSharedPreferences("com.example.socializechatzone", MODE_PRIVATE);

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else {

            setMode();

            Log.i("isDark", String.valueOf(preferences.getBoolean("isDark", false)));
            //Initializing Views
            toolbar = findViewById(R.id.toolBar);
            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);

            //Initializing Fragments

            firstTabFragment = new FirstTabFragment();
            onlineFriendsFragment = new OnlineFriendsFragment();
            settingsFragment = new SettingsFragment();

            setSupportActionBar(toolbar);
            tabLayout.setupWithViewPager(viewPager);

            //Setting up tabs
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
            viewPagerAdapter.addFragment(firstTabFragment, "Chats");
            viewPagerAdapter.addFragment(onlineFriendsFragment, "Online");
            viewPagerAdapter.addFragment(settingsFragment, "Settings");
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setOffscreenPageLimit(4);

            DatabaseReference onlineStatusReference = FirebaseDatabase.getInstance().getReference()
                    .child(getResources().getString(R.string.usersTableName))
                    .child(user.getUid())
                    .child(getResources().getString(R.string.onlineStatus));
            onlineStatusReference.setValue(2);

            //startService(new Intent(this, fetchMessagesService.class));
        }
    }

    @Override
    public void onBackPressed() {
        if(tabLayout.getTabAt(0).isSelected())
            super.onBackPressed();
        else
            tabLayout.getTabAt(0).select();
    }

    public void setMode(){
        boolean isDark = preferences.getBoolean("isDark", false);

        if(isDark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}