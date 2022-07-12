package com.example.chatapp;

import android.graphics.Typeface;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Adapters.SearchAdapter;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.Adapters.chatNotificationAdapter;
import com.example.chatapp.Adapters.connectionReqAdapter;
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


public class ChatRequest extends Fragment {

    private RecyclerView recyclerView;

    FrameLayout frameLayout;


    private UserAdapter userAdapter;
    private SearchAdapter Searchadapter;
    private List<Users> mUsers;
    private List<Requests> mRequests;
    static OnItemClick onItemClick;

    EditText search_users;

    public static ChatRequest newInstance(OnItemClick click) {
        onItemClick = click;
        Bundle args = new Bundle();

        ChatRequest fragment = new ChatRequest();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_request, container, false);


        recyclerView = view.findViewById(R.id.recyclerView_chat_requests);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mUsers = new ArrayList<>();
        searchUsers();

        return view;
    }

    private void searchUsers() {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query1 = FirebaseDatabase.getInstance().getReference("ChatRequests").child(fuser.getUid()).orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);

                    assert req != null;
                    assert fuser != null;

                    if (req.getId().equals(fuser.getUid())) {
                        mUsers.add(req);
                    }

                }

                //userAdapter = new UserAdapter(getContext(),onItemClick, mUsers, false);

                chatNotificationAdapter adapter = new chatNotificationAdapter(getContext(), onItemClick, mUsers, false);
                recyclerView.setAdapter(adapter);
                //  recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
