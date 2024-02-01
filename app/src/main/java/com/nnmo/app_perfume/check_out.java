package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class check_out extends AppCompatActivity {
    RecyclerView rccView;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    checkout_adapter checkout_adapter;
    Integer total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        TextView tv_total = findViewById(R.id.total);
        EditText edt_billName = findViewById(R.id.bill_name);
        EditText edt_billAddress = findViewById(R.id.bill_address);
        EditText edt_billPhone = findViewById(R.id.bill_phone);
        Button btn_back = findViewById(R.id.btn_back);
        Button btn_buy = findViewById(R.id.btn_buy);
        rccView = findViewById(R.id.rv);


        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText fields
                String billName = edt_billName.getText().toString().trim();
                String billAddress = edt_billAddress.getText().toString().trim();
                String billPhone = edt_billPhone.getText().toString().trim();

                // Check if any of the fields are empty
                if (billName.isEmpty() || billAddress.isEmpty() || billPhone.isEmpty()) {
                    // Display a toast message indicating the error
                    Toast.makeText(getApplicationContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo một UUID và gán nó cho thuộc tính id của sản phẩm
                    String orderId = UUID.randomUUID().toString();
                    Map<String,Object> map = new HashMap<>();
                    map.put("orderId", orderId);
                    map.put("nameuser", billName);
                    map.put("totalprice",total);
                    map.put("address",billAddress);
                    map.put("phonenumber",billPhone);
                    map.put("status","Confirmed");


                    FirebaseDatabase.getInstance().getReference().child("Order").push()
                            .setValue(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(check_out.this, "Order successfully.", Toast.LENGTH_SHORT).show();

                                    addIdOrder(orderId);

                                    Intent intent = new Intent(check_out.this, success_buy.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(check_out.this, "Error order.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(check_out.this, menu_client.class);
                startActivity(intent);
            }
        });

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
        checkout_adapter = new checkout_adapter(options, this);
        rccView.setAdapter(checkout_adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rccView.setLayoutManager(layoutManager);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

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
    }

    private void addIdOrder(String orderId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Kiểm tra xem người dùng có đăng nhập không
        if (currentUser != null) {
            // Lấy ID của người dùng
            String userId = currentUser.getUid();

            // Thêm ID sản phẩm vào collection "Favorites"
            mStore.collection("Users").document(userId).collection("Order").document(orderId).set(new HashMap<>());

            // Reference đến node cũ
            DatabaseReference oldCartReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);

            // Đọc dữ liệu từ node cũ
            oldCartReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Lấy dữ liệu từ node cũ
                    Object cartData = dataSnapshot.getValue();

                    // Tạo một node mới với tên khóa mới
                    DatabaseReference newCartReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(orderId);

                    // Gán dữ liệu từ node cũ cho node mới
                    newCartReference.setValue(cartData);

                    // Xóa node cũ
                    oldCartReference.removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkout_adapter != null) {
            checkout_adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkout_adapter != null) {
            checkout_adapter.stopListening();
        }
    }

}