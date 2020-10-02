package org.alexcode.snake;

import android.content.Context;
import android.content.SharedPreferences;


public class PlayerPreferences {

    private static final String PLAYER_PREFERENCE = "org.alexcode.snake";
    private static final int PLAYER_ID = 0;
    private static final String PLAYER_NAME = "pref_player_name";
    private static final int GAMES_PLAYED = 0;
    private static final int HI_SCORE = 0;
    private static final String MUSIC = "pref_music"; //will be used to activate or mute music
    private static final String FX_SOUNDS = "pref_fx_sounds"; //will be used to activate or mute fx sounds effect
    private static final String GAME_LANGUAGE = "pref_game_lang"; //will be used to set the language of the game
    private static final String GAME_MODE = "pref_game_mode"; //will be used to play online or offline
    private static SharedPreferences sharedPreferences;

    public PlayerPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PLAYER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void savePlayerPreferences(int playerId, String playerName, int gamesPlayed, int hiScore, String music, String fxSounds, String language, String gameMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PLAYER_ID", playerId);
        editor.putString(PLAYER_NAME, playerName);
        editor.putInt("GAMES_PLAYED", gamesPlayed);
        editor.putInt("HI_SCORE", hiScore);
        editor.putString(MUSIC, music);
        editor.putString(FX_SOUNDS, fxSounds);
        editor.putString(GAME_LANGUAGE, language);
        editor.putString(GAME_MODE, gameMode);
        editor.apply();
    }

    public static int getPlayerId() {
        return sharedPreferences.getInt("PLAYER_ID", 0);
    }

    public static String getPlayerName() {
        return sharedPreferences.getString(PLAYER_NAME, "Player0");
    }

    public static void setPlayerNewName(String playerNewName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_NAME, playerNewName);
        editor.apply();
    }

    public static int getGamesPlayed() {
        return sharedPreferences.getInt("GAMES_PLAYED", 0);
    }

    public static int getHiScore() {
        return sharedPreferences.getInt("HI_SCORE", 0);
    }

    public static String getMusic() {
        return sharedPreferences.getString(MUSIC, "On");
    }

    public static void setMusic(String music) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MUSIC, music);
        editor.apply();
    }

    public static String getFxSounds() {
        return sharedPreferences.getString(FX_SOUNDS, "On");
    }

    public static void setFxSounds(String music) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FX_SOUNDS, music);
        editor.apply();
    }

    public static String getGameLanguage() {
        return sharedPreferences.getString(GAME_LANGUAGE, "English");
    }

    public static void setLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GAME_LANGUAGE, language);
        editor.apply();
    }

    public static String getGameMode() {
        return sharedPreferences.getString(GAME_MODE, "Online");
    }
}
