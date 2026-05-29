package com.example.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        // Liaison avec le design XML
        ListView lvScores = findViewById(R.id.lvScores);
        Button btnBackMenu = findViewById(R.id.btnBackMenu);

        // 1. Récupération des scores depuis la base de données SQLite
        DatabaseHelper db = new DatabaseHelper(this);
        List<String> topScores = db.getTopScores();

        // 2. Création d'un adaptateur pour relier les données (topScores) à l'affichage (lvScores)
        // android.R.layout.simple_list_item_1 est un design de base fourni par Android
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                topScores
        );

        // 3. Application de l'adaptateur à la liste
        lvScores.setAdapter(adapter);

        // 4. Logique du bouton de retour
        btnBackMenu.setOnClickListener(v -> {
            Intent intent = new Intent(HighscoreActivity.this, MainActivity.class);
            // On nettoie l'historique des écrans pour éviter d'empiler les pages à l'infini
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}