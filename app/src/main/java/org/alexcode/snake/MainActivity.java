package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button startGame, profile, ranking, settings;
    private String playerName, volume, language, gameMode;
    private int playerId, gamesPlayed, hiScore;
    private PlayerPreferences playerPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerPreferences =  new PlayerPreferences(this);
        initGameMenu();
        initPlayerData();
        startGame.setOnClickListener(new View.OnClickListener() {   
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Level1.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayerProfile.class);
                startActivity(intent);
            }
        });
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, Rankings.class);
                startActivity(intent);
            }
        });
    }

    private void initGameMenu() {
        startGame = findViewById(R.id.btnStartGame);
        profile = findViewById(R.id.btnPlayerProfile);
        ranking = findViewById(R.id.btnPlayerRank);
        settings = findViewById(R.id.btnPlayerSettings);
    }

    private void initPlayerData() {
        playerId = playerPreferences.getPlayerId();
        playerName = playerPreferences.getPlayerName();
        gamesPlayed = playerPreferences.getGamesPlayed();
        hiScore = playerPreferences.getHiScore();
        volume = playerPreferences.getVolume();
        language = playerPreferences.getGameLanguage();
        gameMode = playerPreferences.getGameMode();
        if(playerId == 0) {
            createNewPlayer();
        } else {
            getPlayerData();
        }
    }

    private void createNewPlayer() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).createNewPlayer(playerId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus().equals("ok")) {
                        if(response.body().getResultCode() == 1) {
                            playerId = response.body().getId();
                            playerName  =  response.body().getName();
                            PlayerPreferences.savePlayerPreferences(playerId, playerName, gamesPlayed, hiScore, volume, language, gameMode);
                            Log.d("CREATE NEW PLAYER", "New player created with id " + playerId);
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Log.d("CREATE NEW PLAYER", "Cannot create new player");
                        } else if (response.body().getResultCode() == 3) {
                            Log.d("CREATE NEW PLAYER", "The users table is empty");
                        } else if (response.body().getResultCode() == 4) {
                            Log.d("CREATE NEW PLAYER", "SQL query error");
                        }
                    }
                } else {
                    Log.d("CREATE NEW PLAYER", "Cannot connect to database.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("CREATE NEW PLAYER", "Api Call fail. The error is: " + t);
            }
        });
    }

    private void getPlayerData() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getPlayerData(playerId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus().equals("ok")) {
                        if(response.body().getResultCode() == 1) {
                            playerName = response.body().getName();
                            gamesPlayed = response.body().getGames_played();
                            hiScore =  response.body().getHiScore();
                            PlayerPreferences.savePlayerPreferences(playerId, playerName, gamesPlayed, hiScore, volume, language, gameMode);
                            Log.d("GET PLAYER DATA", "Player data loaded. Player id: " + playerId);
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Log.d("GET PLAYER DATA", "Cannot load player data. Player id: " + playerId);
                        } else if (response.body().getResultCode() == 3) {
                            Log.d("GET PLAYER DATA", "The players table is empty");
                        } else if (response.body().getResultCode() == 4) {
                            Log.d("GET PLAYER DATA", "SQL query error");
                        }
                    }
                } else {
                    Log.d("GET PLAYER DATA", "Cannot connect to database.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("GET PLAYER DATA", "Api Call fail. The error is: " + t);
            }
        });
    }
}