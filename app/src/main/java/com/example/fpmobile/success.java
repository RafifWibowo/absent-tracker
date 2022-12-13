package com.example.fpmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class success extends AppCompatActivity {

    Button successBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        successBtn = (Button) findViewById(R.id.successBtn);
        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(success.this, ProfileActivity.class));
                Toast.makeText(success.this, "Selamat mengikuti event :)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
