package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button startGame, profile, ranking, settings;
    private String playerName, volume, language, gameMode;
    private int gamesPlayed, hiScore;
    private PlayerPreferences playerPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerPreferences =  new PlayerPreferences(this);
        initGameMenu();
        initPlayerData();
    }

    private void initGameMenu() {
        startGame = findViewById(R.id.btnStartGame);
        profile = findViewById(R.id.btnPlayerProfile);
        ranking = findViewById(R.id.btnPlayerRank);
        settings = findViewById(R.id.btnPlayerSettings);
    }

    private void initPlayerData() {
        playerName = playerPreferences.getPlayerName();
        gamesPlayed =  playerPreferences.getGamesPlayed();
        hiScore = playerPreferences.getHiScore();
        volume = playerPreferences.getVolume();
        language = playerPreferences.getGameLanguage();
        gameMode = playerPreferences.getGameMode();
        if(playerName.equals("Player0")) {
            createNewPlayer();
            PlayerPreferences.savePlayerData(playerName, gamesPlayed, hiScore, volume, language, gameMode);
        } else {
            getPlayerData();
            PlayerPreferences.savePlayerData(playerName, gamesPlayed, hiScore, volume, language, gameMode);
        }
    }

    private void createNewPlayer() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).createNewPlayer(playerName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus().equals("ok")) {
                        if(response.body().getResultCode() == 1) {
                            playerName  =  response.body().getName();
                            Toast.makeText(MainActivity.this, "New account created", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Toast.makeText(MainActivity.this, "Cannot create new account", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getResultCode() == 3) {
                            Toast.makeText(MainActivity.this, "The users table is empty", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getResultCode() == 4) {
                            Toast.makeText(MainActivity.this, "SQL query error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot connect to database. Please check the internet connection and open de game again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void getPlayerData() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getPlayerData(playerName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus().equals("ok")) {
                        if(response.body().getResultCode() == 1) {
                            gamesPlayed = response.body().getGames_played();
                            hiScore =  response.body().getHiScore();
                            Toast.makeText(MainActivity.this, "Player data loaded", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Toast.makeText(MainActivity.this, "Cannot load player data", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getResultCode() == 3) {
                            Toast.makeText(MainActivity.this, "The players table is empty", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getResultCode() == 4) {
                            Toast.makeText(MainActivity.this, "SQL query error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot connect to database. Please check the internet connection and open de game again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
}