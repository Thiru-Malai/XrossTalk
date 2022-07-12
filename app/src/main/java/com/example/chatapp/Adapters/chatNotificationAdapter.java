package com.example.chatapp.Adapters;

import static com.example.chatapp.R.*;

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
import com.example.chatapp.messageActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatNotificationAdapter extends RecyclerView.Adapter<chatNotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private List<Requests> mrequests;
    DatabaseReference reqreference, friendreference;
    ImageView btn_follow;
    TextView say_hi_view;
    ImageView btn_decline;
    private boolean ischat;
    private OnItemClick onItemClick;
    DatabaseReference mreference;
    private FirebaseUser firebaseUser;
    String usernames, imageurls;
    private String tagColor;
    TextView txttag;
    APIService apiService;
    String friendid;
    private com.example.chatapp.Login.ColorGetter colorGetter;

    int row_index = -1;
    String current_states = "he_sent_pending";


    public chatNotificationAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.item_chat_requests, parent, false);
        reqreference = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        friendreference = FirebaseDatabase.getInstance().getReference().child("ChatConnections");
        mreference = FirebaseDatabase.getInstance().getReference();
        btn_follow = (ImageView) view.findViewById(id.say_hi_btn1);
        btn_decline = (ImageView) view.findViewById(id.ignore_chat1);
        say_hi_view = (TextView) view.findViewById(id.say_hi_user1);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        txttag = view.findViewById(id.txttagonlayouts1);
        return new chatNotificationAdapter.ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users user = mUsers.get(position);
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        //setColor();
        (holder.txttag).setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) txttag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
        friendid = user.getFrid();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getStatus().equals("pending")) {
            current_states = "he_sent_pending";
        } else if (user.getStatus().equals("")) {
            current_states = "friend";
        } else {
            current_states = "nothing_happend";
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.friendprofile.class);
                intent.putExtra("userprofileid", friendid);
                mContext.startActivity(intent);

            }
        });

        if (user.getStatus().equals("pending")) {
            current_states = "he_sent_pending";
        }
        reqreference.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        current_states = "he_sent_pending";

                        holder.say_hi_view.setText("SAY HI...");
                        holder.btn_decline.setVisibility(View.VISIBLE);


                    }
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
                    current_states = "friend";
                    holder.say_hi_view.setText("connected");
                    holder.btn_decline.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendreference.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    current_states = "friend";

                    holder.say_hi_view.setText("connected");
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
                        current_states = "I_sent_pending";
                        holder.say_hi_view.setText("Requested");
                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        current_states = "I_sent_decline";
                        holder.say_hi_view.setText("Requested");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.username.setText(user.getUsername());


//        if (user.getImageURL().equals("default")){
//            holder.profile_image.setImageResource(drawable.user);
//        } else {
//            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
//        }

        // holder.profile_image.setImageResource(drawable.user);
        btn_decline.setVisibility(View.VISIBLE);
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
        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqreference.child(user.getId()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                            current_states = "nothing_happend";
                            say_hi_view.setText("connect+");
                            //btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        if (holder.say_hi_view.getText().toString().equals("SAY HI...")) {
            current_states = "he_sent_pending";
        }
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                row_index = holder.getAdapterPosition();
                friendreference.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            current_states = "friend";
                            for (int i = 0; i < mUsers.size(); i++) {
                                if (i == row_index) {
                                    say_hi_view.setText("connected");
                                    btn_decline.setVisibility(View.GONE);
                                    //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (current_states.equals("nothing_happend")) {
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
                                Toast.makeText(mContext.getApplicationContext(), "You have Send Friend Request", Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        holder.say_hi_view.setText("Requested");
                                        //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        current_states = "I_sent_pending";
                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_states.equals("I_sent_pending") || current_states.equals("I_sent_decline")) {
                    reqreference.child(user.getId()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                current_states = "nothing_happend";
                                say_hi_view.setText("connect+");
                                btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));

                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (current_states.equals("he_sent_pending")) {
                    reqreference.child(user.getId()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendMessage(user.getFrid(), user.getId(), "Hi");
                                sendMessage(user.getId(), user.getFrid(), "Hi");


                                HashMap hashMap = new HashMap();
                                hashMap.put("status", "friend");
                                hashMap.put("username", user.getUsername());
                                hashMap.put("imageUrl", user.getImageURL());
                                friendreference.child(firebaseUser.getUid()).child(user.getFrid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            friendreference.child(user.getFrid()).child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(mContext.getApplicationContext(), "You added as a Connection", Toast.LENGTH_SHORT).show();
                                                    current_states = "friend";
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
                                                    holder.say_hi_view.setText("Connected");
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
                if (current_states.equals("friend")) {
                    //
                }
            }
        });
        checkuserExistance(user.getId());
    }

    private void checkuserExistance(String id) {
        friendreference.child(firebaseUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    current_states = "friend";
                    say_hi_view.setText("connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (current_states.equals("nothing_happend")) {
            current_states = "nothing_happend";
            say_hi_view.setText("connect+");
            btn_decline.setVisibility(View.GONE);
        }
        if (current_states.equals("he_sent_pending")) {
            say_hi_view.setText("SAY HI...");
            btn_decline.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public ImageView btn_follow;
        public ImageView btn_decline;
        public TextView say_hi_view;
        public TextView txttag;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(id.username_userfrag1);
            profile_image = itemView.findViewById(id.image_user_userfrag1);
            btn_follow = itemView.findViewById(id.say_hi_btn1);
            btn_decline = itemView.findViewById(id.ignore_chat1);
            say_hi_view = itemView.findViewById(id.say_hi_user1);
            txttag = itemView.findViewById(id.txttagonlayouts1);


        }
    }

    public void sendMessage(final String myid, final String friendid, final String message) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        System.out.println(sdf.format(cal.getTime()));


        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference();
        mreference.child("Users").child(friendid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {


                if (!task.isSuccessful()) {
                    // Toast.makeText(.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    String mood = task.getResult().child("mood").getValue().toString();


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", myid);
                    hashMap.put("reciever", friendid);
                    hashMap.put("message", message);
                    hashMap.put("isseen", false);
                    hashMap.put("mood", mood);
                    hashMap.put("msgTime", sdf.format(cal.getTime()));


                    reference.child("Chats").push().setValue(hashMap);


                    final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatslist").child(myid).child(friendid);

                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if (!snapshot.exists()) {
                                reference1.child("id").setValue(friendid);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    final String msg = message;
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }


    private void setColor() {

        txttag.setTextColor(Color.parseColor(tagColor));
//        GradientDrawable myGrad = (GradientDrawable)txttag.getBackground();
//        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.app_symbol, username + " is available to chat", "Let's Chat",
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


    //check for last message


}
