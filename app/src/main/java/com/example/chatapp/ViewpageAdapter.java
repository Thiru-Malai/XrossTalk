package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewpageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    ArrayList<String> titles;


    public ViewpageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragements(Fragment fragment, String title) {
        titles.add(title);
        fragments.add(fragment);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
