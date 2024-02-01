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
import java.util.Map;

public class order_adapter extends FirebaseRecyclerAdapter<order, order_adapter.myViewHolder> {

    public order_adapter (@NonNull FirebaseRecyclerOptions<order> options) {
        super(options);
    }
    String value;
    String url;
    RecyclerView rccView;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    checkout_adapter checkout_adapter;
    @Override
    protected void onBindViewHolder(@NonNull order_adapter.myViewHolder holder, int position, @NonNull order model) {
        int adapterPosition = holder.getAdapterPosition();

        holder.buyerName.setText(model.getNameuser());
        holder.status.setText(model.getStatus());
        holder.total.setText(model.getTotalprice().toString() + "$");


        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.buyerName.getContext());

                // Gắn layout XML vào BottomSheetDialog
                View view_detail = LayoutInflater.from(holder.buyerName.getContext()).inflate(R.layout.check_order_detail, null);
                bottomSheetDialog.setContentView(view_detail);


                Spinner spinner = view_detail.findViewById(R.id.spinner_order);
                String[] country = { "Confirmed", "Wait for delivery", "Delivering", "Delivered"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(holder.buyerName.getContext(), android.R.layout.simple_spinner_item,country);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                // Find the index of the current status in the country array
                int statusIndex = Arrays.asList(country).indexOf(model.getStatus());

                // Set the selection based on the index
                spinner.setSelection(statusIndex);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        value = parentView.getItemAtPosition(position).toString();
//                        Log.d("value",value);
//                        Toast.makeText(holder.buyerName.getContext(), value, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing here
                    }
                });

                rccView = view_detail.findViewById(R.id.rv);
                TextView name_detail =view_detail.findViewById(R.id.detail_order_name);
                TextView address_detail =view_detail.findViewById(R.id.detail_order_address);
                TextView phone_detail =view_detail.findViewById(R.id.detail_order_phone);
                Button ord_update = view_detail.findViewById(R.id.update_order_btn);
                Button ord_cancel = view_detail.findViewById(R.id.cancel_order_btn);


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

                // Khởi tạo adapter và gắn nó vào RecyclerView
                checkout_adapter = new checkout_adapter(options, holder.buyerName.getContext());
                rccView.setAdapter(checkout_adapter);
                checkout_adapter.startListening();
                LinearLayoutManager layoutManager = new LinearLayoutManager( holder.buyerName.getContext());
                rccView.setLayoutManager(layoutManager);

                name_detail.setText(model.getNameuser());
                address_detail.setText(model.getAddress());
                phone_detail.setText(model.getPhonenumber());

                ord_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", value);

                        FirebaseDatabase.getInstance().getReference().child("Order")
                                .child(getRef(adapterPosition).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.buyerName.getContext(), "Update successfully.", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.buyerName.getContext(), "Error Update.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

                ord_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(holder.buyerName.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.buyerName.getContext());
                builder.setTitle("Are you sure ?");
                builder.setMessage("Deleted data can't be undo. ");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Order")
                                .child(getRef(adapterPosition).getKey()).removeValue();

                        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                                .child(model.getOrderId());

                        // Thực hiện xóa item trong Firebase
                        cartReference.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // Hiển thị thông báo xóa thành công (nếu cần)
                                    Toast.makeText(holder.buyerName.getContext(), "Item removed from cart.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Hiển thị thông báo lỗi nếu xóa không thành công (nếu cần)
                                    Toast.makeText(holder.buyerName.getContext(), "Error removing item from cart.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.buyerName.getContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });
    }


    @NonNull
    @Override
    public order_adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_order_list, parent, false);
        return new order_adapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView buyerName, total, status ;
        Button btn_edit, btn_delete;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_edit = itemView.findViewById(R.id.btn_edit);
            buyerName = itemView.findViewById(R.id.order_name);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }

    }
}
