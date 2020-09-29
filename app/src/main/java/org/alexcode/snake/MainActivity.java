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
    int gamesPlayed, hiScore;
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
    }

    private void initGameMenu() {
        startGame = findViewById(R.id.btnStartGame);
        profile = findViewById(R.id.btnPlayerProfile);
        ranking = findViewById(R.id.btnPlayerRank);
        settings = findViewById(R.id.btnPlayerSettings);
    }

    private void initPlayerData() {
        playerName = playerPreferences.getPlayerName();
        gamesPlayed = playerPreferences.getGamesPlayed();
        hiScore = playerPreferences.getHiScore();
        volume = playerPreferences.getVolume();
        language = playerPreferences.getGameLanguage();
        gameMode = playerPreferences.getGameMode();
        if(playerName.equals("Player0")) {
            createNewPlayer();
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
                            Log.d("CREATE NEW PLAYER", "New player created");
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
                            Log.d("GET PLAYER DATA", "Player data loaded. Player Name: " + playerName);
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Log.d("GET PLAYER DATA", "Cannot load player data. Player Name: " + playerName);
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

            }
        });
    }
}