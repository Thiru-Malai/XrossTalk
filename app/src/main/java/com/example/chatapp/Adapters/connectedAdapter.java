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

public class connectedAdapter extends RecyclerView.Adapter<connectedAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private List<Requests> mrequests;
    DatabaseReference reqreference, friendreference;
    Button btn_follow, btn_decline;
    private boolean ischat;
    private OnItemClick onItemClick;
    DatabaseReference mreference;
    private FirebaseUser firebaseUser;
    String usernames, imageurls;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    TextView txttag;
    private String tagColor;
    int row_index = -1;
    public String current_state = "friend";


    public connectedAdapter(Context mContext, OnItemClick onItemClick, List<Users> mUsers, boolean ischat) {
        this.onItemClick = onItemClick;
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(layout.layoutofusers, parent, false);
        friendreference = FirebaseDatabase.getInstance().getReference().child("Connections");
        mreference = FirebaseDatabase.getInstance().getReference();
        btn_follow = view.findViewById(id.follow);
        btn_decline = view.findViewById(id.decline);
        txttag = view.findViewById(id.txttagonlayouts);
        return new connectedAdapter.ViewHolder(view);
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

//        if (user.getImageURL().equals("default")){
//            holder.profile_image.setImageResource(drawable.user);
//        } else {
        Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        // }
        holder.btn_follow.setText("Connected");

        colorGetter = new com.example.chatapp.Login.ColorGetter();
        holder.txttag.setText(user.getTag());
        tagColor = colorGetter.getColor(user.getTag());
        setColor();
        holder.txttag.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, com.example.chatapp.friendprofile1.class);
                intent.putExtra("userprofileid", user.getFrid());
                mContext.startActivity(intent);
            }
        });


        holder.btn_follow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                row_index = holder.getAdapterPosition();
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you want to Unconnect ?");
                alertbox.setTitle("Alert");

                alertbox.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        friendreference.child(firebaseUser.getUid()).child(user.getFrid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    friendreference.child(user.getFrid()).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                for (int i = 0; i < mUsers.size(); i++) {
                                                    if (i == row_index) {
                                                        Toast.makeText(mContext.getApplicationContext(), "UnConnected", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        //   Toast.makeText(mContext.getApplicationContext(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
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
