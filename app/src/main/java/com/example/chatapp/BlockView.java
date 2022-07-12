package com.example.chatapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.Adapters.BlockedAdapter;
import com.example.chatapp.Adapters.connectionAdapter;

import com.example.chatapp.Adapters.NotificationtabAdapter;
import com.google.android.material.tabs.TabLayout;

public class BlockView extends AppCompatActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionview);

        // TabLayout tb = (TabLayout) findViewById(R.id.tabLayouts);
        viewPager = (ViewPager) findViewById(R.id.connectionViewpager);
        final BlockedAdapter adapter = new BlockedAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


    }
}