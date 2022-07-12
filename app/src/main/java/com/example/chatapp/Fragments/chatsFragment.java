package com.example.chatapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.Login.EditProfile;
import com.example.chatapp.Login.Profile;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.Chatslist;
import com.example.chatapp.Model.Users;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;


public class chatsFragment extends Fragment {

    private RecyclerView recyclerView;


    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private List<Users> mUsers2;
    TextView emptyview;
    ImageView imageView, notification, imagenotify;

    FirebaseUser fuser;
    DatabaseReference reference;
    TextView notifycnt;

    private List<Chatslist> usersList;
    static OnItemClick onItemClick;
    public Boolean isRequestc = true, isRequestr = true;
    int notifycounts;
    LinearLayoutManager mLayoutmanager;
    SharedPreferences mSharedPref;


    public static chatsFragment newInstance(OnItemClick click) {

        onItemClick = click;
        Bundle args = new Bundle();

        chatsFragment fragment = new chatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        notification = view.findViewById(R.id.notification_icon);
        imagenotify = view.findViewById(R.id.MsgunreadNotify);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        notifycnt = view.findViewById(R.id.notifycount);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        mSharedPref = getActivity().getSharedPreferences("Sortsettings", Context.MODE_PRIVATE);
        String mSorting = mSharedPref.getString("sort", "newest");
        if (mSorting.equals("newest")) {
            mLayoutmanager = new LinearLayoutManager(getContext());
            mLayoutmanager.setReverseLayout(true);
            mLayoutmanager.setStackFromEnd(true);

        }


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), com.example.chatapp.notificationActivity.class);
                startActivity(i);
            }
        });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query1 = FirebaseDatabase.getInstance().getReference("ChatRequests").child(firebaseUser.getUid());

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imagenotify.setVisibility(View.VISIBLE);
                    notifycnt.setVisibility(View.VISIBLE);
                    isRequestc = true;

                } else {
                    isRequestc = false;
                    //imagenotify.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2 = FirebaseDatabase.getInstance().getReference("Requests").child(firebaseUser.getUid());

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isRequestr = true;
                    imagenotify.setVisibility(View.VISIBLE);
                    notifycnt.setVisibility(View.VISIBLE);
                } else {
                    isRequestr = false;

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!isRequestc && !isRequestr) {
            imagenotify.setVisibility(View.GONE);
        }


        DatabaseReference notifyreference = FirebaseDatabase.getInstance().getReference("ChatRequests").child(firebaseUser.getUid());

        notifyreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotss) {


                for (DataSnapshot snapshots : dataSnapshotss.getChildren()) {
                    Users users = snapshots.getValue(Users.class);
                    if (firebaseUser.getUid() != null && users.getId() != null)

                        notifycounts++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference notifyreference1 = FirebaseDatabase.getInstance().getReference("Requests").child(firebaseUser.getUid());

        notifyreference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotss) {


                for (DataSnapshot snapshots : dataSnapshotss.getChildren()) {
                    Users users = snapshots.getValue(Users.class);
                    if (firebaseUser.getUid() != null && users.getId() != null)

                        notifycounts++;
                }
                notifycnt.setText(Integer.toString(notifycounts));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (notifycounts == 0) {
            imagenotify.setVisibility(View.GONE);
            notifycnt.setVisibility(View.GONE);
        }


        recyclerView = view.findViewById(R.id.chat_recyclerview_chatfrag);

        imageView = view.findViewById(R.id.image_user_userfrag);
        emptyview = view.findViewById(R.id.empty);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutmanager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        };
        recyclerView.addItemDecoration(dividerItemDecoration, 0);
        for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
            if (recyclerView.getItemDecorationAt(i) instanceof DividerItemDecoration)
                recyclerView.removeItemDecorationAt(i);
        }


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatslist").child(firebaseUser.getUid());
        Query q = reference.orderByChild("time").getRef();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    Chatslist chatlist = snapshots.getValue(Chatslist.class);
                    usersList.add(chatlist);
                }
                //  Collections.sort(usersList, Chatslist.timecomparator);

                //  mUsers  = new ArrayList<>();
//                mUsers.clear();
                //   getFragmentManager().beginTransaction().detach(this).attach(this).commit();

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }


    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    public void navigateNotify(View view) {
        Toast.makeText(getActivity().getApplicationContext(), "helllo", Toast.LENGTH_SHORT).show();
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        mUsers2 = new ArrayList<>();
        mUsers2.clear();
        mUsers.clear();


        Collections.sort(usersList, Chatslist.timecomparator);


        for (Chatslist chatlist : usersList) {
            // Collections.sort(Chatslist, new sortItems());
            mUsers.clear();
            reference = FirebaseDatabase.getInstance().getReference("Users");


            reference.addValueEventListener(new ValueEventListener() {


                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Users user = snapshot.getValue(Users.class);
                        if (user.getImageURL() != null) {
                            if (user.getImageURL().equals("default")) {
                                if (imageView != null) {
                                    imageView.setImageResource(R.drawable.user);
                                }
                            }
                        } else {

                            // Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageView);

                        }
                        if (user != null && user.getId() != null && chatlist != null && chatlist.getId() != null &&
                                user.getId().equals(chatlist.getId())) {
                            if (mUsers.size() < usersList.size())
                                mUsers.add(user);
                            else
                                break;
                        }
                        //   mUsers= removeDuplicates(mUsers);


                    }


                    //mUsers.clear();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        chatAdapter chatadapter = new chatAdapter(getContext(), onItemClick, mUsers, true);
        recyclerView.setAdapter(chatadapter);
        if (mUsers.size() == 0) {
            emptyview.setVisibility(View.GONE);
        }


    }

    public static <Users> List<Users> removeDuplicates(List<Users> lists) {

        // Create a new ArrayList
        List<Users> newList = new ArrayList<>();

        // Traverse through the first list
        for (Users element : lists) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }


}
