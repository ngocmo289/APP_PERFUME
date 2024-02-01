package com.nnmo.app_perfume;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class check_order_admin extends AppCompatActivity {

    RecyclerView rccView;
    order_adapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_order_admin);
        Button btn_back = findViewById(R.id.btn_back_udt);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rccView = findViewById(R.id.rv);
        rccView.setLayoutManager(new LinearLayoutManager(this));


        // Tạo một đối tượng Query từ DatabaseReference
        Query query = FirebaseDatabase.getInstance().getReference().child("Order");

        FirebaseRecyclerOptions<order> options =
                new FirebaseRecyclerOptions.Builder<order>()
                        .setQuery(query, order.class)
                        .build();
       // Log.d("FavoriteClientFragment", query.toString());
        mainAdapter = new order_adapter(options);
        rccView.setAdapter(mainAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}