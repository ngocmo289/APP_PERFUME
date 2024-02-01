package com.nnmo.app_perfume;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class CartClientFragment extends Fragment {
    TextView tv_total;

    Button btn_checkout;
    RecyclerView rccv;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    cart_adapter cart_adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_client, container, false);
        rccv = view.findViewById(R.id.rv);
        tv_total = view.findViewById(R.id.total);
        btn_checkout = view.findViewById(R.id.btn_checkout);
        // Sử dụng giá trị của cartCounter để tạo idCart
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        // Lấy thông tin người dùng hiện tại từ Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Thực hiện truy vấn để lấy dữ liệu từ Firebase
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Cart")
                .child(currentUser.getUid());

        FirebaseRecyclerOptions<cart_item> options =
                new FirebaseRecyclerOptions.Builder<cart_item>()
                        .setQuery(query, cart_item.class)
                        .build();
        // Khởi tạo adapter và gắn nó vào RecyclerView
        cart_adapter = new cart_adapter(options, getContext());
        rccv.setAdapter(cart_adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rccv.setLayoutManager(layoutManager);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;

                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    cart_item cartItem = cartItemSnapshot.getValue(cart_item.class);

                    if (cartItem != null) {
                        // Assuming there is a 'price' field in your cart_item class
                        total += (cartItem.getCount() * cartItem.getPrice());
                    }
                }

                // Set the total to the TextView
                tv_total.setText(String.valueOf(total) + "$");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("CartClientFragment", "Error fetching cart items", error.toException());
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), check_out.class);
                startActivity(intent);
            }
        });
//
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bắt đầu lắng nghe sự kiện để cập nhật dữ liệu khi có thay đổi
        getActivity().invalidateOptionsMenu();
        cart_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Dừng lắng nghe sự kiện khi Fragment không còn hoạt động
        cart_adapter.stopListening();
    }
}