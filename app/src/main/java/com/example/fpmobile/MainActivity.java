package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layout;
    private TextView user;
    private LinearLayout nav_profile_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main_container);

        nav_profile_btn = findViewById(R.id.nav_profile);
        nav_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
            }
        });

        user = findViewById(R.id.main_current_user);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = currentUser.getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        user.setText(doc.getData().get("name").toString() + "!");
                    } else {
                        Toast.makeText(MainActivity.this, "Error, data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gagal mendapatkan data user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore
                .getInstance()
                .collection("events")
                .whereEqualTo("status", "active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                addEventCard(doc.getData(), doc.getId());
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Gagal mendapatkan data event", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addEventCard(Map<String, Object> data, String id) {
        View v = getLayoutInflater().inflate(R.layout.card, null);

        TextView title = v.findViewById(R.id.card_title);
        TextView desc = v.findViewById(R.id.card_desc);
        ImageView img = v.findViewById(R.id.card_img);
        LinearLayout wrapper = v.findViewById(R.id.main_card_wrapper);

        title.setText(data.get("title").toString());
        if (data.get("desc").toString().length() > 80) {
            desc.setText(data.get("desc").toString().substring(0, 80) + "...");
        } else {
            desc.setText(data.get("desc").toString());
        }
        Picasso.get().load(data.get("path").toString()).into(img);

        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, event_details.class).putExtra("id", id));
            }
        });

        layout.addView(v);
    }
}
