package com.example.chatapp.Adapters;

import static com.example.chatapp.R.*;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

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
    APIService apiService;
    String friendid;
    int row_index = -1;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    TextView txttag;
    private String tagColor;
    String current_state[] = new String[1000];


    public SearchAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.layoutofusers, parent, false);
        reqreference = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendreference = FirebaseDatabase.getInstance().getReference().child("Connections");
        blockref = FirebaseDatabase.getInstance().getReference().child("Blocked");
        mreference = FirebaseDatabase.getInstance().getReference();
        btn_follow = view.findViewById(id.follow);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        btn_decline = view.findViewById(id.decline);
        txttag = view.findViewById(id.txttagonlayouts);
        return new SearchAdapter.ViewHolder(view);
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
        friendid = user.getFrid();
        for (int i = 0; i < mUsers.size(); i++) {
            current_state[i] = "nothing_happend";
        }
        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.friendprofile.class);
                intent.putExtra("userprofileid", user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.username.setText(user.getUsername());
        if (user.getStatus().equals("pending")) {

        }
        if (!(user.getImageURL().equals("https://firebasestorage.googleapis.com/v0/b/chatapp-2d7a9.appspot.com/o/images%2Fuser.png?alt=media&token=0628b0d7-2537-42c4-8eb6-c7015965f1fd"))) {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        mreference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext.getApplicationContext(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    usernames = task.getResult().child("username").getValue().toString();
                    imageurls = task.getResult().child("imageURL").getValue().toString();


                }
            }
        });

        friendreference.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    holder.btn_follow.setText("connected");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        friendreference.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // current_state[holder.getAdapterPosition()] = "friend";
                    holder.btn_follow.setText("connected");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
                    holder.btn_decline.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reqreference.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        // current_state[holder.getAdapterPosition()] = "I_sent_pending";
                        holder.btn_follow.setText("Requested");
                        Drawable buttonDrawable = holder.btn_follow.getBackground();
                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                        //the color is a direct color int and not a color resource
                        DrawableCompat.setTint(buttonDrawable, Color.parseColor("#19B5FE"));
                        holder.btn_follow.setBackground(buttonDrawable);

                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        current_state[holder.getAdapterPosition()] = "I_sent_decline";
                        holder.btn_follow.setText("Requested");
                        holder.btn_follow.setBackground(mContext.getDrawable(drawable.bluebutton));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reqreference.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        //current_state = "he_sent_pending";

                        holder.btn_follow.setText("pending");

                        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(mContext.getApplicationContext(), "User already Sent the Request", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        for (int i = 0; i < mUsers.size(); i++) {
            if (holder.btn_follow.getText().toString().equals("Requested")) {
                current_state[i] = "I_sent_pending";
            }
        }

        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqreference.child(firebaseUser.getUid()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                            current_state[holder.getAdapterPosition()] = "nothing_happend";
                            btn_follow.setText("connect+");
                            btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        holder.btn_follow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                row_index = holder.getAdapterPosition();

                if (current_state[row_index].equals("nothing_happend")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "pending");
                    hashMap.put("username", usernames);
                    hashMap.put("imageURL", imageurls);
                    hashMap.put("id", user.getId());
                    hashMap.put("tag", user.getTag());
                    hashMap.put("frid", firebaseUser.getUid());
                    hashMap.put("search", user.getUsername().toLowerCase());
                    reqreference.child(user.getId()).child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        //Toast.makeText(mContext.getApplicationContext(), "You have Send Friend Request", Toast.LENGTH_SHORT).show();
                                        holder.btn_follow.setText("Requested");
                                        removeAt(holder.getAdapterPosition());
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Users users = snapshot.getValue(Users.class);
                                                sendNotification(user.getId(), users.getUsername(), "You have a chat request");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        // sendNotification(user.getId(), user.getUsername(), "you have a connection request");
                                        holder.btn_follow.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        current_state[row_index] = "I_sent_pending";
                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_state[row_index].equals("I_sent_pending") || current_state.equals("I_sent_decline")) {

                    reqreference.child(user.getId()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        // Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                        current_state[row_index] = "nothing_happend";
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
                if (current_state[holder.getAdapterPosition()].equals("he_sent_pending")) {

                }

                if (current_state.equals("friend")) {
                    //
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public Button btn_follow;
        public Button btn_decline;
        public TextView txttag;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(id.username_userfrag);
            profile_image = itemView.findViewById(id.image_user_userfrag);
            btn_follow = itemView.findViewById(id.follow);
            btn_decline = itemView.findViewById(id.decline);
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
                    Data data = new Data(firebaseUser.getUid(), R.drawable.app_symbol, message + " from " + username, "Connection Request",
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
        txttag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) txttag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void removeAt(int position) {
        Users user = mUsers.get(position);
        mUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUsers.size());
        mUsers.add(user);
    }


}
