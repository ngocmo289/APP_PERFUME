package com.nnmo.app_perfume;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {

    EditText password;
    Button btn_Signup;
    boolean passwordVisible = false;
    Drawable keyDrawable;
    Drawable visibilityOnDrawable;
    Drawable visibilityOffDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SHOW HIDE PASSWORD
        password = findViewById(R.id.pass);
        keyDrawable = getResources().getDrawable(R.drawable.key);
        visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
        visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);

        setupShowHidePassword(password);

        btn_Signup = findViewById(R.id.btn_signup);
        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openregister();
            }
        });
    }

    private void setupShowHidePassword(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[Right].getBounds().width())) {
                        if (passwordVisible) {
                            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            editText.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOffDrawable, null);
                        } else {
                            editText.setTransformationMethod(null);
                            editText.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOnDrawable, null);
                        }
                        passwordVisible = !passwordVisible;
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void openregister(){
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
}