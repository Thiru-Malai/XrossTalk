package com.example.chatapp.Adapters;

import static com.example.chatapp.R.*;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.Requests;
import com.example.chatapp.Model.Users;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.example.chatapp.R.drawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class connectionReqAdapter extends RecyclerView.Adapter<connectionReqAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private List<Requests> mrequests;
    DatabaseReference reqreference, friendreference, blockref;
    Button btn_follow, btn_decline;
    private boolean ischat;
    private OnItemClick onItemClick;
    DatabaseReference mreference;
    private FirebaseUser firebaseUser;
    String usernames, imageurls;
    int row_index = -1;
    public String current_state = "he_sent_pending";
    APIService apiService;
    private String tagColor;
    public String tag;
    String friendid;
    TextView txttag;
    private com.example.chatapp.Login.ColorGetter colorGetter;

    public connectionReqAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.item_connections, parent, false);
        reqreference = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendreference = FirebaseDatabase.getInstance().getReference().child("Connections");
        mreference = FirebaseDatabase.getInstance().getReference();
        btn_follow = view.findViewById(id.follow_connect);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        btn_decline = view.findViewById(id.connect_decline);
        txttag = view.findViewById(id.txttagonlayouts);

        blockref = FirebaseDatabase.getInstance().getReference().child("Blocked");
        return new connectionReqAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users user = mUsers.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        holder.username.setText(user.getUsername());
        friendid = user.getFrid();


//        if (user.getImageURL().equals("default")){
//            holder.profile_image.setImageResource(drawable.user);
//         else {
        Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
//
        // Picasso.with(mContext.getApplicationContext()).load(user.getImageURL().toString()).resize(160,160).into(holder.profile_image);
        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();
        txttag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) (holder.txttag).getBackground();
        myGrad.setStroke(3, Color.parseColor(tagColor));

        mreference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext.getApplicationContext(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    usernames = task.getResult().child("username").getValue().toString();
                    tag = task.getResult().child("tag").getValue().toString();
                    imageurls = task.getResult().child("imageURL").getValue().toString();


                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.friendprofile.class);
                intent.putExtra("userprofileid", friendid);
                mContext.startActivity(intent);
            }
        });


        blockref.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    holder.btn_follow.setText("Blocked");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        blockref.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    holder.btn_follow.setText("Blocked");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        current_state = "he_sent_pending";

        btn_follow.setText("accept");
        btn_decline.setVisibility(View.VISIBLE);
        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqreference.child(firebaseUser.getUid()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                            current_state = "nothing_happend";
                            btn_follow.setText("connect+");
                            btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        if (mUsers.size() > 1 && current_state.equals("he_sent_pending")) {
            sendNotification(firebaseUser.getUid(), user.getUsername(), "you have a friend request");
        }


        holder.btn_follow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                row_index = holder.getAdapterPosition();

                if (current_state.equals("nothing_happend")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "pending");
                    hashMap.put("username", usernames);
                    hashMap.put("imageURL", imageurls);
                    hashMap.put("id", user.getId());
                    hashMap.put("frid", firebaseUser.getUid());
                    hashMap.put("search", user.getUsername().toLowerCase());
                    reqreference.child(user.getId()).child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        Toast.makeText(mContext.getApplicationContext(), "You have Send Friend Request", Toast.LENGTH_SHORT).show();
                                        holder.btn_follow.setText("Requested");
                                        holder.btn_follow.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        current_state = "I_sent_pending";
                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_state.equals("I_sent_pending") || current_state.equals("I_sent_decline")) {

                    reqreference.child(user.getId()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                        current_state = "nothing_happend";
                                        //  btn_follow.setVisibility(View.VISIBLE);
                                        holder.btn_follow.setText("connect+");
                                        holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_state.equals("he_sent_pending")) {
                    reqreference.child(firebaseUser.getUid()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("tag", user.getTag());
                                hashMap.put("id", firebaseUser.getUid());
                                hashMap.put("search", user.getUsername().toLowerCase());
                                hashMap.put("status", "friend");
                                hashMap.put("username", user.getUsername());
                                hashMap.put("imageURL", user.getImageURL());
                                hashMap.put("frid", user.getFrid());

                                HashMap hashMap2 = new HashMap();
                                hashMap2.put("tag", tag);
                                hashMap2.put("id", user.getFrid());
                                hashMap2.put("search", user.getUsername().toLowerCase());
                                hashMap2.put("status", "friend");

                                hashMap2.put("username", usernames);
                                hashMap2.put("imageURL", imageurls);
                                hashMap2.put("frid", firebaseUser.getUid());

                                btn_follow.setVisibility(View.VISIBLE);
                                friendreference.child(firebaseUser.getUid()).child(user.getFrid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Users users = snapshot.getValue(Users.class);
                                                    sendNotification(user.getFrid(), users.getUsername(), "accepted the friend request");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            // sendNotification(user.getFrid(), user.getUsername(), "accepted the friend request");

                                            friendreference.child(user.getFrid()).child(firebaseUser.getUid()).updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatslist").child(user.getFrid()).child(user.getId());

                                                    reference1.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                            if (!snapshot.exists()) {
                                                                reference1.child("id").setValue(user.getId());
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatslist").child(user.getId()).child(user.getFrid());

                                                    reference2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                            if (!snapshot.exists()) {
                                                                reference2.child("id").setValue(user.getFrid());

                                                            }
                                                            if (task.isSuccessful()) {
                                                                for (int i = 0; i < mUsers.size(); i++) {
                                                                    if (i == row_index) {
                                                                        Toast.makeText(mContext.getApplicationContext(), "You added as a Connection", Toast.LENGTH_SHORT).show();
                                                                        current_state = "friend";
                                                                        holder.btn_follow.setText("Connected");
                                                                        holder.btn_decline.setVisibility(View.GONE);

                                                                    }
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                if (current_state.equals("friend")) {
                    //
                }
            }
        });
    }

    private void checkuserExistance(String id) {

        if (current_state.equals("nothing_happend")) {
            current_state = "nothing_happend";

            btn_follow.setText("connect+");
            btn_decline.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public CircleImageView profile_image;
        public TextView txttag;
        public Button btn_follow;
        public Button btn_decline;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(id.usernameconnect);
            profile_image = itemView.findViewById(id.profile_pic_users_connect);
            btn_follow = itemView.findViewById(id.follow_connect);
            btn_decline = itemView.findViewById(id.connect_decline);
            txttag = itemView.findViewById(id.txttagonlayouts);

        }
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.app_symbol, username + " is added you as a connection", "Connection",
                            friendid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setColor() {
//        GradientDrawable drawable = (GradientDrawable)txttag.getBackground();
//        drawable.setStroke(3, Color.parseColor(tagColor));

    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    //check for last message


}
