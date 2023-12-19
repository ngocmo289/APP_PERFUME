package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class login extends AppCompatActivity {

    EditText password, email;
    Boolean client;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    Button btn_Signup, btn_login;
    boolean passwordVisible = false;
    Drawable keyDrawable;
    Drawable visibilityOnDrawable;
    Drawable visibilityOffDrawable;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            checkAdmin(uid);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SHOW HIDE PASSWORD

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        keyDrawable = getResources().getDrawable(R.drawable.key);
        visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
        visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        setupShowHidePassword(password);
        btn_login = findViewById(R.id.btn_login);
        btn_Signup = findViewById(R.id.btn_signup);
        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openregister();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1, password1;
                email1 = String.valueOf(email.getText());
                password1 = String.valueOf(password.getText());

                if (TextUtils.isEmpty(email1)) {
                    Toast.makeText(login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password1)) {
                    Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Log.e("YourTag", "Initial task failed for action RecaptchaAction(action=signInWithPassword) with exception", task.getException());
                                if (task.isSuccessful()) {
                                    // If sign in is successful, display a message and navigate to the menu activity.
                                    Toast.makeText(login.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                    // Lấy thông tin người dùng hiện tại
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String uid = currentUser.getUid();
                                    checkAdmin(uid);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void checkAdmin(String uid) {
        DocumentReference df = mStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if("1".equals(documentSnapshot.getString("isClient"))){

                    Intent intent = new Intent(getApplicationContext(), menu_client.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), menu.class);
                    startActivity(intent);
                    finish();
                }
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

    private void openregister() {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
}