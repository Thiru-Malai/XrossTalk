package com.example.chatapp.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.ChatRequest;
import com.example.chatapp.ConnectionRequestNotification;

public class NotificationtabAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public NotificationtabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatRequest chatRequest = new ChatRequest();
                return chatRequest;
            case 1:
                ConnectionRequestNotification connectionRequest = new ConnectionRequestNotification();
                return connectionRequest;
            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
