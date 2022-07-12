package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;

import android.graphics.drawable.GradientDrawable;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private boolean ischat;
    private OnItemClick onItemClick;
    private FirebaseUser firebaseUser;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    TextView txttag;
    private String tagColor;
    String theLastMessage;
    String a;
    long final_time;
    boolean flag = false;
    private String lastMsgTime;

    public chatAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_of_chatusers, parent, false);
        txttag = view.findViewById(R.id.txttagonlayouts);

        return new chatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Users user = mUsers.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();


        holder.username.setText(user.getUsername());
        if (user.getId().equals(firebaseUser.getUid())) {
            //    holder.btn_follow.setVisibility(View.GONE);
        }
        if (!(user.getImageURL().equals("https://firebasestorage.googleapis.com/v0/b/chatapp-2d7a9.appspot.com/o/images%2Fuser.png?alt=media&token=0628b0d7-2537-42c4-8eb6-c7015965f1fd"))) {
            Picasso.with(mContext.getApplicationContext()).load(user.getImageURL().toString()).resize(160, 160).into(holder.profile_image);
        }
        if (ischat) {
            lastMessage(user.getId(), holder.last_msg, holder.txtLastMessageTime);
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

        getUnreadMsgCount(user.getId(), holder.backgroundUnreadMsgCount, holder.txtUnreadMsgCount, holder.txtLastMessageTime);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.messageActivity.class);
                intent.putExtra("friendid", user.getId());
                mContext.startActivity(intent);
            }
        });


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
        public TextView txttag;
        private TextView txtLastMessageTime;
        private TextView txtUnreadMsgCount;
        private ImageView backgroundUnreadMsgCount;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_userfrag);
            profile_image = itemView.findViewById(R.id.image_user_userfrag);
            img_on = itemView.findViewById(R.id.image_online);
            img_off = itemView.findViewById(R.id.image_offline);
            last_msg = itemView.findViewById(R.id.lastMessage);
            btn_follow = itemView.findViewById(R.id.follow);
            txttag = itemView.findViewById(R.id.txttagonlayouts);
            txtLastMessageTime = itemView.findViewById(R.id.txtLastMessagetime);
            txtUnreadMsgCount = itemView.findViewById(R.id.txtUnreadMsgCount);
            backgroundUnreadMsgCount = itemView.findViewById(R.id.backgroundUnreadMsgCount);
        }
    }

    public void getUnreadMsgCount(String userid, ImageView background, TextView txtUnreadMsgCount, TextView txtLastMsgTime) {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadMsgCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chat = snapshot.getValue(Chats.class);
                    if (firebaseUser.getUid() != null && chat.getSender() != null && userid != null) {
                        if (chat.getReciever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                            if (!chat.isIsseen()) {
                                unreadMsgCount++;
                            }
                        }
                    }
                }


                if (unreadMsgCount == 0) {
                    background.setVisibility(View.GONE);
                    txtUnreadMsgCount.setVisibility(View.GONE);
                    txtLastMsgTime.setTextColor(Color.parseColor("#717171"));
                } else {
                    txtLastMsgTime.setTextColor(Color.WHITE);
                    txtUnreadMsgCount.setVisibility(View.VISIBLE);
                    background.setVisibility(View.VISIBLE);
                    txtUnreadMsgCount.setText(Integer.toString(unreadMsgCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg, TextView txtLastMsgTime) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMsgTime = "default";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chat = snapshot.getValue(Chats.class);
                    if (firebaseUser.getUid() != null && chat.getSender() != null && userid != null) {
                        if (chat.getReciever().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReciever().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            //  final_time = chat.getTime();
                            lastMsgTime = chat.getMsgTime();
                            //a = final_time;
                            flag = true;


                        }

                    }

                }
                if (flag) {


                }

                switch (lastMsgTime) {
                    case "default":
                        txtLastMsgTime.setText("        ");
                        break;

                    default:
                        txtLastMsgTime.setText(lastMsgTime);
                        break;
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

    private void setColor() {
        txttag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) txttag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
