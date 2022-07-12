package com.example.chatapp.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.R;

public class Intro extends AppCompatActivity {

    private ViewPager slider;
    private LinearLayout slidebtnlayout;
    private Button btnGetStarted;

    private com.example.chatapp.Login.SliderAdapter sliderAdapter;

    private int[] rectangle_buttons = {R.id.rectangle1, R.id.rectangle2, R.id.rectangle3};
    private Button[] btnRectangle = new Button[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        slider = findViewById(R.id.SplashPager);
        slidebtnlayout = findViewById(R.id.btnSlide);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        sliderAdapter = new com.example.chatapp.Login.SliderAdapter(this);

        slider.setAdapter(sliderAdapter);

        btnGetStarted.setVisibility(View.GONE);

        for (int i = 0; i < btnRectangle.length; i++) {
            btnRectangle[i] = findViewById(rectangle_buttons[i]);
        }

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intro.this, com.example.chatapp.Login.Selection.class);
                startActivity(intent);
                finish();
            }
        });

        changeslidebtn(0);
        slider.addOnPageChangeListener(viewListener);

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            changeslidebtn(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void changeslidebtn(int position) {
        btnRectangle[position].setBackgroundColor(Color.parseColor("#FB8B00"));

        switch (position) {
            case 0: {
                btnRectangle[1].setBackgroundColor(Color.parseColor("#3F4959"));
                btnRectangle[2].setBackgroundColor(Color.parseColor("#3F4959"));
                btnGetStarted.setVisibility(View.GONE);
                break;
            }
            case 1: {
                btnRectangle[0].setBackgroundColor(Color.parseColor("#3F4959"));
                btnRectangle[2].setBackgroundColor(Color.parseColor("#3F4959"));
                btnGetStarted.setVisibility(View.GONE);
                break;
            }
            case 2: {
                btnRectangle[0].setBackgroundColor(Color.parseColor("#3F4959"));
                btnRectangle[1].setBackgroundColor(Color.parseColor("#3F4959"));
                btnGetStarted.setVisibility(View.VISIBLE);
                break;
            }


        }
    }
}