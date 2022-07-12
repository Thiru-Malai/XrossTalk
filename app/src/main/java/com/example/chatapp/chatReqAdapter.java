package com.example.chatapp;

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

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatReqAdapter extends RecyclerView.Adapter<chatReqAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private List<Requests> mrequests;
    DatabaseReference reqreference, friendreference, blockref;
    ImageView btn_follow;
    Button btn_decline;
    private boolean ischat;
    private OnItemClick onItemClick;
    DatabaseReference mreference;
    private FirebaseUser firebaseUser;
    public String usernames, imageurls;
    TextView say_hi_view;
    int row_index = -1;
    private String tagColor;
    APIService apiService;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    String friendid;
    CircleImageView profile_image;
    TextView txttag;
    public String tag;
    String current_states[] = new String[1000];


    public chatReqAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.userlayouthi, parent, false);
        reqreference = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        friendreference = FirebaseDatabase.getInstance().getReference().child("ChatConnections");
        mreference = FirebaseDatabase.getInstance().getReference();
        btn_follow = view.findViewById(id.say_hi_btn);
        btn_decline = view.findViewById(id.ignore_chat);
        profile_image = view.findViewById(id.image_user_userfrag);
        blockref = FirebaseDatabase.getInstance().getReference().child("Blocked");
        say_hi_view = view.findViewById(id.say_hi_user);
        txttag = view.findViewById(id.txttagonlayouts);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        return new chatReqAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users user = mUsers.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        for (int i = 0; i < mUsers.size(); i++) {
            current_states[i] = "nothing_happend";
        }
        colorGetter = new com.example.chatapp.Login.ColorGetter();
        if (user.getTag() == null) {
            user.setTag("helper");
        }
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();


        holder.username.setText(user.getUsername());
        friendid = user.getId();

        if (!(user.getImageURL().equals("https://firebasestorage.googleapis.com/v0/b/chatapp-2d7a9.appspot.com/o/images%2Fuser.png?alt=media&token=0628b0d7-2537-42c4-8eb6-c7015965f1fd"))) {
            Picasso.with(mContext.getApplicationContext()).load(user.getImageURL().toString()).resize(160, 160).into(holder.profile_image);
        }//Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.friendprofile.class);
                intent.putExtra("userprofileid", user.getId());
                mContext.startActivity(intent);
            }
        });


        mreference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext.getApplicationContext(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    usernames = task.getResult().child("username").getValue().toString();
                    imageurls = task.getResult().child("imageURL").getValue().toString();
                    tag = task.getResult().child("tag").getValue().toString();


                }
            }
        });
        blockref.child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //  current_state[holder.getAdapterPosition()] = "friend";

                    holder.say_hi_view.setText("Blocked");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    //  holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));

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

                    holder.say_hi_view.setText("Blocked");
                    holder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    //   holder.btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));

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
                        // current_states[holder.getAdapterPosition()] = "he_sent_pending";

                        holder.say_hi_view.setText("pending");
                        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
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
        friendreference.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // current_states[holder.getAdapterPosition()] = "friend";
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
                    // current_states[holder.getAdapterPosition()] = "friend";

                    holder.say_hi_view.setText("connected");
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

        reqreference.child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("pending")) {
                        try {
                            current_states[holder.getAdapterPosition()] = "I_sent_pending";
                        } catch (Exception e) {

                        }
                        holder.say_hi_view.setText("Requested");
                    }
                    if (snapshot.child("status").getValue().toString().equals("decline")) {
                        current_states[holder.getAdapterPosition()] = "I_sent_decline";
                        holder.say_hi_view.setText("Requested");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (holder.say_hi_view.getText().toString().equals("accept")) {
            current_states[holder.getAdapterPosition()] = "he_sent_pending";
        }
        if (holder.say_hi_view.getText().toString().equals("connected")) {
            current_states[holder.getAdapterPosition()] = "friend";
        }
        for (int i = 0; i < mUsers.size(); i++) {
            if (holder.say_hi_view.getText().toString().equals("requested")) {
                current_states[i] = "I_sent_pending";
            }
        }

        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Toast.makeText(mContext.getApplicationContext(),""+current_states,Toast.LENGTH_SHORT).show();
                reqreference.child(user.getId()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                            current_states[holder.getAdapterPosition()] = "nothing_happend";
                            say_hi_view.setText("connect+");
                            //btn_follow.setBackground(mContext.getDrawable(drawable.mybutton));
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

                if (current_states[row_index].equals("nothing_happend")) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "pending");
                    hashMap.put("username", usernames);
                    hashMap.put("imageURL", imageurls);
                    hashMap.put("id", user.getId());
                    hashMap.put("tag", tag);
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
                                        removeAt(holder.getAdapterPosition());
                                        //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                        current_states[row_index] = "I_sent_pending";
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

                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_states[row_index].equals("I_sent_pending") || current_states.equals("I_sent_decline")) {

                    reqreference.child(user.getId()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext.getApplicationContext(), "You have cancelled Friend Request", Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < mUsers.size(); i++) {
                                    if (i == row_index) {
                                        holder.say_hi_view.setText("Say Hi...");
                                        current_states[row_index] = "nothing_happend";

                                        //  say_hi_view.setBackground(mContext.getDrawable(drawable.bluebutton));
                                    }
                                }


                            } else {
                                Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if (current_states[row_index].equals("he_sent_pending")) {
                    Toast.makeText(mContext.getApplicationContext(), "User already sent request", Toast.LENGTH_SHORT).show();
                }
                if (current_states.equals("friend")) {
                    //
                }
            }
        });
        checkuserExistance(user.getId());
    }

    private void checkuserExistance(String id) {

        if (current_states.equals("nothing_happend")) {
            current_states[row_index] = "nothing_happend";

            say_hi_view.setText("Say Hi...");
            btn_decline.setVisibility(View.GONE);
        }
        if (current_states.equals("friend")) {
            say_hi_view.setText("connected");
            btn_decline.setVisibility(View.GONE);
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
        public Button btn_decline;
        public TextView txttag;
        public TextView say_hi_view;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(id.username_userfrag);
            profile_image = itemView.findViewById(id.image_user_userfrag);
            txttag = itemView.findViewById(id.txttagonlayouts);
            btn_follow = itemView.findViewById(id.say_hi_btn);
            btn_decline = itemView.findViewById(id.ignore_chat);
            say_hi_view = itemView.findViewById(id.say_hi_user);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    //check for last message

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.app_symbol, message + " from " + username, "Chat Request",
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
        mUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUsers.size());
    }


}
