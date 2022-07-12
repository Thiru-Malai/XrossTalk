package com.example.chatapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.BlockedValueAdapter;
import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Adapters.SearchAdapter;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Model.Requests;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
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


public class Blocked extends Fragment {

    static OnItemClick onItemClick;
    FrameLayout frameLayout;
    ImageView back;
    EditText search_users;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private SearchAdapter Searchadapter;
    private List<Users> mUsers;
    private List<Requests> mRequests;

    public static Connections newInstance(OnItemClick click) {
        onItemClick = click;
        Bundle args = new Bundle();

        Connections fragment = new Connections();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_blocked, container, false);
        back = view.findViewById(R.id.block_back);

        recyclerView = view.findViewById(R.id.blockedRecyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.chatapp.MainActivity.class);
                startActivity(intent);

            }
        });

        mUsers = new ArrayList<>();
        searchUsers();

        return view;
    }

    private void searchUsers() {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        Query query1 = FirebaseDatabase.getInstance().getReference("Blocked").child(fuser.getUid()).orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);

                    assert req != null;
                    assert fuser != null;

                    if (!req.getId().equals(fuser.getUid())) {
                        mUsers.add(req);
                    }

                }

                //userAdapter = new UserAdapter(getContext(),onItemClick, mUsers, false);

                BlockedValueAdapter adapter = new BlockedValueAdapter(getContext(), onItemClick, mUsers, false);
                recyclerView.setAdapter(adapter);
                //  recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
