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

public class checkout_adapter extends FirebaseRecyclerAdapter<cart_item, checkout_adapter.CheckoutViewHolder> {
    Context context;

    public checkout_adapter(@NonNull FirebaseRecyclerOptions<cart_item> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_cart, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position, @NonNull cart_item model) {
        Glide.with(holder.img.getContext())
                .load(model.getImg())
                .into(holder.img);
        Log.d("SharedPreferences", "Product ID: " + model.getName());
        holder.name.setText(model.getName());
        holder.price.setText(String.valueOf(model.getPrice()));
        holder.count.setText(String.valueOf(model.getCount()));

    }

    // Lớp ViewHolder để giữ các thành phần View của mỗi item
    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, price, count;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img_sp);
            name = itemView.findViewById(R.id.name_sp);
            price = itemView.findViewById(R.id.price);
            count = itemView.findViewById(R.id.bill_count);
        }
    }

}
