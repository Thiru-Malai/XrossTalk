package com.example.chatapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class fullmsgadapter extends RecyclerView.Adapter<fullmsgadapter.MyViewHolder> {


    Context context;
    List<Chats> chatslist;
    String imageURL;
    String friendid;

    public static final int MESSAGE_RIGHT = 0; // FOR ME (
    public static final int MESSAGE_LEFT = 1; // FOR FRIEND
    public static final int ANGRY_LEFT = 3;
    public static final int ANGRY_RIGHT = 2;
    public static final int ATTACHED_LEFT = 5;
    public static final int ATTACHED_RIGHT = 6;
    public static final int BROKEN_LEFT = 7;
    public static final int BROKEN_RIGHT = 8;
    public static final int CONFUSED_LEFT = 9;
    public static final int CONFUSED_RIGHT = 10;
    public static final int EXCITED_LEFT = 11;
    public static final int EXCITED_RIGHT = 12;
    public static final int HAPPY_LEFT = 13;
    public static final int HAPPY_RIGHT = 14;
    public static final int HIGH_LEFT = 15;
    public static final int HIGH_RIGHT = 16;
    public static final int ROMANTIC_LEFT = 17;
    public static final int ROMANTIC_RIGHT = 18;
    public static final int SAD_LEFT = 19;
    public static final int SAD_RIGHT = 20;


    public fullmsgadapter(Context context, List<Chats> chatslist, String imageURL, String friendid) {
        this.context = context;
        this.chatslist = chatslist;
        this.imageURL = imageURL;
        this.friendid = friendid;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_RIGHT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);


        } else if (viewType == MESSAGE_LEFT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);

        } else if (viewType == ANGRY_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.angry_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ANGRY_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.angry_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ATTACHED_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.attached_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ATTACHED_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.attached_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == BROKEN_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.broken_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == BROKEN_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.broken_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == CONFUSED_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.confused_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == CONFUSED_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.confused_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == EXCITED_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.excited_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == EXCITED_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.excited_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == HAPPY_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.happy_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == HAPPY_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.happy_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == HIGH_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.high_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == HIGH_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.high_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ROMANTIC_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.romantic_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ROMANTIC_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.romantic_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == SAD_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sad_left, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sad_right, parent, false);
            return new MyViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Chats chats = chatslist.get(position);

        if (chats.getMessage() != null) {
            holder.messagetext.setText(chats.getMessage());
        }


        if (position == chatslist.size() - 1) {

            if (chats.isIsseen()) {

                holder.seen.setText("seen");


            } else {
                holder.seen.setText("Delivered");
            }

        } else {
            holder.seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return chatslist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messagetext, seen;
        ImageView imageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messagetext = itemView.findViewById(R.id.show_message);
            imageView = itemView.findViewById(R.id.chat_image);
            seen = itemView.findViewById(R.id.text_Seen);
        }
    }


    @Override
    public int getItemViewType(int position) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (chatslist.get(position).getSender().equals(user.getUid())) {
            if (chatslist.get(position).getMood().equals("happy")) {
                return HAPPY_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("angry")) {
                return ANGRY_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("attached")) {
                return ATTACHED_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("broken")) {
                return BROKEN_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("confused")) {
                return CONFUSED_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("excited")) {
                return EXCITED_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("high")) {
                return HIGH_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("romantic")) {
                return ROMANTIC_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("sad")) {
                return SAD_RIGHT;
            }
            if (chatslist.get(position).getMood().equals("normal")) {
                return MESSAGE_RIGHT;
            }
        } else {
            if (chatslist.get(position).getMood().equals("happy")) {
                return HAPPY_LEFT;
            }
            if (chatslist.get(position).getMood().equals("angry")) {
                return ANGRY_LEFT;
            }
            if (chatslist.get(position).getMood().equals("attached")) {
                return ATTACHED_LEFT;
            }
            if (chatslist.get(position).getMood().equals("broken")) {
                return BROKEN_LEFT;
            }
            if (chatslist.get(position).getMood().equals("confused")) {
                return CONFUSED_LEFT;
            }
            if (chatslist.get(position).getMood().equals("excited")) {
                return EXCITED_LEFT;
            }
            if (chatslist.get(position).getMood().equals("high")) {
                return HIGH_LEFT;
            }
            if (chatslist.get(position).getMood().equals("romantic")) {
                return ROMANTIC_LEFT;
            }
            if (chatslist.get(position).getMood().equals("sad")) {
                return SAD_LEFT;
            }
            if (chatslist.get(position).getMood().equals("normal")) {
                return MESSAGE_LEFT;
            }


        }

        return -1;
    }
}
