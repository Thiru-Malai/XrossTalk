package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashs extends AppCompatActivity {
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashs);

        //creating thread that will sleep for 10 seconds
        Thread t = new Thread() {
            public void run() {

                try {
                    //sleep thread for 10 seconds, time in milliseconds
                    sleep(1000);

                    //start new activity
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Intent i = new Intent(splashs.this, com.example.chatapp.Login.Intro.class);
                        startActivity(i);
                        finish();

                    } else {
                        Intent in = new Intent(splashs.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                    //destroying Splash activity

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //start thread

            }
        };
        t.start();
    }
}
