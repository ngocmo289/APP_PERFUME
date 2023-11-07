package com.nnmo.app_perfume;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class register extends AppCompatActivity {

    EditText password, passwordRepeat;

    Button btn_signin;
    boolean passwordVisible = false;
    boolean passwordRepeatVisible = false;
    Drawable keyDrawable;
    Drawable visibilityOnDrawable;
    Drawable visibilityOffDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //SHOW HIDE PASSWORD
        password = findViewById(R.id.pass);
        passwordRepeat = findViewById(R.id.pass_rp);
        keyDrawable = getResources().getDrawable(R.drawable.key);
        visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
        visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);

        password.setOnTouchListener(passwordTouchListener);
        passwordRepeat.setOnTouchListener(passwordTouchListener);

        btn_signin = findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensignin();
            }
        });
    }

    private View.OnTouchListener passwordTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (v.getRight() - ((EditText) v).getCompoundDrawables()[Right].getBounds().width())) {
                    if (v == password) {
                        if (passwordVisible) {
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            password.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOffDrawable, null);
                        } else {
                            password.setTransformationMethod(null);
                            password.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOnDrawable, null);
                        }
                        passwordVisible = !passwordVisible;
                    } else if (v == passwordRepeat) {
                        if (passwordRepeatVisible) {
                            passwordRepeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordRepeat.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOffDrawable, null);
                        } else {
                            passwordRepeat.setTransformationMethod(null);
                            passwordRepeat.setCompoundDrawablesWithIntrinsicBounds(keyDrawable, null, visibilityOnDrawable, null);
                        }
                        passwordRepeatVisible = !passwordRepeatVisible;
                    }
                    return true;
                }
            }
            return false;
        }
    };

    private void opensignin(){
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}