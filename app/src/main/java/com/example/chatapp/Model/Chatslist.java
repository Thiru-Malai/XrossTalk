package com.example.chatapp.Model;

import java.util.Comparator;

public class Chatslist {

    public static Comparator<Chatslist> timecomparator = new Comparator<Chatslist>() {
        @Override
        public int compare(Chatslist t1, Chatslist t2) {
            return Long.compare(t1.getTime(), t2.getTime());
        }
    };
    String id;
    long time;

    public Chatslist(String id, long time) {
        this.id = id;
        this.time = time;
    }

    public Chatslist() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
