package com.example.projetandroid;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class FeaturesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        Button btnBack = findViewById(R.id.btnBackFromFeatures);
        btnBack.setOnClickListener(v -> finish()); // Retourne au menu précédent
    }
}