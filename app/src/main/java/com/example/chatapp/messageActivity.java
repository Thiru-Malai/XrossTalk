package com.example.chatapp;


import static com.firebase.ui.auth.AuthUI.TAG;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.chatapp.Adapters.MessageAdapter;

import com.example.chatapp.Adapters.fullmsgadapter;

import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Login.Profile;
import com.example.chatapp.Model.Chats;
import com.example.chatapp.Model.Users;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class messageActivity extends AppCompatActivity {


    String friendid, message, myid;
    ImageView imageViewOnToolbar;
    TextView usernameonToolbar;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    ProgressDialog progressdialog;

    EditText et_message;
    Handler handler = new Handler();
    ImageButton send;
    ImageView moodsetterBtn;

    DatabaseReference reference;

    List<Chats> chatsList;
    MessageAdapter messageAdapter;

    RecyclerView recyclerView;
    ValueEventListener seenlistener;
    APIService apiService;
    RelativeLayout leftchats;
    ImageView prev;
    Boolean notify = false;
    public boolean blocked = false;
    public String names, tag, imageurls;
    public String user_tag, user_name, user_imageurls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // receivermessage = findViewById(R.id.show_messagel);
        imageViewOnToolbar = findViewById(R.id.profile_image_toolbar_message);
        usernameonToolbar = findViewById(R.id.username_ontoolbar_message);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView = findViewById(R.id.recyclerview_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        send = findViewById(R.id.send_messsage_btn);
        et_message = findViewById(R.id.edit_message_text);
        moodsetterBtn = findViewById(R.id.moodsetter);
        leftchats = findViewById(R.id.leftchat);
        prev = findViewById(R.id.btnPrev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myid = firebaseUser.getUid(); // my id or the one who is loggedin
        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference();
        mreference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(messageActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                } else {

                    user_name = task.getResult().child("username").getValue().toString();
                    user_tag = task.getResult().child("tag").getValue().toString();
                    user_imageurls = task.getResult().child("imageURL").getValue().toString();


                }
            }
        });
        friendid = getIntent().getStringExtra("friendid"); // retreive the friendid when we click on the item

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(messageActivity.this, friendprofile.class);
                intent.putExtra("userprofileid", friendid);
                startActivity(intent);

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(friendid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);


                usernameonToolbar.setText(users.getUsername()); // set the text of the user on textivew in toolbar

                if (!(users.getImageURL().equals("https://firebasestorage.googleapis.com/v0/b/chatapp-2d7a9.appspot.com/o/images%2Fuser.png?alt=media&token=0628b0d7-2537-42c4-8eb6-c7015965f1fd"))) {
                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageViewOnToolbar);
                }
                readMessages(myid, friendid, users.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference mrefere = FirebaseDatabase.getInstance().getReference();
        moodsetterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(messageActivity.this, moodsetterBtn);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(messageActivity.this, "" + item.getTitle() + " mood is setted!!", Toast.LENGTH_SHORT).show();
                        mrefere.child("Users").child(firebaseUser.getUid()).child("mood").setValue(item.getTitle().toString().toLowerCase());


                        return true;
                    }
                });
                popup.show();
            }
        });


        seenMessage(friendid);


        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().length() > 0 && s.toString().trim() != "") {

                    send.setEnabled(true);

                } else {

                    send.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = et_message.getText().toString();

                if (!text.startsWith(" ")) {
                    et_message.getText().insert(0, " ");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;
                message = et_message.getText().toString();
                if (message.trim().equals("")) {
                    Toast.makeText(messageActivity.this, "You cannot send the empty message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(myid, friendid, message.trim());
                }


                et_message.setText("");


            }
        });


    }


    private void seenMessage(final String friendid) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");


        seenlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getReciever() != null && myid != null && chats.getSender() != null && friendid != null) {
                        if (chats.getReciever().equals(myid) && chats.getSender().equals(friendid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            ds.getRef().updateChildren(hashMap);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessages(final String myid, final String friendid, final String imageURL) {

        chatsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getReciever() != null && myid != null && chats.getSender() != null && friendid != null) {
                        if (chats.getSender().equals(myid) && chats.getReciever().equals(friendid) ||
                                chats.getSender().equals(friendid) && chats.getReciever().equals(myid)) {

                            chatsList.add(chats);
                        }

                        fullmsgadapter messageAdapter = new fullmsgadapter(messageActivity.this, chatsList, imageURL, friendid);
                        recyclerView.setAdapter(messageAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendMessage(final String myid, final String friendid, final String message) {


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yy hh:mm aa");
        System.out.println(sdf.format(cal.getTime()));

        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference();
        mreference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(messageActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
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
                    hashMap.put("time", sdf1.format(cal.getTime()));
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
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdfs = new SimpleDateFormat("dd-MM-yy hh:mm aa", Locale.ENGLISH);
                    try {
                        cal.setTime(sdfs.parse("dd-MM-yy hh:mm aa"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Chatslist");
                    ref1.child(myid).child(friendid).child("time").setValue(fieldToTimestamp(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)));
                    ref1.child(friendid).child(myid).child("time").setValue(fieldToTimestamp(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)));
                    final String msg = message;
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);
                            sendNotification(friendid, user.getUsername(), msg);
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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
                    Data data = new Data(firebaseUser.getUid(), R.drawable.app_symbol, username + ": " + message, "New Message",
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


    private void Status(final String status) {


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete: {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you want to delete this chat? \nAll the chat will be cleared for both");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


                                CreateProgressDialog();


                                deleteMsg();
                                deleteFriendMsg();
                                ShowProgressDialog();
//
//                                try {
//                                    progressDialog.show();
//                                    Thread.sleep(4000);
//                                    Toast.makeText(messageActivity.this, "Message deleted", Toast.LENGTH_LONG).show();
//                                   // progressDialog.dismiss();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }


                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            }

            case R.id.block: {

                AlertDialog.Builder alertDialogBuild = new AlertDialog.Builder(this);
                alertDialogBuild.setMessage("Are you sure you want to block this chat?");
                alertDialogBuild.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


                                DatabaseReference reqreference = FirebaseDatabase.getInstance().getReference().child("Blocked");
                                DatabaseReference reqreference1 = FirebaseDatabase.getInstance().getReference().child("Chatslist");
                                DatabaseReference reqreference2 = FirebaseDatabase.getInstance().getReference().child("Connections");
                                DatabaseReference mreference = FirebaseDatabase.getInstance().getReference();
                                mreference.child("Users").child(friendid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(messageActivity.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                                        } else {
                                            // String about = task.getResult().child("about").getValue().toString();
                                            String name = task.getResult().child("username").getValue().toString();
                                            String tags = task.getResult().child("tag").getValue().toString();
                                            //  String connections = task.getResult().child("connections").getValue().toString();
                                            String imageurl = task.getResult().child("imageURL").getValue().toString();

                                            HashMap hashMap1 = new HashMap();
                                            hashMap1.put("tag", user_tag);
                                            hashMap1.put("id", firebaseUser.getUid());
                                            hashMap1.put("search", user_name.toLowerCase());
                                            hashMap1.put("status", "friend");
                                            hashMap1.put("username", user_name);
                                            hashMap1.put("imageURL", user_imageurls);
                                            hashMap1.put("frid", friendid);

                                            HashMap hashMap2 = new HashMap();
                                            hashMap2.put("tag", tags);
                                            hashMap2.put("id", friendid);
                                            hashMap2.put("search", name.toString().toLowerCase());
                                            hashMap2.put("status", "friend");

                                            hashMap2.put("username", name);
                                            hashMap2.put("imageURL", imageurl);
                                            hashMap2.put("frid", firebaseUser.getUid());
                                            HashMap hash = new HashMap();
                                            hash.put("id", "abc");
                                            reqreference.child(myid).child(friendid).updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
//                                                                                reqreference2.child(myid).child(friendid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if(task.isSuccessful()){
//                                                                                            reqreference2.child(friendid).child(myid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                @Override
//                                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                                    if(task.isSuccessful()){
                                                        reqreference1.child(myid).child(friendid).updateChildren(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    reqreference1.child(friendid).child(myid).updateChildren(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                             @Override
                                                                                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                 if (task.isSuccessful()) {
                                                                                                                                                                     Toast.makeText(messageActivity.this, "Blocked ", Toast.LENGTH_LONG).show();
                                                                                                                                                                     blocked = true;
                                                                                                                                                                     Intent i = new Intent(messageActivity.this, com.example.chatapp.MainActivity.class);
                                                                                                                                                                     i.putExtra("bl", "yes_blocked");
                                                                                                                                                                     startActivity(i);
                                                                                                                                                                 }


                                                                                                                                                             }
                                                                                                                                                             //   });

                                                                                                                                                             //         }
//                                                                                                }
//                                                                                            });
//


                                                                                                                                                         }
//
//                                                                                                }

                                                                            //);


                                                                    );
                                                                }

                                                                //}
                                                            }
                                                        });

                                                    }
                                                }

                                                //Toast.makeText(messageActivity.this, "Blocked", Toast.LENGTH_LONG).show();

                                            });

                                        }


                                    }
                                });
                            }
                        });


                alertDialogBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuild.create();
                alertDialog.show();
                break;

            }
            case R.id.visitprofile:
                Intent intent = new Intent(messageActivity.this, friendprofile.class);
                intent.putExtra("userprofileid", friendid);
                startActivity(intent);
                break;


        }

        return true;
    }

    //return true;
    @Override
    protected void onResume() {
        super.onResume();
        Status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
        reference.removeEventListener(seenlistener);
    }

    private void deleteMsg() {
        final String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String msgtimestmp = list.get(position).getTimestamp();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Chats");
        Query query = dbref.orderByChild("reciever").equalTo(friendid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("sender").getValue().equals(myuid)) {
                        dataSnapshot1.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFriendMsg() {
        final String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Chats");
        Query query = dbref.orderByChild("reciever").equalTo(myuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    if (dataSnapshot2.child("sender").getValue().equals(friendid)) {
                        dataSnapshot2.getRef().removeValue();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CreateProgressDialog() {

        progressdialog = new ProgressDialog(messageActivity.this);

        progressdialog.setIndeterminate(false);

        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressdialog.setCancelable(true);

        progressdialog.setMax(100);

        progressdialog.show();

    }

    public void ShowProgressDialog() {
        final int[] status = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status[0] < 100) {

                    status[0] += 1;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            progressdialog.setProgress(status[0]);

                            if (status[0] == 100) {

                                progressdialog.dismiss();
                            }
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        if (blocked == true) {
            Intent i = new Intent(messageActivity.this, MainActivity.class);
            i.putExtra("blocked", "block");
            startActivity(i);

        } else {


            int page = 1;
            Intent intent = new Intent(messageActivity.this, MainActivity.class);
            intent.putExtra("One", page);// One is your argument
            startActivity(intent);
        }

    }

    private long fieldToTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return (long) (calendar.getTimeInMillis() / 1000L);
    }


}