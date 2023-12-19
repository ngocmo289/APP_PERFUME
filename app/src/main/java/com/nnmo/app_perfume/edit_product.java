package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class edit_product extends AppCompatActivity {


    RecyclerView rccView;
    manage_model_product_adapter mainAdapter;
    boolean isImageSelected = false;
    FloatingActionButton floatingActionButton;

    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        EditText edt_search = findViewById(R.id.edt_search);
        TextView tv_edt = findViewById(R.id.tv_edt);

        Button btn_back = findViewById(R.id.btn_back_udt);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                mainAdapter =new manage_model_product_adapter(options);
                mainAdapter.startListening();
                rccView.setAdapter(mainAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Hide the TextView if there is text in the EditText, otherwise show it
                if (s.length() > 0) {
                    tv_edt.setVisibility(View.GONE);
                } else {
                    tv_edt.setVisibility(View.VISIBLE);
                }

                FirebaseRecyclerOptions<model_product> options =
                        new FirebaseRecyclerOptions.Builder<model_product>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name").startAt(s.toString()).endAt(s+"~"), model_product.class)
                                .build();
                mainAdapter =new manage_model_product_adapter(options);
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

        mainAdapter = new manage_model_product_adapter(options);
        rccView.setAdapter(mainAdapter);

        floatingActionButton = findViewById(R.id.btnAdd);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(edit_product.this);

                // Gắn layout XML vào BottomSheetDialog
                View view_add = LayoutInflater.from(edit_product.this).inflate(R.layout.add_prd, null);
                bottomSheetDialog.setContentView(view_add);

                ImageView add_img = view_add.findViewById(R.id.add_img);
                EditText add_name = view_add.findViewById(R.id.add_name_edt);
                EditText add_Price = view_add.findViewById(R.id.add_price_edt);
                EditText add_sale = view_add.findViewById(R.id.add_sale_edt);
                EditText add_des = view_add.findViewById(R.id.add_des_edt) ;
                CheckBox add_item_new = view_add.findViewById(R.id.add_item_new_cb);
                CheckBox add_item_popular = view_add.findViewById(R.id.add_item_popular_cb);
                CheckBox add_item_sale = view_add.findViewById(R.id.add_item_sale_cb);
                Button add_btn = view_add.findViewById(R.id.add_btn);
                Button add_cancel = view_add.findViewById(R.id.add_cancel_btn);


                add_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Tạo một AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(edit_product.this);
                        builder.setTitle("Add the URL image");

                        // Inflate layout của dialog
                        View dialogView = LayoutInflater.from(edit_product.this).inflate(R.layout.custom_dialog_change_img, null);
                        builder.setView(dialogView);

                        // Lấy reference đến EditText trong layout của dialog
                        EditText editTextUrl = dialogView.findViewById(R.id.url_img);

                        // Thêm nút Cancel vào dialog
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(edit_product.this, "Cancelled.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); // Đóng dialog khi nhấn Cancel
                            }
                        });

                        // Thêm nút Change Image vào dialog
                        builder.setPositiveButton("Change Image", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Lấy URL từ EditText
                                String imageUrl = editTextUrl.getText().toString();
                                url = imageUrl;
                                // Kiểm tra xem URL có hợp lệ không
                                if (!TextUtils.isEmpty(imageUrl) && URLUtil.isValidUrl(imageUrl)) {
                                    // Thực hiện các thay đổi cần thiết với URL hình ảnh
                                    // Ví dụ: Load hình ảnh từ URL vào ImageView
                                    add_img.setBackground(null); // Xóa hình ảnh hiện tại
                                    Glide.with(add_img.getContext())
                                            .load(imageUrl)
                                            .into(add_img);

                                    // Đặt trạng thái đã chọn hình ảnh thành true
                                    isImageSelected = true;

                                } else {
                                    // Nếu URL trống hoặc không hợp lệ, hiển thị thông báo lỗi
                                    Toast.makeText(edit_product.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                                }

                                // Đóng dialog
                                dialog.dismiss();
                            }
                        });

                        // Hiển thị dialog
                        builder.show();
                    }
                });
                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isImageSelected) {
                            Toast.makeText(edit_product.this, "Please select an image", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Kiểm tra xem các trường có được điền đầy đủ hay không
                        if (TextUtils.isEmpty(add_Price.getText()) || TextUtils.isEmpty(add_name.getText()) || TextUtils.isEmpty(add_sale.getText()) || TextUtils.isEmpty(add_des.getText())) {
                            Toast.makeText(edit_product.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(add_Price.getText().toString().equals("0")){
                            Toast.makeText(edit_product.this, "Price cannot be set to $0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(add_sale.getText().toString().equals("0")){
                            add_item_sale.setChecked(false);
                        }
                        else{
                            add_item_sale.setChecked(true);
                        }


                        Map<String,Object> map = new HashMap<>();
                        map.put("name", add_name.getText().toString());
                        map.put("price", Integer.parseInt(add_Price.getText().toString()));
                        map.put("sale", Integer.parseInt(add_sale.getText().toString()));
                        map.put("img",url);
                        map.put("des", add_des.getText().toString());
                        map.put("item_new", add_item_new.isChecked());
                        map.put("item_popular", add_item_popular.isChecked());
                        map.put("item_sale", add_item_sale.isChecked());

                        FirebaseDatabase.getInstance().getReference().child("Product").push()
                                .setValue(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(edit_product.this, "Insert successfully.", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(edit_product.this, "Error inserting.", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                    }
                });

                add_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(edit_product.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });


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