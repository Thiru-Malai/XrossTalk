package com.example.chatapp.Login;

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
import com.example.chatapp.Model.Users;
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
import java.util.List;

public class Profile extends AppCompatActivity {

    private ImageView imgProfile, btnEdit;
    private TextView txtUsername, txtAbout, txtPopularity, txtConnections, txtTag;
    private String userid;
    private FirebaseAuth auth;
    private com.example.chatapp.Login.ColorGetter colorGetter;
    private String tagColor;
    RelativeLayout connections;
    private List<Users> mUsers;


    private FirebaseUser loggedUser;
    private FirebaseDatabase mdb;
    private DatabaseReference mreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        ImageView prev = findViewById(R.id.btnPrev);
        imgProfile = findViewById(R.id.imgProfile);
        btnEdit = findViewById(R.id.btnEdit);
        txtUsername = findViewById(R.id.txtUsername);
        txtTag = findViewById(R.id.txtTagname);
        txtAbout = findViewById(R.id.txtAbout);
        txtConnections = findViewById(R.id.txtConnections);
        txtPopularity = findViewById(R.id.pops);
        connections = findViewById(R.id.connectionView);

        colorGetter = new com.example.chatapp.Login.ColorGetter();
        mdb = FirebaseDatabase.getInstance();
        mreference = mdb.getReference();
        mUsers = new ArrayList<>();

        loggedUser = auth.getCurrentUser();
        userid = loggedUser.getUid();


        mreference.child("Users").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(Profile.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
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
                    Picasso.with(Profile.this).load(image).resize(160, 160).into(imgProfile);

                }
            }
        });
        Query query1 = FirebaseDatabase.getInstance().getReference("Connections").child(userid).orderByChild("search");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users req = snapshot.getValue(Users.class);

                    assert req != null;
                    assert userid != null;

                    if (req.getId().equals(userid)) {
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
                Integer s = new Integer(size);
                txtConnections.setText(s.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, com.example.chatapp.MainActivity.class));
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, com.example.chatapp.Login.EditProfile.class));
                finish();
            }
        });
        connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, ConnectionView.class));
                finish();
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
}