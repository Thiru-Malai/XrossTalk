package com.example.chatapp.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class ProfileFragments extends Fragment {
    RelativeLayout logout;
    RelativeLayout mprofile;
    RelativeLayout mblocked;
    RelativeLayout mtags;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.activity_settings, container, false);
        logout = (RelativeLayout) rootview.findViewById(R.id.logoutmenu);
        mprofile = (RelativeLayout) rootview.findViewById(R.id.profile);
        mblocked = (RelativeLayout) rootview.findViewById(R.id.blocked);
        mtags = (RelativeLayout) rootview.findViewById(R.id.tag_settings);
        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), com.example.chatapp.Login.Profile.class);
                startActivity(i);
            }
        });
        mtags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), com.example.chatapp.
                        Tags.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog ad = new AlertDialog.Builder(getActivity())
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Logout");
                ad.setMessage("Do you want to logout?");
                ad.setButton(AlertDialog.BUTTON_POSITIVE, "yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(getActivity(), com.example.chatapp.Login.Intro.class);
                        startActivity(i);
                    }
                });
                ad.setButton(AlertDialog.BUTTON_NEGATIVE, "no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.show();

            }
        });

        mblocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), com.example.chatapp.BlockView.class);
                startActivity(in);
            }
        });

        return rootview;
    }


}