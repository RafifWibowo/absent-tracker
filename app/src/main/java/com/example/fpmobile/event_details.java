package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class event_details extends AppCompatActivity {
    private ImageView img;
    private TextView name;
    private TextView desc;
    private TextView place;
    private TextView time;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        img = findViewById(R.id.detail_img);
        name = findViewById(R.id.detail_name);
        desc = findViewById(R.id.detail_desc);
        place = findViewById(R.id.detail_place);
        time = findViewById(R.id.detail_time);

        String id = getIntent().getStringExtra("id");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = currentUser.getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("events").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        name.setText(doc.getData().get("title").toString());
                        desc.setText(doc.getData().get("desc").toString());
                        place.setText(doc.getData().get("loc").toString());
                        time.setText(doc.getData().get("time").toString());
                        Picasso.get().load(doc.getData().get("path").toString()).into(img);
                    } else {
                        Toast.makeText(event_details.this, "Error, data event detail tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(event_details.this, "Gagal mendapatkan data event detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore
                .getInstance()
                .collection("absen")
                .whereEqualTo("event_id", id)
                .whereEqualTo("user_id", userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isUserRegistered = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.exists()) {
                                    isUserRegistered = true;
                                }
                            }

                            btn = findViewById(R.id.detail_btn);
                            if (isUserRegistered) {
                                   btn.setVisibility(View.GONE);
                            } else {
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("event_id", id);
                                        data.put("user_id", userUid);
                                        data.put("status", false);
                                        db.collection("absen").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(event_details.this, "Sukses daftar event", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(event_details.this, ProfileActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(event_details.this, "Gagal daftar event", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(event_details.this, "Gagal mendapatkan data absen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
