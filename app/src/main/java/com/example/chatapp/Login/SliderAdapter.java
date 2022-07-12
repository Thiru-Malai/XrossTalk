package com.example.chatapp.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chatapp.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.man,
            R.drawable.pradfamily,
            R.drawable.fam
    };

    public String[] slide_headings = {
            "Express your personality",
            "Meet New people",
            "Share your memories"
    };

    public String[] slide_contents = {
            "and be you!",
            "and connect with your mutuals",
            "with the people you love"
    };


    @Override
    public int getCount() {

        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view;

        view = layoutInflater.inflate(R.layout.slider_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgIntroImage);
        TextView sliderHeading = (TextView) view.findViewById(R.id.introTxtHeader);
        TextView sliderContent = (TextView) view.findViewById(R.id.introTxt);

        sliderHeading.setText(slide_headings[position]);
        sliderContent.setText(slide_contents[position]);
        imageView.setImageResource(slide_images[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
