package com.example.chatapp.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendOTP extends AppCompatActivity {

    private EditText email_field;
    private Button btnSendOTP;

    private String from_email;
    private String from_email_password;
    private String code;
    private String email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        email_field = findViewById(R.id.txtEmail);
        btnSendOTP = findViewById(R.id.btnSendOTP);

        from_email = "noreplyxrosstalk@gmail.com";
        from_email_password = "rimfdlzxybgdqvvr";
        auth = FirebaseAuth.getInstance();
        ImageView prev = findViewById(R.id.otpback);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOTP.this, Selection.class));
                finish();
            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_field.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    email_field.setError("Required");
                }
                try {


                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
//                                        ProgressDialog progressDialog
//                                                = new ProgressDialog(SendOTP.this);
//                                        progressDialog.setTitle("Sending Email");
//                                        progressDialog.show();
                                        Toast.makeText(SendOTP.this, "Sending Email , Please wait", Toast.LENGTH_SHORT).show();
                                        Log.e("TAG", "Is New User!");
                                        Random random = new Random();

                                        int temp_code = random.nextInt(8999) + 1000;

                                        code = Integer.toString(temp_code);

                                        String message_to_send = "Your otp is " + code;
                                        Properties props = new Properties();
                                        props.put("mail.smtp.auth", "true");
                                        props.put("mail.smtp.starttls.enable", "true");
                                        props.put("mail.smtp.host", "smtp.gmail.com");
                                        props.put("mail.smtp.port", "587");
                                        props.put("mail.imap.ssl.enable","true");
                                        props.put("mail.imap.auth.mechanisms","XOAUTH2");


                                        Session session = Session.getInstance(props,
                                                new Authenticator() {
                                                    @Override
                                                    protected PasswordAuthentication getPasswordAuthentication() {
                                                        return new PasswordAuthentication(from_email, from_email_password);
                                                    }
                                                });
                                        try {
                                            Message message = new MimeMessage(session);
                                            message.setFrom(new InternetAddress(from_email));
                                            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email_field.getText().toString()));
                                            message.setSubject("OTP VERIFICATION");
                                            message.setText(message_to_send);
                                            Transport.send(message);
                                            Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(SendOTP.this, com.example.chatapp.Login.VerifyOTP.class);
                                            i.putExtra("email", email);
                                            i.putExtra("code", code);
                                            startActivity(i);
                                            finish();
                                        } catch (MessagingException e) {
                                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            Toast.makeText(SendOTP.this, "Sorry there is a problem in this Email \nPlease Try another valid email to Register", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.e("Message", e.getMessage());
                                        }
                                    } else {
                                        Log.e("TAG", "Is Old User!");
                                        Toast.makeText(getApplicationContext(), "User Already available please sign in", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(SendOTP.this, "Enter the valid email", Toast.LENGTH_SHORT).show();
                }


                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }


            }


        });
    }
}