package com.example.projetandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvLives, tvScore, tvQuestion;
    private EditText etAnswer;
    private Button btnSubmit;

    private int score = 0;
    private int lives = 3;
    private int expectedAnswer;

    // Nouvelles variables pour le système de Streak (Combo)
    private int currentStreak = 0;
    private int multiplier = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvLives = findViewById(R.id.tvLives);
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);

        updateScoreAndLives();
        generateNewQuestion();

        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private void generateNewQuestion() {
        Random random = new Random();
        int operator = random.nextInt(4);
        int num1, num2;

        switch (operator) {
            case 0:
                num1 = random.nextInt(50) + 1;
                num2 = random.nextInt(50) + 1;
                expectedAnswer = num1 + num2;
                tvQuestion.setText(num1 + " + " + num2);
                break;
            case 1:
                num1 = random.nextInt(50) + 20;
                num2 = random.nextInt(20) + 1;
                expectedAnswer = num1 - num2;
                tvQuestion.setText(num1 + " - " + num2);
                break;
            case 2:
                num1 = random.nextInt(10) + 1;
                num2 = random.nextInt(10) + 1;
                expectedAnswer = num1 * num2;
                tvQuestion.setText(num1 + " × " + num2);
                break;
            case 3:
                num2 = random.nextInt(10) + 1;
                expectedAnswer = random.nextInt(10) + 1;
                num1 = num2 * expectedAnswer;
                tvQuestion.setText(num1 + " ÷ " + num2);
                break;
        }
        etAnswer.setText("");
    }

    private void checkAnswer() {
        String answerStr = etAnswer.getText().toString();

        if (answerStr.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une réponse", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer = Integer.parseInt(answerStr);

        if (userAnswer == expectedAnswer) {
            // GESTION DU STREAK : On augmente le combo
            currentStreak++;
            if (currentStreak >= 5) {
                multiplier = 2; // Au bout de 5 bonnes réponses, les points comptent double
            }

            score += multiplier;

            String msg = "Bonne réponse !";
            if (multiplier > 1) msg += " (Combo x" + multiplier + " !)";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        } else {
            // GESTION DU STREAK : On perd le combo si erreur
            lives--;
            currentStreak = 0;
            multiplier = 1;
            Toast.makeText(this, "Faux ! La réponse était " + expectedAnswer, Toast.LENGTH_SHORT).show();
        }

        updateScoreAndLives();

        if (lives <= 0) {
            showGameOverDialog();
        } else {
            generateNewQuestion();
        }
    }

    private void updateScoreAndLives() {
        tvLives.setText(getString(R.string.label_lives) + " " + lives);

        // Affichage dynamique du score avec le multiplicateur s'il est actif
        String scoreText = getString(R.string.label_score) + " " + score;
        if (multiplier > 1) {
            scoreText += " (x" + multiplier + ")";
        }
        tvScore.setText(scoreText);
    }

    /**
     * Détermine la ligue en fonction du score final
     */
    private String getLeague(int finalScore) {
        if (finalScore >= 80) return "Ligue Légende";
        if (finalScore >= 50) return "Ligue Titan";
        if (finalScore >= 30) return "Ligue Or";
        if (finalScore >= 10) return "Ligue Bronze";
        return "Non classé";
    }

    private void fetchRandomFact(TextView tvFactDisplay) {
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String fact = "";
            try {
                java.net.URL url = new java.net.URL("https://uselessfacts.jsph.pl/api/v2/facts/random");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String apiLine;
                while ((apiLine = reader.readLine()) != null) {
                    response.append(apiLine);
                }
                reader.close();

                org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                fact = jsonResponse.getString("text");

                String currentLanguage = java.util.Locale.getDefault().getLanguage();
                if (currentLanguage.equals("fr") && fact != null && !fact.isEmpty()) {
                    String encodedFact = java.net.URLEncoder.encode(fact, "UTF-8");
                    java.net.URL urlTranslate = new java.net.URL("https://api.mymemory.translated.net/get?q=" + encodedFact + "&langpair=en|fr");
                    java.net.HttpURLConnection connTrans = (java.net.HttpURLConnection) urlTranslate.openConnection();
                    connTrans.setRequestMethod("GET");

                    java.io.BufferedReader transReader = new java.io.BufferedReader(new java.io.InputStreamReader(connTrans.getInputStream()));
                    StringBuilder transResponse = new StringBuilder();
                    String line;
                    while ((line = transReader.readLine()) != null) {
                        transResponse.append(line);
                    }
                    transReader.close();

                    org.json.JSONObject json = new org.json.JSONObject(transResponse.toString());
                    fact = json.getJSONObject("responseData").getString("translatedText");
                }

            } catch (Exception e) {
                e.printStackTrace();
                fact = getString(R.string.fact_error);
            }

            final String finalFact = fact;
            runOnUiThread(() -> tvFactDisplay.setText(finalFact));
        });
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partie Terminée !");

        String playerLeague = getLeague(score);
        builder.setMessage("Votre score final est de " + score + ".\nVous terminez en : " + playerLeague);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // --- NOUVEAU : Le titre de l'anecdote ---
        TextView tvFactTitle = new TextView(this);
        tvFactTitle.setText(getString(R.string.fact_title));
        tvFactTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvFactTitle.setPadding(0, 0, 0, 10);
        layout.addView(tvFactTitle);
        // ----------------------------------------

        // La zone de texte pour l'anecdote elle-même
        TextView tvFact = new TextView(this);
        tvFact.setText(getString(R.string.fact_loading));
        tvFact.setTypeface(null, android.graphics.Typeface.ITALIC);
        tvFact.setPadding(0, 0, 0, 30);
        layout.addView(tvFact);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Entrez votre pseudo");
        layout.addView(input);

        builder.setView(layout);

        fetchRandomFact(tvFact);

        builder.setPositiveButton("Sauvegarder", (dialog, which) -> {
            String playerName = input.getText().toString().trim();
            if (playerName.isEmpty()) {
                playerName = "Anonyme";
            }

            String nameWithLeague = playerName + " [" + playerLeague + "]";

            DatabaseHelper db = new DatabaseHelper(GameActivity.this);
            db.addScore(nameWithLeague, score);

            Intent intent = new Intent(GameActivity.this, HighscoreActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }
}