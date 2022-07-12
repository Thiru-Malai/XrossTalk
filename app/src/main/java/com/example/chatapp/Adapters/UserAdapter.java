package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private boolean ischat;
    private OnItemClick onItemClick;
    private FirebaseUser firebaseUser;

    Typeface MR, MRR;
    String theLastMessage;

    public UserAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.userlayouthi, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Users user = mUsers.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //holder.say_hi_btn.setVisibility(View.VISIBLE);


        holder.username.setText(user.getUsername());
        if (user.getId().equals(firebaseUser.getUid())) {
            //holder.btn_follow.setVisibility(View.GONE);
        }


        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.user_icon);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.messageActivity.class);
                intent.putExtra("friendid", user.getId());
                mContext.startActivity(intent);
            }
        });


//        holder.profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(user.getId() !=null){
//                onItemClick.onItemCLick(user.getId(),view);
//            }}
//        });
    }

    private void addNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "requested following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        public Button btn_follow;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_userfrag);
            profile_image = itemView.findViewById(R.id.image_user_userfrag);
            img_on = itemView.findViewById(R.id.image_online);
            img_off = itemView.findViewById(R.id.image_offline);
            last_msg = itemView.findViewById(R.id.lastMessage);
            btn_follow = itemView.findViewById(R.id.follow);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chat = snapshot.getValue(Chats.class);
                    if (firebaseUser.getUid() != null && chat.getSender() != null && userid != null) {
                        if (chat.getReciever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReciever().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("        ");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isFollowing(final String userid, final Button button) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
