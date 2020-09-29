package org.alexcode.snake;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("snake_new_player.php")
    Call<ApiResponse> createNewPlayer(@Field("name") String userName);

    @FormUrlEncoded
    @POST("snake_get_player_data.php")
    Call<ApiResponse> getPlayerData(@Field("name") String userName);

    @FormUrlEncoded
    @POST("snake_get_player_hi_score.php")
    Call<ApiResponse> getPlayerHiScore(@Field("name") String userName);

    @FormUrlEncoded
    @POST("snake_player_stats_update.php")
    Call<ApiResponse> updatePlayerStats(@Field("name") String userName, @Field("score") int score);
}
