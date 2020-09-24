package org.alexcode.snake;

import android.content.Context;
import android.content.SharedPreferences;


public class PlayerPreferences {

    private static final String PLAYER_PREFERENCE = "org.alexcode.snake";
    private static final String PLAYER_NAME = "pref_player_name";
    private static final int GAMES_PLAYED = 0;
    private static final int HI_SCORE = 0;
    private static final String VOLUME_CONTROL = "pref_volume_control"; //will be used to activate or mute sounds
    private static final String GAME_LANGUAGE = "pref_game_lang"; //will be used to set the language of the game
    private static final String GAME_MODE = "pref_game_mode"; //will be used to play online or offline
    private static SharedPreferences sharedPreferences;

    public PlayerPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PLAYER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void savePlayerData(String playerName, int gamesPlayed, int hiScore, String volume, String language, String gameMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_NAME, playerName);
        editor.putInt(String.valueOf(GAMES_PLAYED), gamesPlayed);
        editor.putInt(String.valueOf(HI_SCORE), hiScore);
        editor.putString(VOLUME_CONTROL, volume);
        editor.putString(GAME_LANGUAGE, language);
        editor.putString(GAME_MODE, gameMode);
        editor.apply();
    }

    public static String getPlayerName() {
        return sharedPreferences.getString(PLAYER_NAME, "Player0");
    }

    public static int getGamesPlayed() {
        return sharedPreferences.getInt("GAMES_PLAYED", 0);
    }

    public static int getHiScore() {
        return sharedPreferences.getInt("HI_SCORE", 0);
    }

    public static String getVolume() {
        return sharedPreferences.getString(VOLUME_CONTROL, "On");
    }

    public static String getGameLanguage() {
        return sharedPreferences.getString(GAME_LANGUAGE, "English");
    }

    public static String getGameMode() {
        return sharedPreferences.getString(GAME_MODE, "Online");
    }
}
