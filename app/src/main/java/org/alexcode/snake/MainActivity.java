package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startGame, profile, ranking, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGameMenu();
    }

    private void initGameMenu() {
        startGame = findViewById(R.id.btnStartGame);
        profile = findViewById(R.id.btnPlayerProfile);
        ranking = findViewById(R.id.btnPlayerRank);
        settings = findViewById(R.id.btnPlayerSettings);
    }
}