package com.example.chatapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.Adapters.connectionAdapter;

import com.example.chatapp.Adapters.NotificationtabAdapter;
import com.google.android.material.tabs.TabLayout;

public class ConnectionView extends AppCompatActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionview);

        // TabLayout tb = (TabLayout) findViewById(R.id.tabLayouts);
        viewPager = (ViewPager) findViewById(R.id.connectionViewpager);
        final connectionAdapter adapter = new connectionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


    }
}