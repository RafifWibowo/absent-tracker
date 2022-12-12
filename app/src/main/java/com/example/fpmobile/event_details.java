package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class event_details extends AppCompatActivity {
    private ImageView img;
    private TextView name;
    private TextView desc;
    private TextView place;
    private TextView time;

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
    }
}
