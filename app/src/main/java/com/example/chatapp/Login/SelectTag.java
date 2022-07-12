package com.example.chatapp.Login;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class SelectTag extends AppCompatActivity {

    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tag);

        radioGroup = findViewById(R.id.tagselection);

    }
}