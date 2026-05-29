package com.example.projetandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Informations de la base de données
    private static final String DATABASE_NAME = "GameScores.db";
    private static final int DATABASE_VERSION = 1;

    // Informations de la table
    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "player_name";
    private static final String COLUMN_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de la table avec ID auto-incrémenté, Nom et Score
        String createTable = "CREATE TABLE " + TABLE_SCORES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cas où on met à jour la structure de la base (version supérieure)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    /**
     * Méthode pour ajouter une nouvelle partie dans la base
     */
    public void addScore(String name, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SCORE, score);

        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    /**
     * Méthode pour récupérer les 10 meilleurs scores (utile pour la page Highscore)
     */
    public List<String> getTopScores() {
        List<String> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Requête SQL pour trier par score décroissant et limiter à 10 résultats
        String query = "SELECT * FROM " + TABLE_SCORES + " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 10";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));

                // On formate l'affichage directement ici
                scoreList.add(name + " : " + score + " pts");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scoreList;
    }
}