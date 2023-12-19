package com.nnmo.app_perfume;

import static android.content.ContentValues.TAG;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    EditText password, passwordRepeat, email,name;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    Button btn_signin, btn_register;
    boolean passwordVisible = false;
    boolean passwordRepeatVisible = false;
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
        setContentView(R.layout.activity_register);

        //SHOW HIDE PASSWORD
        name = findViewById(R.id.name_user);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        passwordRepeat = findViewById(R.id.pass_rp);
        keyDrawable = getResources().getDrawable(R.drawable.key);
        visibilityOnDrawable = getResources().getDrawable(R.drawable.baseline_visibility_24);
        visibilityOffDrawable = getResources().getDrawable(R.drawable.baseline_visibility_off_24);

        password.setOnTouchListener(passwordTouchListener);
        passwordRepeat.setOnTouchListener(passwordTouchListener);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        btn_signin = findViewById(R.id.btn_signin);
        btn_register = findViewById(R.id.btn_register);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensignin();
            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1, password1, passwordRepeat1, name1;
                email1 = String.valueOf(email.getText());
                password1 = String.valueOf(password.getText());
                passwordRepeat1 = String.valueOf(passwordRepeat.getText());
                name1 = String.valueOf(name.getText());


                if (TextUtils.isEmpty(email1)) {
                    Toast.makeText(register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password1)) {
                    Toast.makeText(register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordRepeat1)) {
                    Toast.makeText(register.this, "Repeat the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password1.equals(passwordRepeat1)) {
                    Toast.makeText(register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng ký thành công

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(register.this, "Authentication successful", Toast.LENGTH_SHORT).show();

                                    DocumentReference df = mStore.collection("Users").document(user.getUid());

                                    // Tạo một collection "Favorites" trong document của người dùng
                                    CollectionReference favoritesCollection = df.collection("Favorites");


                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("name", name1);
                                    userInfo.put("email", email1);
                                    //admin/client
                                    userInfo.put("isClient","1");
                                    df.set(userInfo);

                                    // Thêm sản phẩm vào danh sách yêu thích
                                    addProductToFavorite(favoritesCollection, "huy.com");
                                    addProductToFavorite(favoritesCollection, "mer.com");

                                    Intent intent = new Intent(getApplicationContext(), menu_client.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Xử lý lỗi khi đăng ký không thành công
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        String errorMessage = exception.getMessage();
                                        if (errorMessage != null) {
                                            Toast.makeText(register.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(register.this, "Authentication failed: Unknown error", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(register.this, "Authentication failed: Unknown error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });
    }

    // Hàm thêm sản phẩm vào danh sách yêu thích
    private void addProductToFavorite(CollectionReference favoritesCollection, String productName) {
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("productName", productName);

        favoritesCollection.add(productInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Xử lý khi thêm sản phẩm thành công
                        Log.d("AddProductToFavorite", "Product added successfully: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi thêm sản phẩm thất bại
                        Log.e("AddProductToFavorite", "Error adding product", e);
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

    private void opensignin() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
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
}