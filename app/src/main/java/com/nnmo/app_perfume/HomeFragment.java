package com.nnmo.app_perfume;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private BottomNavigationView menu_item_prd;
    RecyclerView rccv;
    home_model_product_adapter mainAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        EditText home_edt_search = view.findViewById(R.id.home_edt_search);
        menu_item_prd = view.findViewById(R.id.menu_item);
        rccv = view.findViewById(R.id.rccv_item_prd);

        FirebaseRecyclerOptions<model_product> options = new FirebaseRecyclerOptions.Builder<model_product>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("item_new").equalTo(true), model_product.class)
                .build();

        mainAdapter = new home_model_product_adapter(options);
        rccv.setAdapter(mainAdapter);
        //mainAdapter.startListening();

        home_edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FirebaseRecyclerOptions<model_product> options =
                        new FirebaseRecyclerOptions.Builder<model_product>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name").startAt(s.toString()).endAt(s+"~"), model_product.class)
                                .build();
                mainAdapter =new home_model_product_adapter(options);
                mainAdapter.startListening();
                rccv.setAdapter(mainAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

                FirebaseRecyclerOptions<model_product> options =
                        new FirebaseRecyclerOptions.Builder<model_product>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name").startAt(s.toString()).endAt(s+"~"), model_product.class)
                                .build();
                mainAdapter =new home_model_product_adapter(options);
                mainAdapter.startListening();
                rccv.setAdapter(mainAdapter);
            }
        });


// Thay đổi từ LinearLayoutManager sang GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rccv.setLayoutManager(layoutManager);

        menu_item_prd.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if(itemID == R.id.menu_item_new){
                    FirebaseRecyclerOptions<model_product> options = new FirebaseRecyclerOptions.Builder<model_product>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("item_new").equalTo(true), model_product.class)
                            .build();

                    mainAdapter = new home_model_product_adapter(options);
                    rccv.setAdapter(mainAdapter);
                    mainAdapter.startListening();
                    return true;
                }

                if(itemID == R.id.menu_item_popular){
                    FirebaseRecyclerOptions<model_product> options = new FirebaseRecyclerOptions.Builder<model_product>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("item_popular").equalTo(true), model_product.class)
                            .build();

                    mainAdapter = new home_model_product_adapter(options);
                    rccv.setAdapter(mainAdapter);
                    mainAdapter.startListening();
                    return true;
                }
                if(itemID == R.id.menu_item_sale){
                    FirebaseRecyclerOptions<model_product> options = new FirebaseRecyclerOptions.Builder<model_product>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("item_sale").equalTo(true), model_product.class)
                            .build();

                    mainAdapter = new home_model_product_adapter(options);
                    rccv.setAdapter(mainAdapter);
                    mainAdapter.startListening();
                    return true;
                }
                if(itemID == R.id.menu_item_all){
                    FirebaseRecyclerOptions<model_product> options = new FirebaseRecyclerOptions.Builder<model_product>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Product"), model_product.class)
                            .build();

                    mainAdapter = new home_model_product_adapter(options);
                    rccv.setAdapter(mainAdapter);
                    mainAdapter.startListening();
                    return true;
                }
                return false;
            }
        });
        return view;
    }
    public void onStart() {
        super.onStart();
        if (mainAdapter != null) {
            mainAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mainAdapter != null) {
            mainAdapter.stopListening();
        }
    }
}