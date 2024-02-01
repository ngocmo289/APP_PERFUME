package com.nnmo.app_perfume;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteClientFragment extends Fragment {

    RecyclerView rccv;
    favorite_model_product_adapter mainAdapter;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_client, container, false);
        rccv = view.findViewById(R.id.rccv_item_prd);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            mStore.collection("Users").document(userId).collection("Favorites")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> favoriteProductIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                favoriteProductIds.add(document.getId());
                            }
                            if (!favoriteProductIds.isEmpty()) {
                                // Log danh sách favoriteProductIds
                                for (String productId : favoriteProductIds) {
                                    Log.d("FavoriteClientFragment", "Favorite Product ID: " + productId);
                                }

                                // Khởi tạo LayoutManager cho RecyclerView
                                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                                rccv.setLayoutManager(layoutManager);

                                // Thực hiện truy vấn đến "Product" node trong Realtime Database
                                // Chú ý: Query không còn startListening() và stopListening() nữa
                                // vì giờ bạn sử dụng RecyclerView.Adapter thay vì FirebaseRecyclerAdapter
                                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");
                                Query query = productRef.orderByChild("id");

                                // Lắng nghe sự kiện khi có thay đổi trong dữ liệu trên Realtime Database
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        List<model_product> productList = new ArrayList<>();

                                        // Lặp qua danh sách sản phẩm từ DataSnapshot
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            // Chuyển đổi dữ liệu từ DataSnapshot thành model_product (hoặc dữ liệu sản phẩm của bạn)
                                            model_product product = snapshot.getValue(model_product.class);

                                            // Kiểm tra xem sản phẩm có "id" trùng với danh sách favoriteProductIds không
                                            if (favoriteProductIds.contains(product.getId())) {
                                                productList.add(product);
                                            }
                                        }

                                        // Kiểm tra xem mainAdapter đã được khởi tạo hay chưa
                                        if (mainAdapter == null) {
                                            // Nếu chưa, khởi tạo và setAdapter
                                            mainAdapter = new favorite_model_product_adapter(getActivity(),productList);
                                            rccv.setAdapter(mainAdapter);
                                        } else {
                                            // Nếu đã khởi tạo, chỉ cần cập nhật dữ liệu
                                            mainAdapter.updateData(productList);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("YourTag", "Error reading data: " + databaseError.getMessage());
                                    }
                                });
                            } else {
                                // Hiển thị thông báo hoặc thực hiện hành động phù hợp khi danh sách yêu thích trống
                                Log.d("FavoriteClientFragment", "Danh sách yêu thích trống");
                            }
                        } else {
                            Log.e("FavoriteClientFragment", "Error getting favorite products", task.getException());
                        }
                    });
        }

        return view;
    }
}
