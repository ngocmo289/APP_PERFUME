package com.nnmo.app_perfume;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ManageFragment extends Fragment {

    Button btn_product, btn_order, btn_notify;

    public ManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        // Find views by ID
        btn_product = view.findViewById(R.id.edit_product);
        btn_order = view.findViewById(R.id.check_order);
        btn_notify = view.findViewById(R.id.edit_notify);

        btn_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedit_product();
            }
        });

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedit_order();
            }
        });

        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedit_notify();
            }
        });
        // Now you can use btn_manage, btn_order, and btn_notify
        return view;
    }

    private void openedit_product() {
        Intent intent = new Intent(getActivity(), edit_product.class);
        startActivity(intent);
    }

    private void openedit_order(){

    }

    private void openedit_notify(){

    }
}
