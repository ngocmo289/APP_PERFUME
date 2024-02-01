package com.nnmo.app_perfume;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class my_order_adapter extends RecyclerView.Adapter<my_order_adapter.myViewHolder> {

    private List<order> orderList;

    public void updateData(List<order> newOrderList) {
        orderList.clear();
        orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }


    public my_order_adapter(List<order> orderList) {
        this.orderList = orderList;
    }

    String value;
    String url;
    RecyclerView rccView;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    checkout_adapter checkout_adapter;

    @NonNull
    @Override
    public my_order_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list, parent, false);
        return new my_order_adapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull my_order_adapter.myViewHolder holder, int position) {
        order model = orderList.get(position);

        holder.buyerName.setText(model.getNameuser());
        holder.status.setText(model.getStatus());
        holder.total.setText(model.getTotalprice().toString() + "$");

        holder.btn_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.buyerName.getContext());

                // Gắn layout XML vào BottomSheetDialog
                View view_detail = LayoutInflater.from(holder.buyerName.getContext()).inflate(R.layout.my_order_detail, null);
                bottomSheetDialog.setContentView(view_detail);

                rccView = view_detail.findViewById(R.id.rv);
                TextView name_detail =view_detail.findViewById(R.id.detail_my_order_name);
                TextView address_detail =view_detail.findViewById(R.id.detail_my_order_address);
                TextView phone_detail =view_detail.findViewById(R.id.detail_my_order_phone);
                TextView status_detail =view_detail.findViewById(R.id.detail_my_order_status);
                TextView total_detail =view_detail.findViewById(R.id.detail_my_order_total);


                mAuth = FirebaseAuth.getInstance();
                mStore = FirebaseFirestore.getInstance();
                // Lấy thông tin người dùng hiện tại từ Firebase
                FirebaseUser currentUser = mAuth.getCurrentUser();
                // Thực hiện truy vấn để lấy dữ liệu từ Firebase
                Query query = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Cart")
                        .child(model.getOrderId());


                FirebaseRecyclerOptions<cart_item> options =
                        new FirebaseRecyclerOptions.Builder<cart_item>()
                                .setQuery(query, cart_item.class)
                                .build();


                name_detail.setText(model.getNameuser());
                address_detail.setText(model.getAddress());
                phone_detail.setText(model.getPhonenumber());
                status_detail.setText(model.getStatus());
                total_detail.setText(model.getTotalprice().toString() + "$");

                // Khởi tạo adapter và gắn nó vào RecyclerView
                checkout_adapter = new checkout_adapter(options, holder.buyerName.getContext());
                rccView.setAdapter(checkout_adapter);
                checkout_adapter.startListening();
                LinearLayoutManager layoutManager = new LinearLayoutManager(holder.buyerName.getContext());
                rccView.setLayoutManager(layoutManager);

                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView buyerName, total, status;
        Button btn_order_detail;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_order_detail = itemView.findViewById(R.id.btn_order_detail);
            buyerName = itemView.findViewById(R.id.order_name);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
        }
    }
}

