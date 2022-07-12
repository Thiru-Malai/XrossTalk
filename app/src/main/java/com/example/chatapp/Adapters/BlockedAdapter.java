package com.example.chatapp.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.Blocked;
import com.example.chatapp.Connections;

public class BlockedAdapter extends FragmentPagerAdapter {

    public BlockedAdapter(
            FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return new Blocked();


    }

    @Override
    public int getCount() {
        return 1;
    }
}