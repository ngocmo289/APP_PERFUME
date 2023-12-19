package com.nnmo.app_perfume;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

public class home_model_product_adapter extends FirebaseRecyclerAdapter<model_product, home_model_product_adapter.myViewHolder> {

    String cost2;
    public home_model_product_adapter(@NonNull FirebaseRecyclerOptions<model_product> options) {
        super(options);
    }

    protected void onBindViewHolder(@NonNull home_model_product_adapter.myViewHolder holder, int position, @NonNull model_product model) {



        if (model.getName() != null) {
            holder.name.setText(model.getName());
        } else {
            holder.name.setText("N/A");
        }

        if (model.getPrice() != null) {
            if (! model.getSale().toString().equals("0")) {
                holder.frame_sale.setVisibility(View.VISIBLE);
                holder.sale.setText(model.getSale().toString() + "%");
                double percent = ((double)model.getSale())/100;
                double saleoff = model.getPrice() * percent;
                double new_price = model.getPrice() - saleoff;
                String cost = "$" + new_price;
                cost2 = cost;
                // Tạo một SpannableString để giữ cả giá mới và giá cũ
                SpannableString spannableString = new SpannableString(cost + "   $" + model.getPrice());

                // Đặt chữ giá mới mà không gạch ngang
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, (cost).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Gạch chân chữ giá cũ
                spannableString.setSpan(new StrikethroughSpan(), (cost + " ").length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), (cost + " ").length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Hiển thị trong TextView
                holder.price.setText(spannableString);
            } else {
                holder.price.setText("$" + model.getPrice().toString() );
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
                TextView name_detail =view_detail.findViewById(R.id.detail_name_prd);
                TextView price_detail =view_detail.findViewById(R.id.detail_price_prd);
                TextView des_detail =view_detail.findViewById(R.id.detail_des_prd);

                name_detail.setText(model.getName());
                price_detail.setText(cost2);
                des_detail.setText(model.getDes());

                Glide.with(img_detail.getContext())
                        .load(model.getImg())
                        .into(img_detail);

                // Hiển thị BottomSheetDialog
                bottomSheetDialog.show();
            }
        });
    }



    public home_model_product_adapter.myViewHolder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item_prd, parent, false);
        return new home_model_product_adapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, sale;

        CheckBox cb_like;
        ImageView img;

        FrameLayout frame_sale;


        public myViewHolder(@NonNull View itemView) {
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
