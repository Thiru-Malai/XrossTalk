package com.example.chatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chatapp.Login.Login;
import com.example.chatapp.Login.Register;
import com.example.chatapp.R;


public class selection extends Fragment {
    Button login, signup;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selection, container, false);
        login = (Button) rootView.findViewById(R.id.signupb);
        signup = (Button) rootView.findViewById(R.id.registerb);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Register.class));
            }
        });
        return rootView;


    }
}

