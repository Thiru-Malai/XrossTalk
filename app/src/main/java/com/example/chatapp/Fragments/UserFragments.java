package com.example.chatapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp.Adapters.OnItemClick;
import com.example.chatapp.Adapters.SearchAdapter;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Adapters.chatAdapter;
import com.example.chatapp.ChatRequest;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.example.chatapp.ViewpageAdapter;
import com.example.chatapp.chatReqAdapter;
import com.example.chatapp.currentmood.angry;
import com.example.chatapp.currentmood.attached;
import com.example.chatapp.currentmood.broken;
import com.example.chatapp.currentmood.confused;
import com.example.chatapp.currentmood.excited;
import com.example.chatapp.currentmood.happy;
import com.example.chatapp.currentmood.high;
import com.example.chatapp.currentmood.normal;
import com.example.chatapp.currentmood.romantic;
import com.example.chatapp.currentmood.sad;
import com.example.chatapp.messageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserFragments extends Fragment {

    private RecyclerView recyclerView;

    FrameLayout frameLayout;


    private UserAdapter userAdapter;
    private SearchAdapter Searchadapter;
    private List<Users> mUsers;
    static OnItemClick onItemClick;
    ImageView moodexplorer;
    public String moods;

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

        View view = inflater.inflate(R.layout.fragment_users, container, false);
        TabLayout tabLayout = view.findViewById(R.id.moodselection);
        ViewPager viewpager = view.findViewById(R.id.viewpagermood);
        moodexplorer = view.findViewById(R.id.moods_explorer);
        viewpager.setOffscreenPageLimit(9);
        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        TabLayout tabLayoutmain = view.findViewById(R.id.tablayout);

        moodexplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), moodexplorer);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getActivity(), "" + item.getTitle() + " mood is setted!!", Toast.LENGTH_SHORT).show();
                        mreference.child("Users").child(firebaseUser.getUid()).child("mood").setValue(item.getTitle().toString().toLowerCase());
                        return true;
                    }
                });


                popup.show();
            }
        });


        ViewpageAdapter viewpageAdapter = new ViewpageAdapter(getActivity().getSupportFragmentManager());
        viewpageAdapter.addFragements(new normal(), "normal");
        viewpageAdapter.addFragements(new happy(), "happy");
        viewpageAdapter.addFragements(new romantic(), "romantic");
        viewpageAdapter.addFragements(new broken(), "broken");
        viewpageAdapter.addFragements(new excited(), "excited");
        viewpageAdapter.addFragements(new high(), "high");
        viewpageAdapter.addFragements(new sad(), "sad");
        viewpageAdapter.addFragements(new attached(), "attached");
        viewpageAdapter.addFragements(new confused(), "confused");
        viewpageAdapter.addFragements(new angry(), "angry");
        viewpager.setAdapter(viewpageAdapter);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                getActivity().finish();
//                startActivity(getActivity().getIntent());

            }

        });
        //  tabLayout.getTabAt(0).get


        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        mUsers = new ArrayList<>();


        search_users = view.findViewById(R.id.search_users);


        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                viewpager.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerView.setVisibility(View.VISIBLE);
                viewpager.setVisibility(View.GONE);
                if (charSequence.toString().toLowerCase().trim().length() == 0) {
                    viewpager.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                    //   getActivity().finish();

                    //startActivity(getActivity().getIntent());


                }

                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // viewpager.setVisibility(View.VISIBLE);
                //     recyclerView.setVisibility(View.GONE);
//               readUsers();
//                viewpager.setAdapter(viewpageAdapter);
//                tabLayout.setupWithViewPager(viewpager);
                if (editable.toString().length() == 0) {
                    viewpager.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    viewpager.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }


            }
        });

        return view;
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getId().equals(fuser.getUid())) {
                        mUsers.add(user);
                    }
                }

                //userAdapter = new UserAdapter(getContext(),onItemClick, mUsers, false);
                Searchadapter = new SearchAdapter(getContext(), onItemClick, mUsers, false);
                recyclerView.setAdapter(Searchadapter);
                //  recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Users user = snapshot.getValue(Users.class);

                        if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }

//                    if(mUsers.size()==0){
//                        frameLayout.setVisibility(View.VISIBLE);
//                    }

                    chatReqAdapter adapter = new chatReqAdapter(getContext(), onItemClick, mUsers, false);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
