package org.alexcode.snake;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("result_code")
    private int resultCode;
    @SerializedName("name")
    private String name;
    @SerializedName("hi_score")
    private int hiScore;
    @SerializedName("games_played")
    private int games_played;

    public String getStatus() {
        return status;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getName() {
        return name;
    }

    public int getHiScore() {
        return hiScore;
    }

    public int getGames_played() {
        return games_played;
    }
}
