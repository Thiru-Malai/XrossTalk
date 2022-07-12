package com.example.chatapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.chatapp.ConnectionView;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Model.Users;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class friendprofile extends AppCompatActivity {

    private ImageView imgProfile, btnEdit;
    private TextView txtUsername, txtAbout, txtPopularity, txtConnections, txtTag;
    private String userid;
    private FirebaseAuth auth;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    private String tagColor;
    RelativeLayout connections;
    String usernames;
    APIService apiService;
    String imageurls, tags;
    private List<Users> mUsers;
    private Button say_hi_view, btn;
    public String current_stat = "nothing_happend";
    public String current = "nothing_happend";

    private FirebaseUser loggedUser;
    private FirebaseDatabase mdb;
    private DatabaseReference mreference;
    String friendid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile);

        auth = FirebaseAuth.getInstance();

        imgProfile = findViewById(R.id.imgProfiles);
        btnEdit = findViewById(R.id.btnPrevs);
        txtUsername = findViewById(R.id.txtUsernames);
        txtTag = findViewById(R.id.txtTagnames);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        txtAbout = findViewById(R.id.txtAbouts);
        txtConnections = findViewById(R.id.txtConnectionss);
        txtPopularity = findViewById(R.id.popu);
        connections = findViewById(R.id.connectionViews);
        btn = findViewById(R.id.btnfollow);
        say_hi_view = findViewById(R.id.say_hi_btns);

        colorGetter = new com.example.chatapp.Login.ColorGetter();
        mdb = FirebaseDatabase.getInstance();
        mreference = mdb.getReference();
        mUsers = new ArrayList<>();

        loggedUser = auth.getCurrentUser();
        DatabaseReference reqreference = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        DatabaseReference friendreference = FirebaseDatabase.getInstance().getReference().child("ChatConnections");
        DatabaseReference reqreference1 = FirebaseDatabase.getInstance().getReference().child("Requests");
        DatabaseReference friendreference1 = FirebaseDatabase.getInstance().getReference().child("Connections");
        DatabaseReference blockref = FirebaseDatabase.getInstance().getReference().child("Blocked");
        userid = loggedUser.getUid();
        friendid = getIntent().getStringExtra("userprofileid");


        mreference.child("Users").child(friendid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(friendprofile.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {
                    String about = task.getResult().child("about").getValue().toString();
                    String username = task.getResult().child("username").getValue().toString();
                    String tagname = task.getResult().child("tag").getValue().toString();
                    String connections = task.getResult().child("connections").getValue().toString();
                    String image = task.getResult().child("imageURL").getValue().toString();


                    txtUsername.setText(username);
                    txtTag.setText(tagname);
                    tagColor = colorGetter.getColor(tagname);
                    setColor();
                    txtConnections.setText(connections);
                    txtAbout.setText(about);
                    txtAbout.setMovementMethod(new ScrollingMovementMethod());
                    Picasso.with(friendprofile.this).load(image).resize(160, 160).into(imgProfile);

                }
            }
        });
        Query query1 = FirebaseDatabase.getInstance().getReference("Connections").child(friendid).orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);

                    assert req != null;
                    assert userid != null;

                    if (req.getId().equals(friendid)) {
                        mUsers.add(req);
                    }

                }
                int size = mUsers.size();
                if (size < 10) txtPopularity.setText("New");
                else if (size >= 10 && size < 50) txtPopularity.setText("Noticed");
                else if (size >= 10 && size < 50) txtPopularity.setText("Recognised");
                else if (size >= 50 && size < 100) txtPopularity.setText("Well Known");
                else if (size >= 100 && size < 500) txtPopularity.setText("Respected");
                else if (size >= 500 && size < 1000) txtPopularity.setText("Famous");
                else if (size >= 1000 && size < 10000) txtPopularity.setText("Popular");
                else if (size >= 10000 && size < 100000) txtPopularity.setText("Influencer");
                else if (size >= 100000 && size < 1000000) txtPopularity.setText("Celebrity");
                else if (size >= 1000000 && size < 10000000) txtPopularity.setText("Star");
                else if (size >= 10000000 && size < 100000000) txtPopularity.setText("Big Shot");
                else if (size >= 100000000 && size < 1000000000) txtPopularity.setText("Icon");
                else if (size >= 1000000000 && size < 1000000000) txtPopularity.setText("Poineer");
                else txtPopularity.setText("Ace");


                Integer s = new Integer(size);
                txtConnections.setText(s.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reqreference.child(userid).child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        // current_states[holder.getAdapterPosition()] = "he_sent_pending";

                        say_hi_view.setText("pending");
                        say_hi_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                        //   holder.btn_decline.setVisibility(View.VISIBLE);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendreference.child(friendid).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // current_states[holder.getAdapterPosition()] = "friend";
                    say_hi_view.setText("connected");
                    say_hi_view.setOnClickListener(new View.OnClickListener() {
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


        friendreference.child(userid).child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // current_states[holder.getAdapterPosition()] = "friend";

                    say_hi_view.setText("connected");
                    say_hi_view.setOnClickListener(new View.OnClickListener() {
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

        reqreference.child(friendid).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        try {
                            current_stat = "I_sent_pending";
                        } catch (Exception e) {

                        }
                        say_hi_view.setText("Requested");
                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        current_stat = "I_sent_decline";
                        say_hi_view.setText("Requested");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mreference.child("Users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(friendprofile.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    usernames = task.getResult().child("username").getValue().toString();
                    imageurls = task.getResult().child("imageURL").getValue().toString();
                    tags = task.getResult().child("tag").getValue().toString();


                }
            }
        });
        friendreference1.child(userid).child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    btn.setText("connected");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    //holder.btn_follow.setBackground(mContext.getDrawable(R.drawable.mybutton));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        blockref.child(userid).child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    btn.setText("Blocked");
                    say_hi_view.setText("Blocked");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    say_hi_view.setOnClickListener(new View.OnClickListener() {
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
        blockref.child(friendid).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    btn.setText("Blocked");
                    say_hi_view.setText("Blocked");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    say_hi_view.setOnClickListener(new View.OnClickListener() {
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
        friendreference1.child(friendid).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // current_state[holder.getAdapterPosition()] = "friend";
                    btn.setText("connected");
                    btn.setOnClickListener(new View.OnClickListener() {
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
        reqreference1.child(friendid).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        // current_state[holder.getAdapterPosition()] = "I_sent_pending";
                        btn.setText("Requested");
                        current = "I_sent_decline";

                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        current = "I_sent_decline";
                        btn.setText("Requested");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reqreference1.child(userid).child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        //current_state = "he_sent_pending";

                        btn.setText("pending");

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(friendprofile.this, "User already Sent the Request", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        say_hi_view.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (current_stat.equals("nothing_happend")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "pending");
                    hashMap.put("username", usernames);
                    hashMap.put("imageURL", imageurls);
                    hashMap.put("id", friendid);
                    hashMap.put("tag", tags);
                    hashMap.put("frid", userid);
                    hashMap.put("search", usernames.toLowerCase());
                    reqreference.child(friendid).child(userid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(friendprofile.this, "You have Send Friend Request", Toast.LENGTH_SHORT).show();
                                say_hi_view.setText("Requested");
                                //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                current_stat = "I_sent_pending";
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        sendNotificationc(friendid, users.getUsername(), "You have a chat request");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {
                                Toast.makeText(friendprofile.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_stat.equals("I_sent_pending") || current_stat.equals("I_sent_decline")) {

                    reqreference.child(friendid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(friendprofile.this, "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                say_hi_view.setText("Say Hi...");
                                current_stat = "nothing_happend";

                                //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                            } else {
                                Toast.makeText(friendprofile.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_stat.equals("he_sent_pending")) {
                    Toast.makeText(friendprofile.this, "User already sent request", Toast.LENGTH_SHORT).show();
                }
                if (current_stat.equals("friend")) {
                    //
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (current.equals("nothing_happend")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "pending");
                    hashMap.put("username", usernames);
                    hashMap.put("imageURL", imageurls);
                    hashMap.put("id", friendid);
                    hashMap.put("tag", tags);
                    hashMap.put("frid", userid);
                    hashMap.put("search", usernames.toLowerCase());
                    reqreference1.child(friendid).child(userid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {


                                //Toast.makeText(mContext.getApplicationContext(), "You have Send Friend Request", Toast.LENGTH_SHORT).show();
                                btn.setText("Requested");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        sendNotification(friendid, users.getUsername(), "You have a chat request");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                // sendNotification(user.getId(), user.getUsername(), "you have a connection request");

                                current = "I_sent_pending";


                            } else {
                                Toast.makeText(friendprofile.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current.equals("I_sent_pending") || current.equals("I_sent_decline")) {

                    reqreference1.child(friendid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                current = "nothing_happend";
                                btn.setText("connect+");


                            } else {
                                Toast.makeText(friendprofile.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current.equals("he_sent_pending")) {

                }

                if (current.equals("friend")) {
                    //
                }
            }
        });

    }

    private void setColor() {
        txtTag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) txtTag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void sendNotificationc(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(userid, R.drawable.app_symbol, message + " from " + username, "Chat Request",
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

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(userid, R.drawable.app_symbol, message + " from " + username, "Connection Request",
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
}