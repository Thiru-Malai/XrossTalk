package com.example.chatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.Adapters.NotificationtabAdapter;

import com.example.chatapp.Model.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class notificationActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageView connectionnotify, chatnotifys;
    TextView chatcnt, connectcnt;
    ImageView prev;
    int chatcounts = 0, connectcounts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TabLayout tb = (TabLayout) findViewById(R.id.tabLayout);
        connectionnotify = findViewById(R.id.requestnotify);
        chatnotifys = findViewById(R.id.chatnotify);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        chatcnt = (TextView) findViewById(R.id.chatcount);
        prev = findViewById(R.id.btnPrev);
        connectcnt = (TextView) findViewById(R.id.requestcount);
        final NotificationtabAdapter adapter = new NotificationtabAdapter(notificationActivity.this, getSupportFragmentManager(), tb.getTabCount());
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb));

        tb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference referencecht = FirebaseDatabase.getInstance().getReference("ChatRequests").child(firebaseUser.getUid());

        referencecht.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshots : dataSnapshot.getChildren()) {
                    Users users = snapshots.getValue(Users.class);
                    if (firebaseUser.getUid() != null && users.getId() != null)
                        chatcounts++;
                }
                chatcnt.setText(Integer.toString(chatcounts));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference referenceconnect = FirebaseDatabase.getInstance().getReference("Requests").child(firebaseUser.getUid());

        referenceconnect.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshots : dataSnapshot.getChildren()) {
                    Users users = snapshots.getValue(Users.class);
                    if (firebaseUser.getUid() != null && users.getId() != null)
                        connectcounts++;
                }
                connectcnt.setText(Integer.toString(connectcounts));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Query query1 = FirebaseDatabase.getInstance().getReference("ChatRequests").child(firebaseUser.getUid());

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatnotifys.setVisibility(View.VISIBLE);
                    chatcnt.setVisibility(View.VISIBLE);


                } else {
                    chatnotifys.setVisibility(View.GONE);
                    chatcnt.setVisibility(View.GONE);
                    //imagenotify.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2 = FirebaseDatabase.getInstance().getReference("Requests").child(firebaseUser.getUid());

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    connectionnotify.setVisibility(View.VISIBLE);
                    connectcnt.setVisibility(View.VISIBLE);
                } else {
                    connectionnotify.setVisibility(View.GONE);
                    connectcnt.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (chatcounts == 0) {
            chatcnt.setVisibility(View.GONE);
            chatnotifys.setVisibility(View.GONE);
        }
        if (connectcounts == 0) {
            connectcnt.setVisibility(View.GONE);
            connectionnotify.setVisibility(View.GONE);
        }
    }
}