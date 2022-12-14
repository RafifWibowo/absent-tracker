package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout nav_profile_home;
    private TextView logout_btn;
    private TextView name;
    private TextView email;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        layout = findViewById(R.id.profile_container);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);

        nav_profile_home = findViewById(R.id.nav_home);
        nav_profile_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        logout_btn = findViewById(R.id.profile_logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Logout sukses!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = currentUser.getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        name.setText(doc.getData().get("name").toString());
                        email.setText(doc.getData().get("email").toString());
                    } else {
                        Toast.makeText(ProfileActivity.this, "Data user tidak ada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Gagal mendapatkan data user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore
                .getInstance()
                .collection("absen")
                .whereEqualTo("user_id", userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                DocumentReference docRef = FirebaseFirestore
                                        .getInstance()
                                        .collection("events")
                                        .document(doc.getData().get("event_id").toString());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot dok = task.getResult();
                                            if (dok.exists()) {
                                                addEventCard(dok.getData(), dok.getId(), doc.getData(), doc.getId());
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Data event detail tidak ditemukan", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Gagal mendapatkan data event detail", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Gagal mendapatkan event yang telah didaftarkan", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addEventCard(Map<String, Object> data, String id, Map<String, Object> relation, String absenId) {
        View v = getLayoutInflater().inflate(R.layout.profile_card, null);

        LinearLayout wrapper = v.findViewById(R.id.profile_card_wrapper);
        ImageView img = v.findViewById(R.id.profile_card_img);
        TextView title = v.findViewById(R.id.profile_card_title);
        TextView time = v.findViewById(R.id.profile_card_time);
        TextView status = v.findViewById(R.id.profile_card_status);

        title.setText(data.get("title").toString());
        time.setText(data.get("time").toString());
        Picasso.get().load(data.get("path").toString()).into(img);

        if (Boolean.parseBoolean(relation.get("status").toString())) {
            status.setText("Sudah Absen!");
        }
        else if (data.get("status").toString().equals("active")) {
            // get current time
            Date currentDate = new Date();
            SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            curDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
            try {
                Date fixCurrentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(curDateFormat.format(currentDate));

                // data from db
                String startDate = data.get("time").toString();
                try{
                    Date fixStartDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(startDate);

                    // jika start > currDate
                    if (fixStartDate.compareTo(fixCurrentDate) == 1) {
                        status.setText("Event Belum Dimulai!");
                    }
                    // jika start < currDate
                    else {
                        status.setText("Absen Sekarang!");
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ProfileActivity.this, get_location.class).putExtra("id", id).putExtra("absenId", absenId));
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else {
            status.setText("Tidak Absen!");
        }

        layout.addView(v);
    }
}
