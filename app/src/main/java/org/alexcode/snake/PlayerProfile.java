package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerProfile extends AppCompatActivity implements EditNameDialog.editPlayerNameListener {
    private TextView textPlayerName, textGamesPlayed, textHiScore;
    private Button btnBackToMenu, btnEditPlayerName;
    private String playerName, newPlayerName;
    private int playerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        initPlayerProfile();
        playerId = PlayerPreferences.getPlayerId();
        getPlayerData();
        btnEditPlayerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerNameEditor(playerName);
            }
        });
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initPlayerProfile() {
        textPlayerName = findViewById(R.id.textPlayerName);
        textGamesPlayed = findViewById(R.id.textGamesNumber);
        textHiScore = findViewById(R.id.textHiScore);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnEditPlayerName = findViewById(R.id.btnEditPlayerName);
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
                            textPlayerName.setText(playerName);
                            textGamesPlayed.setText(Integer.toString(response.body().getGames_played()));
                            textHiScore.setText(Integer.toString(response.body().getHiScore()));
                           Log.d("GET PLAYER PROFILE", "Player data loaded. Player Id: " + playerId);
                        }
                    } else {
                        if (response.body().getResultCode() == 2) {
                            Log.d("GET PLAYER PROFILE", "Cannot load player data. Player Id: " + playerId);
                        } else if (response.body().getResultCode() == 3) {
                            Log.d("GET PLAYER PROFILE", "The players table is empty");
                        } else if (response.body().getResultCode() == 4) {
                            Log.d("GET PLAYER PROFILE", "SQL query error");
                        }
                    }
                } else {
                    Log.d("GET PLAYER PROFILE", "Cannot connect to database.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("GET PLAYER PROFILE", "Api Call fail. The error is: " + t);
            }
        });
    }

    private void openPlayerNameEditor(String playerName) {
        EditNameDialog editNameDialog = new EditNameDialog();
        editNameDialog.setPlayerName(playerName);
        editNameDialog.show(getSupportFragmentManager(), "Edit player name dialog");
    }

    @Override
    public void applyEntryText(String newName) {
        newPlayerName = newName;
        textPlayerName.setText(newPlayerName);
    }
}