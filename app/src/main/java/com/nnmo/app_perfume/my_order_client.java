package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class my_order_client extends AppCompatActivity {

    RecyclerView rccView;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    my_order_adapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_client);

        Button back_my_ord = findViewById(R.id.btn_back);
        rccView = findViewById(R.id.rv);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        rccView.setLayoutManager(new LinearLayoutManager(this));
        // Thực hiện truy vấn để lấy dữ liệu từ Firebase

        if (currentUser != null) {
            String userId = currentUser.getUid();

            mStore.collection("Users").document(userId).collection("Order")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> orderIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orderIds.add(document.getId());
                            }

                            if (!orderIds.isEmpty()) {
                                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Order");

                                // Chuyển đối List<String> thành một mảng để sử dụng trong phương thức orderByChild
                                String[] orderIdsArray = orderIds.toArray(new String[0]);

                                Query query = productRef.orderByChild("orderId").startAt(orderIdsArray[0]).endAt(orderIdsArray[orderIdsArray.length - 1]);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<order> orderList = new ArrayList<>();

                                        // Lặp qua danh sách sản phẩm từ DataSnapshot
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            // Chuyển đổi dữ liệu từ DataSnapshot thành model_order (hoặc dữ liệu order của bạn)
                                            order order = snapshot.getValue(order.class);

                                            // Kiểm tra xem orderId của order có trong danh sách orderIds không
                                            if (orderIds.contains(order.getOrderId())) {
                                                orderList.add(order);
                                            }
                                        }

                                        // Kiểm tra xem mainAdapter đã được khởi tạo hay chưa
                                        if (mainAdapter == null) {
                                            mainAdapter = new my_order_adapter(orderList);
                                            rccView.setAdapter(mainAdapter);
                                        } else {
                                            // Nếu đã khởi tạo, chỉ cần cập nhật dữ liệu
                                            mainAdapter.updateData(orderList);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("OrderClientFragment", "Error checking Order existence", error.toException());
                                    }
                                });
                            } else {
                                // Hiển thị thông báo hoặc thực hiện hành động phù hợp khi danh sách orderIds trống
                                Log.d("OrderClientFragment", "Danh sách đơn hàng trống");
                            }
                        } else {
                            Log.e("OrderClientFragment", "Error getting OrderIds", task.getException());
                        }
                    });
        }

        back_my_ord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}