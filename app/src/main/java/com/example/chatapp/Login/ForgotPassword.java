package com.example.chatapp.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email_field;
    private Button btnSend;
    private String email;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email_field = findViewById(R.id.txtRecoveryEmail);
        btnSend = findViewById(R.id.btnSend);

        auth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_field.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Password recovery details have been sent to your email", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPassword.this, Login.class));
                        } else {
                            Toast.makeText(ForgotPassword.this, "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}