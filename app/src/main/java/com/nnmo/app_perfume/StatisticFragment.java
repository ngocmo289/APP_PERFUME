package com.nnmo.app_perfume;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StatisticFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FragmentLifecycle", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        PieChart piechart = view.findViewById(R.id.piechart);
        List<PieEntry> entries = new ArrayList<>();

        // Khởi tạo Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Product");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String tenSanPham = productSnapshot.child("name").getValue(String.class);
                    boolean isSale = productSnapshot.child("item_sale").getValue(Boolean.class);
                    boolean ispopular = productSnapshot.child("item_popular").getValue(Boolean.class);
                    boolean isnew = productSnapshot.child("item_new").getValue(Boolean.class);

                   // Log.d("FirebaseData", "Name: " + tenSanPham + ", Sale: " + isSale + ", Popular: " + ispopular + ", New: " + isnew);

                    String label = "";
                    if (isSale && ispopular && isnew) {
                        label = "sale & new & popular";
                    } else if (isnew) {
                        if (ispopular) label = "new & popular";
                        else if (isSale) label = "new & sale";
                        else label = "new";
                    } else if (isSale) {
                        if (ispopular) label = "sale & popular";
                        else label = "sale";
                    } else {
                        label = "popular";
                    }

                    // Kiểm tra xem mục có tồn tại trong danh sách chưa
                    boolean found = false;
                    for (PieEntry entry : entries) {
                        if (entry.getLabel().equals(label)) {
                            entry.setY(entry.getY() + 1); // Tăng giá trị của mục đó
                            found = true;
                            break;
                        }
                    }

                    // Nếu mục không tồn tại, thêm một mục mới vào danh sách
                    if (!found) {
                        entries.add(new PieEntry(1, label));
                    }
                }

                PieDataSet dataSet = new PieDataSet(entries, "");

                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter(piechart));
                pieData.setValueTextSize(15f);

                // Thiết lập dữ liệu cho biểu đồ
                piechart.setData(pieData);
                piechart.getDescription().setEnabled(true); // Tắt mô tả
                piechart.setDrawHoleEnabled(true);
                piechart.setHoleRadius(1f); // Đặt bán kính của lỗ
                piechart.setTransparentCircleRadius(30f); // Đặt bán kính của vòng trong suốt xung quanh lỗ

                // Cập nhật biểu đồ
                piechart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });
        return view;
    }
}