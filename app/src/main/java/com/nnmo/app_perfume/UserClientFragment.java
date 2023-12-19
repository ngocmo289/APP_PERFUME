package com.nnmo.app_perfume;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserClientFragment extends Fragment {
    Button btn_signout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_client, container, false);

        btn_signout = view.findViewById(R.id.btn_signout);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),login.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}