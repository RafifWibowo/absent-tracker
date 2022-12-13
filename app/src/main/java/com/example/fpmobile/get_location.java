package com.example.fpmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class get_location extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView latitude, longitude, title;
    Button getLocBtn, validateBtn;
    private final static int REQUEST_CODE = 100;
    private String id;
    Double latDb, lonDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        title = (TextView) findViewById(R.id.lokasi_title);
        getLocBtn = (Button) findViewById(R.id.getLocBtn);
        validateBtn = (Button) findViewById(R.id.validateLoc);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        id = getIntent().getStringExtra("id");

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("events").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        latDb = Double.parseDouble(doc.getData().get("lat").toString());
                        lonDb = Double.parseDouble(doc.getData().get("long").toString());
                    }
                } else {
                    Toast.makeText(get_location.this, "Gagal mendapatkan data event detail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        Geocoder geocoder = new Geocoder(get_location.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String lat = Double.toString(addresses.get(0).getLatitude());
                            String lon = Double.toString(addresses.get(0).getLongitude());
                            latitude.setText(lat);
                            longitude.setText(lon);

                            validateBtn.setVisibility(View.VISIBLE);
                            validateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    validate_location(lat, lon);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        askPermission();
                    }
                }
            });
        }
    }

    private void validate_location(String lat, String lon) {
        Double latParam = Double.parseDouble(lat);
        Double lonParam = Double.parseDouble(lon);

        Double maxLat = latDb - -0.1;
        Double minLat = latDb + -0.1;
        Double maxLon = lonDb - -0.1;
        Double minLon = lonDb + -0.1;

        if (latParam >= minLat && latParam <= maxLat && lonParam >= minLon && lonParam <= maxLon) {
            Toast.makeText(this, "Validasi lokasi sukses", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(get_location.this, take_photo.class));
            finish();
        } else {
            Toast.makeText(this, "Validasi lokasi gagal", Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(get_location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            } else {
                Toast.makeText(this, "Required Pemission", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}