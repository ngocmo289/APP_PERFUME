package com.nnmo.app_perfume;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserClientFragment extends Fragment {
    Button btn_signout, btn_order_client;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView txt_hello;
    FirebaseFirestore mStore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_client, container, false);

        btn_order_client = view.findViewById(R.id.prf_my_order);
        btn_signout = view.findViewById(R.id.prf_sign_out);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        txt_hello = view.findViewById(R.id.txt_hello);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // Lấy thông tin người dùng hiện tại từ Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference userDocRef = mStore.collection("Users").document(currentUser.getUid());
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Lấy giá trị của trường "name" từ tài liệu Firestore
                            String userName = documentSnapshot.getString("name");

                            // Hiển thị "Xin chào" và tên của người dùng
                            if (userName != null && !userName.isEmpty()) {
                                txt_hello.setText("Hello, " + userName + "!");
                            }
                        }
                    }
                }
            });
        }

        btn_order_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),my_order_client.class);
                startActivity(intent);
            }
        });


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