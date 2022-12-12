package com.example.fpmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class success extends AppCompatActivity {

    Button successBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        successBtn = (Button) findViewById(R.id.successBtn);

    }
}