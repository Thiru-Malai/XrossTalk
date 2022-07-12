package com.example.chatapp.currentmood;


import static com.firebase.ui.auth.AuthUI.TAG;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Adapters.SearchAdapter;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.example.chatapp.chatReqAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class excited extends Fragment {

    private RecyclerView recyclerView;

    FrameLayout frameLayout;


    private UserAdapter userAdapter;
    private SearchAdapter Searchadapter;
    private List<Users> mUsers;
    static OnItemClick onItemClick;

    EditText search_users;

//    public static UserFragments newInstance(OnItemClick click) {
//        onItemClick = click;
//        Bundle args = new Bundle();
//
//        UserFragments fragment = new UserFragments();
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mood_fragment, container, false);


        recyclerView = view.findViewById(R.id.sadrecyclerview);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        mUsers = new ArrayList<>();


        readUsers();


        return view;
    }


    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        if (user.getMood() == null) {
                            user.setMood("normal");
                        }
                        if (user.getMood().equals("excited")) {
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.child("ChatConnections").child(firebaseUser.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        //create new user
                                        FirebaseDatabase.getInstance().getReference().child("ChatRequests").child(user.getId()).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.exists()) {
                                                    if (snapshot1.child("status").getValue().toString().equals("pending")) {

                                                    } else {

                                                    }


                                                } else {
                                                    mUsers.add(user);
                                                    chatReqAdapter adapter = new chatReqAdapter(getContext(), onItemClick, mUsers, false);
                                                    recyclerView.setAdapter(adapter);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }


//                    if(mUsers.size()==0){
//                        frameLayout.setVisibility(View.VISIBLE);
//                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
