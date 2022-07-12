package com.example.chatapp.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class Tags1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags1);
        ImageView prev = findViewById(R.id.tags1back);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //startActivity(new Intent(Tags1.this, Register.class));
            }
        });
    }
}