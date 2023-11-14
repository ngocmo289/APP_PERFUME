package com.nnmo.app_perfume;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.FirebaseDatabaseKtxRegistrar;

public class edit_product extends AppCompatActivity {


    RecyclerView rccView;
    model_product_adapter mainAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        EditText edt_search = findViewById(R.id.edt_search);
        TextView tv_edt = findViewById(R.id.tv_edt);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed when text changes
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Hide the TextView if there is text in the EditText, otherwise show it
                if (editable.length() > 0) {
                    tv_edt.setVisibility(View.GONE);
                } else {
                    tv_edt.setVisibility(View.VISIBLE);
                }
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FirebaseRecyclerOptions<model_product> options =
                        new FirebaseRecyclerOptions.Builder<model_product>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name").startAt(s.toString()).endAt(s+"~"), model_product.class)
                                .build();
                mainAdapter =new model_product_adapter(options);
                mainAdapter.startListening();
                rccView.setAdapter(mainAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                FirebaseRecyclerOptions<model_product> options =
                        new FirebaseRecyclerOptions.Builder<model_product>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name").startAt(s.toString()).endAt(s+"~"), model_product.class)
                                .build();
                mainAdapter =new model_product_adapter(options);
                mainAdapter.startListening();
                rccView.setAdapter(mainAdapter);
            }
        });


        rccView = findViewById(R.id.rv);
        rccView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<model_product> options =
                new FirebaseRecyclerOptions.Builder<model_product>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Product"), model_product.class)
                        .build();

        mainAdapter = new model_product_adapter(options);
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