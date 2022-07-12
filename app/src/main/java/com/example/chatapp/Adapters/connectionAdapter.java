package com.example.chatapp.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.Connections;

public class connectionAdapter extends FragmentPagerAdapter {

    public connectionAdapter(
            FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return new Connections();


    }

    @Override
    public int getCount() {
        return 1;
    }
}