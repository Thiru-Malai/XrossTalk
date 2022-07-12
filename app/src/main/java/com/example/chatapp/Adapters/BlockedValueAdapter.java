package com.example.chatapp.Adapters;

import static com.example.chatapp.R.*;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.Requests;
import com.example.chatapp.Model.Users;
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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BlockedValueAdapter extends RecyclerView.Adapter<BlockedValueAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private List<Requests> mrequests;
    DatabaseReference reqreference, friendreference, connectionreference, reference, reference2;
    Button btn_follow, btn_decline;
    private boolean ischat;
    private OnItemClick onItemClick;
    DatabaseReference mreference;
    private FirebaseUser firebaseUser;
    int row_index = -1;
    public String current_state = "friend";
    private com.example.chatapp.Login.ColorGetter colorGetter;
    TextView txttag;
    private String tagColor;
    public String c_usernames;
    public String c_tag;
    public String c_imageurls;
    public String d_usernames;
    public String d_tag;
    public String d_imageurls;


    public BlockedValueAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.layoutofusers, parent, false);
        friendreference = FirebaseDatabase.getInstance().getReference().child("Blocked");
        connectionreference = FirebaseDatabase.getInstance().getReference().child("Connections");
        reference2 = FirebaseDatabase.getInstance().getReference();
        reqreference = FirebaseDatabase.getInstance().getReference().child("Chatslist");
        reference = FirebaseDatabase.getInstance().getReference();
        btn_follow = view.findViewById(id.follow);
        txttag = view.findViewById(id.txttagonlayouts);
        btn_decline = view.findViewById(id.decline);
        return new BlockedValueAdapter.ViewHolder(view);
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
        btn_decline.setVisibility(View.GONE);

        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        holder.btn_follow.setText("UnBlock");

        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();
        reference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext.getApplicationContext(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    c_usernames = task.getResult().child("username").getValue().toString();
                    c_tag = task.getResult().child("tag").getValue().toString();
                    c_imageurls = task.getResult().child("imageURL").getValue().toString();


                }
            }
        });
        reference2.child("Users").child(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext.getApplicationContext(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    d_usernames = task.getResult().child("username").getValue().toString();
                    d_tag = task.getResult().child("tag").getValue().toString();
                    d_imageurls = task.getResult().child("imageURL").getValue().toString();


                }
            }
        });
        HashMap hashMap = new HashMap();
        hashMap.put("tag", d_tag);
        hashMap.put("id", firebaseUser.getUid());
        // hashMap.put("search",d_usernames.toLowerCase());
        hashMap.put("status", "friend");
        hashMap.put("username", d_usernames);
        hashMap.put("imageURL", d_imageurls);
        hashMap.put("frid", user.getId());

        HashMap hashMap2 = new HashMap();
        hashMap2.put("tag", c_tag);
        hashMap2.put("id", user.getId());
        hashMap2.put("search", user.getUsername().toLowerCase());
        hashMap2.put("status", "friend");

        hashMap2.put("username", c_usernames);
        hashMap2.put("imageURL", c_imageurls);
        hashMap2.put("frid", firebaseUser.getUid());


        holder.btn_follow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //  Toast.makeText(mContext.getApplicationContext(),""+holder.username,Toast.LENGTH_SHORT).show();
                row_index = holder.getAdapterPosition();
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you want to UnBlock ?");
                alertbox.setTitle("Alert");

                alertbox.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        friendreference.child(firebaseUser.getUid()).child(user.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    friendreference.child(user.getId()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                if (task.isSuccessful()) {
                                                    HashMap hashMaps = new HashMap();
                                                    hashMaps.put("id", user.getId());
                                                    HashMap hashmap2 = new HashMap();
                                                    hashmap2.put("id", user.getFrid());
                                                    reqreference.child(user.getId()).child(user.getFrid()).updateChildren(hashmap2).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                reqreference.child(user.getFrid()).child(user.getId()).updateChildren(hashMaps).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
//                                                                                    connectionreference.child(user.getId()).child(user.getFrid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
//                                                                                        @Override
//                                                                                        public void onComplete(@NonNull Task task) {
//                                                                                            connectionreference.child(user.getFrid()).child(user.getId()).updateChildren(hashmap2).addOnCompleteListener(new OnCompleteListener() {
//                                                                                                @Override
//                                                                                                public void onComplete(@NonNull Task task) {
                                                                            Toast.makeText(mContext.getApplicationContext(), "UnBlocked", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }//);
                                                                    //                                                                                 }
                                                                });
//                                                                               }
//
//                                                                            }
//                                                                        });
                                                            }
                                                        }
                                                    });
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }


                });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertbox.show();

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
        public TextView txttag;
        public Button btn_decline;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(id.username_userfrag);
            profile_image = itemView.findViewById(id.image_user_userfrag);
            btn_follow = itemView.findViewById(id.follow);
            btn_decline = itemView.findViewById(id.decline);
            txttag = itemView.findViewById(id.txttagonlayouts);

        }
    }

    private void setColor() {
        txttag.setTextColor(Color.parseColor(tagColor));
        GradientDrawable myGrad = (GradientDrawable) txttag.getBackground();
        myGrad.setStroke(convertDpToPx(2), Color.parseColor(tagColor));
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    //check for last message


}
