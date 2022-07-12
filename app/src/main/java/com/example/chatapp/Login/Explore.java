package com.example.chatapp.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class Explore extends AppCompatActivity {

    private Button btnLogout, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Explore.this, Profile.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Explore.this, Login.class));
            }
        });
    }
}