package com.example.bookshelf;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavenie GameView ako obsahu aktivity
        GameView gameView = new GameView(this);
        setContentView(gameView);

        // Voliteľné: Nastavenie titulku v hornej lište
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Catch the Ball");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Umožní návrat do menu pomocou šípky späť v hornej lište
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}