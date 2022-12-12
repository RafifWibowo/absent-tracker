package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextView login;

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button registerBtn;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.register_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.register_confirm_password);
        registerBtn = findViewById(R.id.register_btn);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_name = name.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_confirm_password = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_password)) {
                    Toast.makeText(RegisterActivity.this, "Kredensial tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                } else if (!txt_password.equals(txt_confirm_password)) {
                    Toast.makeText(RegisterActivity.this, "Konfirmasi password tidak sesuai", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txt_name, txt_email, txt_password);
                }
            }
        });
    }

    private void registerUser(String txt_name, String txt_email, String txt_password) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Mendapatkan data user yang berada di aplikasi
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userUid = currentUser.getUid();

                    // Menambahkan data ke firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", txt_name);
                    userData.put("email", txt_email);
                    userData.put("uid", userUid);
                    userData.put("role", "user");
                    db.collection("users").document(userUid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
