package com.example.chatapp;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Users;
import com.example.chatapp.Fragments.ProfileFragments;
import com.example.chatapp.Fragments.UserFragments;
import com.example.chatapp.Fragments.chatsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView username, profilename;
    DatabaseReference databaseReference;
    FirebaseUser firebaseuser;
    String is_blocked = null;
    ImageView notification;
    public static Integer count = 0;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //profilename = findViewById(R.id.username_profile_frag);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewpager = findViewById(R.id.viewPager);

        ViewpageAdapter viewpageAdapter = new ViewpageAdapter(getSupportFragmentManager());
        viewpageAdapter.addFragements(new UserFragments(), "Explore");
        viewpageAdapter.addFragements(new chatsFragment(), "Chat");
        viewpageAdapter.addFragements(new ProfileFragments(), "More");
        Intent i = getIntent();
        is_blocked = i.getStringExtra("bl");
        int defaultValue = 0;
        int page = getIntent().getIntExtra("One", defaultValue);
        viewpager.setCurrentItem(page);


        viewpager.setAdapter(viewpageAdapter);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_flight_takeoff_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_chat_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_more);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        // String tab1= String.valueOf(tabLayout.getTabAt(0).getId());
        viewpager.setCurrentItem(page);
        tabLayout.setScrollPosition(page, 0f, true);
        if (page != 0) {
            tabLayout.getTabAt(page).getIcon().setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
                if (tab.getPosition() == 0) {
                    Intent i = new Intent(getApplicationContext(), com.example.chatapp.MainActivity.class);
                    startActivity(i);
                    finish();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
                if (tab.getPosition() == 0) {

                    Intent i = new Intent(getApplicationContext(), com.example.chatapp.MainActivity.class);
                    startActivity(i);
                    finish();
                }
//
            }
        });


        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                //  username.setText(users.getUsername());

                //  Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, com.example.chatapp.Login.Intro.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

//               Toast.makeText(MainActivity.this,"Press again to exit"+count++,Toast.LENGTH_SHORT).show();
//
//                         startActivity(getIntent());
//
//        if(is_blocked.equals("yes_blocked")) {
//            startActivity(getIntent());
//            is_blocked = null;
//        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()) {

        }.postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
        // super.onBackPressed();

    }

}
