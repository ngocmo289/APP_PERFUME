package com.nnmo.app_perfume;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class cart_adapter extends FirebaseRecyclerAdapter<cart_item, cart_adapter.CartViewHolder> {

    FirebaseFirestore mStore;
    double new_price;
    Context context;

    public cart_adapter(@NonNull FirebaseRecyclerOptions<cart_item> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cart_item model) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean exit = false;

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    model_product product = productSnapshot.getValue(model_product.class);
                    String productId = product.getId();
                    // Nếu productId trùng với model.getItemId(), thì đó là sản phẩm bạn đang cần
                    if (productId != null && productId.equals(model.getItemId())) {
                        exit = true;
                        // Cập nhật thông tin sản phẩm trong ViewHolder
                        holder.name.setText(product.getName());

                        if (!product.getSale().toString().equals("0")) {
                            double percent = ((double) product.getSale()) / 100;
                            double saleoff = product.getPrice() * percent;
                            new_price = product.getPrice() - saleoff;
                            String cost = "$" + new_price;

                            // Hiển thị trong TextView
                            holder.price.setText(cost);
                        } else {
                            holder.price.setText("$" + product.getPrice().toString());
                            new_price = product.getPrice();
                        }

                        Glide.with(holder.img.getContext())
                                .load(product.getImg())
                                .into(holder.img);
                        updateItemInFirebase(model.getItemId(), product.getImg(), product.getName(),(int)new_price);
                        break; // Dừng vòng lặp khi đã tìm thấy sản phẩm
                    }
                }
                if(exit == false ) {
                    removeItemFromFirebase(model.getItemId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Log.e("CartAdapter", "Error fetching product information", error.toException());
            }
        });



        holder.count.setText(String.valueOf(model.getCount()));

        holder.tru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giảm số lượng khi người dùng nhấn vào nút trừ
                int currentCount = model.getCount();
                if (currentCount == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm Remove Item");
                    builder.setMessage("Are you sure you want to remove this item from the cart?");

                    // Add Confirm (Yes) button
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Confirm to remove the item from Firebase
                            removeItemFromFirebase(model.getItemId());
                        }
                    });

                    // Add Cancel (No) button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss the dialog when the user selects Cancel
                            dialog.dismiss();
                        }
                    });

                    // Show the dialog
                    builder.show();
                } else {
                    currentCount--;
                    // Cập nhật số lượng mới vào model
                    model.setCount(currentCount);
                    // Cập nhật số lượng hiển thị trên TextView
                    holder.count.setText(String.valueOf(currentCount));

                    // Gọi phương thức để cập nhật số lượng trong Firebase
                    updateCountInFirebase(model.getItemId(), currentCount);
                }
            }
        });

        holder.cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tăng số lượng khi người dùng nhấn vào nút cộng
                int currentCount = model.getCount();
                currentCount++;
                // Cập nhật số lượng mới vào model
                model.setCount(currentCount);
                // Cập nhật số lượng hiển thị trên TextView
                holder.count.setText(String.valueOf(currentCount));

                // Gọi phương thức để cập nhật số lượng trong Firebase
                updateCountInFirebase(model.getItemId(), currentCount);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Remove Item");
                builder.setMessage("Are you sure you want to remove this item from the cart?");

                // Add Confirm (Yes) button
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Confirm to remove the item from Firebase
                        removeItemFromFirebase(model.getItemId());
                    }
                });

                // Add Cancel (No) button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog when the user selects Cancel
                        dialog.dismiss();
                    }
                });
                // Show the dialog
                builder.show();
            }
        });
    }

    // Phương thức để cập nhật thông tin sản phẩm trong Firebase
    private void updateItemInFirebase(String itemId, String img, String name, Integer price) {
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(itemId);

        // Cập nhật thông tin sản phẩm trong Firebase
        cartReference.child("name").setValue(name);
        cartReference.child("price").setValue(price);
        cartReference.child("img").setValue(img);
        // Hiển thị thông báo cập nhật thành công (nếu cần)
       // Toast.makeText(context, "Item updated in cart.", Toast.LENGTH_SHORT).show();
    }

    private void updateProductInfo(String productId, CartViewHolder holder) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin sản phẩm từ danh sách Product
                    model_product product = snapshot.getValue(model_product.class);

                    // Cập nhật tên, giá và hình ảnh trong ViewHolder
                    holder.name.setText(product.getName());
                    holder.price.setText("$" + product.getPrice().toString());

                    Glide.with(holder.img.getContext())
                            .load(product.getImg())
                            .into(holder.img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Log.e("CartAdapter", "Error fetching product information", error.toException());
            }
        });
    }


    // Phương thức để cập nhật số lượng trong Firebase
    private void updateCountInFirebase(String itemId, int newCount) {
        Log.d("carrt", "current: " + itemId);
        // Thực hiện cập nhật count trong Realtime Database
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(itemId)
                .child("count");
        cartReference.setValue(newCount);
        // Hiển thị thông báo cập nhật thành công (nếu cần)
        Toast.makeText(context, "Item count updated in cart.", Toast.LENGTH_SHORT).show();
    }

    // Phương thức để xóa item khỏi Firebase
    private void removeItemFromFirebase(String itemId) {
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(itemId);

        // Thực hiện xóa item trong Firebase
        cartReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị thông báo xóa thành công (nếu cần)
                    Toast.makeText(context, "Item removed from cart.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu xóa không thành công (nếu cần)
                    Toast.makeText(context, "Error removing item from cart.", Toast.LENGTH_SHORT).show();
                });
    }

    // Lớp ViewHolder để giữ các thành phần View của mỗi item
    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, price, count;
        Button tru, cong,delete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_sp);
            name = itemView.findViewById(R.id.name_sp);
            price = itemView.findViewById(R.id.price);
            count = itemView.findViewById(R.id.count);
            tru = itemView.findViewById(R.id.tru_count);
            cong = itemView.findViewById(R.id.cong_cout);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }

}
