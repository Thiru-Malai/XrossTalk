package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.chatapp.Adapters.connectionAdapter;


public class connectionsMain
        extends AppCompatActivity {

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connections_main);

        // Find the view pager that will
        // allow the user to swipe
        // between fragments
        ViewPager viewPager
                = (ViewPager) findViewById(
                R.id.viewpagerconnection);

        connectionAdapter adapter
                = new connectionAdapter(
                getSupportFragmentManager()) {
        };

        // Set the adapter onto
        // the view pager
        viewPager.setAdapter(adapter);
    }
}