package com.nnmo.app_perfume;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class favorite_model_product_adapter extends RecyclerView.Adapter<favorite_model_product_adapter.ViewHolder> {
    // ... {

    private List<model_product> productList = new ArrayList<>();
    private String cost2;
    FirebaseFirestore mStore;
    Context context;
    public favorite_model_product_adapter(Context context, List<model_product> productList) {
        this.productList = productList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStore = FirebaseFirestore.getInstance();

        model_product model = productList.get(position);

        if (model.getName() != null) {
            holder.name.setText(model.getName());
        } else {
            holder.name.setText("N/A");
        }

        holder.cb_like.setChecked(true);

        holder.cb_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                removeProductFromFavorites(model.getId(),position);
            }
        });

        if (model.getPrice() != null) {
            if (!model.getSale().toString().equals("0")) {
                holder.frame_sale.setVisibility(View.VISIBLE);
                holder.sale.setText(model.getSale().toString() + "%");
                double percent = ((double) model.getSale()) / 100;
                double saleoff = model.getPrice() * percent;
                double new_price = model.getPrice() - saleoff;
                String cost = "$" + new_price;

                // Tạo một SpannableString để giữ cả giá mới và giá cũ
                SpannableString spannableString = new SpannableString(cost + "   $" + model.getPrice());

                // Đặt chữ giá mới mà không gạch ngang
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, (cost).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Gạch chân chữ giá cũ
                spannableString.setSpan(new StrikethroughSpan(), (cost + " ").length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), (cost + " ").length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Hiển thị trong TextView
                holder.price.setText(spannableString);

                // Gán giá trị cho cost2 ở đây
                cost2 = cost;
            } else {
                holder.price.setText("$" + model.getPrice().toString());
                cost2 = "$" + model.getPrice().toString();
            }
        } else {
            holder.price.setText("N/A");
        }

        Glide.with(holder.img.getContext())
                .load(model.getImg())
                .into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.img.getContext());

                // Gắn layout XML vào BottomSheetDialog
                View view_detail = LayoutInflater.from(holder.img.getContext()).inflate(R.layout.product_detail, null);
                bottomSheetDialog.setContentView(view_detail);

                ImageView img_detail = view_detail.findViewById(R.id.detail_img_prd);
                TextView name_detail = view_detail.findViewById(R.id.detail_name_prd);
                TextView price_detail = view_detail.findViewById(R.id.detail_price_prd);
                TextView des_detail = view_detail.findViewById(R.id.detail_des_prd);
                Button addcart = view_detail.findViewById(R.id.addtocartbutton);

                name_detail.setText(model.getName());
                if (!model.getSale().toString().equals("0")) {
                    double new_price = model.getPrice() - (model.getPrice() * (((double) model.getSale()) / 100));
                    ;
                    String cost = "$" + new_price;
                    price_detail.setText(cost);
                } else {
                    price_detail.setText("$" + model.getPrice().toString());
                }
                des_detail.setText(model.getDes());

                Glide.with(img_detail.getContext())
                        .load(model.getImg())
                        .into(img_detail);
                addcart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = 1;
                        cart_item item = new cart_item(model.getId(), model.getName(), model.getPrice(), model.getImg(), count);
                        // Thêm sản phẩm vào giỏ hàng
                        addToCart(item,currentUser);
                    }
                });

                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });
    }


    private void addToCart(cart_item item, FirebaseUser currentUser) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
        // Tạo đối tượng DatabaseReference để thêm sản phẩm vào Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId).child(item.getItemId());

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng hay chưa
        cartReference.child(item.getItemId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String itemId = item.getItemId();

                // Kiểm tra xem có tồn tại nút con với key là itemId hay không
                if (dataSnapshot.exists()) {
                    // Kiểm tra xem nút con có chứa trường "count" hay không
                    if (dataSnapshot.hasChild("count")) {
                        Integer currentCount = dataSnapshot.child("count").getValue(Integer.class);
                        Log.d("current", "current: " + currentCount);
                        if (currentCount != null) {
                            // Thực hiện cập nhật count trong Realtime Database
                            cartReference.child(itemId).child("count").setValue(currentCount + 1);
                            Toast.makeText(context, "Item count updated in cart.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error adding item to cart.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Nếu nút con không chứa trường "count", xử lý tương ứng
                        Toast.makeText(context, "Item does not have a count field.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu sản phẩm chưa tồn tại thì thêm số lượng
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("img", item.getImg());
                    itemMap.put("count", item.getCount());
                    itemMap.put("itemId", item.getItemId());
                    // Thêm sản phẩm vào giỏ hàng trong Realtime Database
                    // Thêm sản phẩm vào giỏ hàng trong Realtime Database
                    databaseReference.setValue(itemMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Item added to cart successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error adding item to cart.", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi đọc dữ liệu thất bại (nếu cần)
                Toast.makeText(context, "Error reading cart data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeProduct(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    private void removeProductFromFavorites(String productId, int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Kiểm tra xem người dùng có đăng nhập không
        if (currentUser != null) {
            // Lấy ID của người dùng
            String userId = currentUser.getUid();

            // Xóa ID sản phẩm khỏi collection "Favorites"
            mStore.collection("Users").document(userId).collection("Favorites").document(productId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Nếu xóa thành công, cập nhật RecyclerView
                            removeProduct(position);
                        } else {
                            // Xử lý khi xóa không thành công (nếu cần)
                        }
                    });
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item_prd, parent, false);
        return new ViewHolder(view);
    }

    public void updateData(List<model_product> updatedProductList) {
        productList.clear();
        productList.addAll(updatedProductList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, sale;

        CheckBox cb_like;
        ImageView img;

        FrameLayout frame_sale;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.home_list_item_prd);
            cb_like = itemView.findViewById(R.id.home_check_favorite);
            name = itemView.findViewById(R.id.home_name_prd);
            price = itemView.findViewById(R.id.home_price_prd);
            sale = itemView.findViewById(R.id.home_sale);
            frame_sale = itemView.findViewById(R.id.home_frame_sale);
        }
    }
}
