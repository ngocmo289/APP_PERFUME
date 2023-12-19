package com.nnmo.app_perfume;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class manage_model_product_adapter extends FirebaseRecyclerAdapter<model_product, manage_model_product_adapter.myViewHolder> {

    public manage_model_product_adapter(@NonNull FirebaseRecyclerOptions<model_product> options) {
        super(options);
    }

    String url;
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull model_product model) {

        int adapterPosition = holder.getAdapterPosition();

        if (model.getName() != null) {
            holder.name.setText(model.getName());
        } else {
            holder.name.setText("N/A");
        }

        if (model.getPrice() != null) {
            holder.price.setText(model.getPrice().toString() + "$");
        } else {
            holder.price.setText("N/A");
        }

        if (model.getSale() != null) {
            holder.sale.setText(model.getSale().toString() + "%");
        } else {
            holder.sale.setText("N/A");
        }

        Glide.with(holder.img.getContext())
                .load(model.getImg())
                .into(holder.img);

        if (model.getItem_new()) {
            holder.item_new.setTypeface(holder.item_new.getTypeface(), Typeface.BOLD);
            holder.item_new.setPaintFlags(holder.item_new.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.item_new.setTextColor(Color.parseColor("#E6879C"));
        } else {
            holder.item_new.setTextColor(Color.parseColor("#686868"));
        }

        if (model.getItem_popular()) {
            holder.item_popular.setTypeface(holder.item_popular.getTypeface(), Typeface.BOLD);
            holder.item_popular.setPaintFlags(holder.item_popular.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.item_popular.setTextColor(Color.parseColor("#E6879C"));
        } else {
            holder.item_popular.setTextColor(Color.parseColor("#686868"));
        }

        if (model.getItem_sale()) {
            holder.item_sale.setTypeface(holder.item_sale.getTypeface(), Typeface.BOLD);
            holder.item_sale.setPaintFlags(holder.item_sale.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.item_sale.setTextColor(Color.parseColor("#E6879C"));
        } else {
            holder.item_sale.setTextColor(Color.parseColor("#686868"));
        }

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.img.getContext());

                // Gắn layout XML vào BottomSheetDialog
                View view_update = LayoutInflater.from(holder.img.getContext()).inflate(R.layout.update_prd, null);
                bottomSheetDialog.setContentView(view_update);

                EditText udt_name = view_update.findViewById(R.id.update_name_edt);
                EditText udt_price = view_update.findViewById(R.id.update_price_edt);
                EditText udt_sale = view_update.findViewById(R.id.update_sale_edt);
                EditText udt_des = view_update.findViewById(R.id.update_des_edt);
                ImageView udt_img = view_update.findViewById(R.id.update_img);
                CheckBox udt_item_sale = view_update.findViewById(R.id.update_item_sale_cb);
                Button udt_update = view_update.findViewById(R.id.update_update_btn);
                Button udt_cancel = view_update.findViewById(R.id.update_cancel_btn);
                CheckBox udt_item_new = view_update.findViewById(R.id.update_item_new_cb);
                CheckBox udt_item_popular = view_update.findViewById(R.id.update_item_popular_cb);

                url = model.getImg();

                udt_name.setText(model.getName());
                udt_price.setText(model.getPrice().toString());
                udt_sale.setText(model.getSale().toString());
                udt_des.setText(model.getDes());
                Glide.with(udt_img.getContext())
                        .load(url)
                        .into(udt_img);
                udt_item_sale.setChecked(model.getItem_sale());
                udt_item_popular.setChecked(model.getItem_popular());
                udt_item_new.setChecked(model.getItem_new());

                udt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(holder.name.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                udt_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Tạo một AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                        builder.setTitle("Add the URL image");

                        // Inflate layout của dialog
                        View dialogView = LayoutInflater.from(holder.name.getContext()).inflate(R.layout.custom_dialog_change_img, null);
                        builder.setView(dialogView);

                        // Lấy reference đến EditText trong layout của dialog
                        EditText editTextUrl = dialogView.findViewById(R.id.url_img);

                        // Thêm nút Cancel vào dialog
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(holder.name.getContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
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
                                    Glide.with(udt_img.getContext())
                                            .load(imageUrl)
                                            .into(udt_img);
                                } else {
                                    // Nếu URL trống hoặc không hợp lệ, hiển thị thông báo lỗi
                                    Toast.makeText(holder.name.getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                                }

                                // Đóng dialog
                                dialog.dismiss();
                            }
                        });

                        // Hiển thị dialog
                        builder.show();
                    }
                });


                udt_update.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // Kiểm tra xem các trường có được điền đầy đủ hay không
                        if (TextUtils.isEmpty(udt_name.getText()) || TextUtils.isEmpty(udt_price.getText()) || TextUtils.isEmpty(udt_sale.getText()) || TextUtils.isEmpty(udt_des.getText())) {
                            Toast.makeText(holder.name.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(udt_price.getText().toString().equals("0")){
                            Toast.makeText(holder.name.getContext(), "Price cannot be set to $0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(udt_sale.getText().toString().equals("0")){
                            udt_item_sale.setChecked(false);
                        }
                        else{
                            udt_item_sale.setChecked(true);
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", udt_name.getText().toString());
                        map.put("price", Integer.parseInt(udt_price.getText().toString()));
                        map.put("sale", Integer.parseInt(udt_sale.getText().toString()));
                        map.put("img",url);
                        map.put("des", udt_des.getText().toString());
                        map.put("item_new", udt_item_new.isChecked());
                        map.put("item_popular", udt_item_popular.isChecked());
                        map.put("item_sale", udt_item_sale.isChecked());

                        FirebaseDatabase.getInstance().getReference().child("Product")
                                .child(getRef(adapterPosition).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.name.getContext(), "Update successfully.", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.name.getContext(), "Error updating.", Toast.LENGTH_SHORT).show();
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                    }
                });
                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure ?");
                builder.setMessage("Deleted data can't be undo. ");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Product")
                                .child(getRef(adapterPosition).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.name.getContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.udt_list_item_product, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, sale, item_new, item_popular, item_sale;
        Button btn_edit, btn_delete;
        ImageView img;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_sp);
            name = itemView.findViewById(R.id.name_sp);
            price = itemView.findViewById(R.id.price);
            sale = itemView.findViewById(R.id.sale);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            item_new = itemView.findViewById(R.id.item_new);
            item_popular = itemView.findViewById(R.id.item_popular);
            item_sale = itemView.findViewById(R.id.item_sale);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }

    }
}
