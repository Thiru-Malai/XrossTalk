package com.example.chatapp.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Adapters.chatNotificationAdapter;
import com.example.chatapp.Model.Users;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Register extends AppCompatActivity {

    private Button tag_btn[] = new Button[9];
    private Button create_account;
    private EditText username_field, password_field;
    private String tag_value = "default", email, username, password;
    int[] btn_id = {R.id.tag1, R.id.tag2, R.id.tag3, R.id.tag4, R.id.tag5, R.id.tag6, R.id.tag7, R.id.tag8, R.id.tag9};
    private FirebaseAuth mAuth;
    private String default_image_url;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private String userid;
    private ImageView tagsinf;
    private TextView selected_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();

        username_field = findViewById(R.id.txtUsername);
        create_account = findViewById(R.id.btnCreateAccount);
        password_field = findViewById(R.id.txtPassword);
        tagsinf = findViewById(R.id.tagsinfo);
        default_image_url = "https://firebasestorage.googleapis.com/v0/b/chatapp-2d7a9.appspot.com/o/images%2Fuser.png?alt=media&token=0628b0d7-2537-42c4-8eb6-c7015965f1fd";
        selected_tag = findViewById(R.id.selectedtag);
        reference = FirebaseDatabase.getInstance().getReference();

        email = intent.getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();

        for (int i = 0; i < btn_id.length; i++) {
            tag_btn[i] = (Button) findViewById(btn_id[i]);
        }
        tagsinf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, com.example.chatapp.Login.Tags1.class));
            }
        });


        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_field.getText().toString();
                password = password_field.getText().toString();
                Query query1 = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search");

                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Users req = snapshot.getValue(Users.class);

                            if (req.getUsername().toLowerCase().equals(username_field.getText().toString().toLowerCase())) {
                                // if(firebaseUser.getUid() !=null && req.getFrid() !=null && req.getId() !=null && firebaseUser.getUid() != req.getid)
                                Toast.makeText(Register.this, "User  Exists", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                        if (TextUtils.isEmpty(username)) {
                            username_field.setError("Required");

                        } else if (TextUtils.isEmpty(password)) {
                            password_field.setError("Required");

                        } else if (password.length() < 6) {

                            password_field.setError("Length Must Be 6 or more");
                        } else if (username.length() > 15) {
                            username_field.setError("Name is too large");
                        } else if (tag_value.equals("default")) {
                            Toast.makeText(Register.this, "Please select a tag value", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser(username, password, email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }


    public void change_tag_value() {
        selected_tag.setText(tag_value);
    }

    public void setColor(Button button) {

        tag_value = button.getText().toString();

        button.setBackgroundColor(Color.parseColor("#373737"));
        for (int i = 0; i < btn_id.length; i++) {
            if (button != tag_btn[i])
                tag_btn[i].setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    public void ButtononClick(View v) {
        switch (v.getId()) {
            case R.id.tag1:
                setColor(tag_btn[0]);
                break;

            case R.id.tag2:
                setColor(tag_btn[1]);
                break;

            case R.id.tag3:
                setColor(tag_btn[2]);
                break;

            case R.id.tag4:
                setColor(tag_btn[3]);
                break;

            case R.id.tag5:
                setColor(tag_btn[4]);
                break;

            case R.id.tag6:
                setColor(tag_btn[5]);
                break;

            case R.id.tag7:
                setColor(tag_btn[6]);
                break;

            case R.id.tag8:
                setColor(tag_btn[7]);
                break;

            case R.id.tag9:
                setColor(tag_btn[8]);
                break;

        }
    }

    private void registerUser(final String username, String password, final String email) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            userid = user.getUid();
                            Toast.makeText(Register.this, "User account created successfully", Toast.LENGTH_LONG).show();
                            addUser();
                            startActivity(new Intent(Register.this, AddImage.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Register.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addUser() {
        AddUser newuser = new AddUser(email, username, tag_value, "Hello", default_image_url, userid, username.toLowerCase(), "offline", "normal");
        reference.child("Users").child(userid).setValue(newuser);

    }
}