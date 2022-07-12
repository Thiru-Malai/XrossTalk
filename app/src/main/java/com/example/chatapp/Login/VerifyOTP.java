package com.example.chatapp.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class VerifyOTP extends AppCompatActivity {

    private String code, email;
    private EditText OTP_field;
    private Button btnVerifyOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        email = intent.getStringExtra("email");

        OTP_field = findViewById(R.id.txtOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        ImageView prev = findViewById(R.id.votpback);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyOTP.this, Selection.class));
            }
        });

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entered_otp = OTP_field.getText().toString();

                if (entered_otp.equals(code)) {
                    Toast.makeText(VerifyOTP.this, "Verification Successful", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(VerifyOTP.this, Register.class);
                    i.putExtra("email", email);
                    startActivity(i);
                } else {
                    Toast.makeText(VerifyOTP.this, "Incorrect otp", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}