package com.nnmo.app_perfume;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class success_buy extends AppCompatActivity {

    Button btn_backHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_buy);

        btn_backHome = findViewById(R.id.btn_continue_shopping);

        btn_backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(success_buy.this, menu_client.class);
                startActivity(intent);
            }
        });
    }
}