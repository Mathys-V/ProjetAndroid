package com.example.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des boutons depuis le XML
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnHighscores = findViewById(R.id.btnHighscores);

        // Action quand on clique sur "Jouer" (Utilisation de la syntaxe Lambda)
        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        // Action quand on clique sur "Highscores" (Utilisation de la syntaxe Lambda)
        btnHighscores.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HighscoreActivity.class);
            startActivity(intent);
        });
    }
}