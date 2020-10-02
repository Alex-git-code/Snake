package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Rankings extends AppCompatActivity {
    private static final String URL_PLAYERS = "https://upgreat.ro/snake_get_rankings.php";
    Button btnBackToMenu;
    //list to store all players
    List<Player> playersList;
    //the recyclerview
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);
        recyclerView = findViewById(R.id.bestPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //initializing the playersList
        playersList = new ArrayList<>();
        //this method will fetch and parse json to be display it in recyclerview
        getRankings();
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Rankings.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getRankings() {
        RequestQueue queue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        cache.clear();
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);
        // Start the queue
        queue.start();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PLAYERS,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);

                        //traversing through all the object
                        for (int i = 0; i < array.length(); ++i) {

                            //getting player objects from json array
                            JSONObject players = array.getJSONObject(i);

                            //adding the player to players list
                            playersList.add(new Player(
                                    players.getString("name"),
                                    players.getString("hi_score")
                            ));
                        }

                        //creating adapter object and setting it to recyclerview
                        PlayerAdapter adapter = new PlayerAdapter(Rankings.this, playersList);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.d("GET RANKINGS", "Json error. The error is: " + e);
                    }
                },
                error -> {
                    Log.d("GET RANKINGS", "Api Call fail.");
                });
        //adding our stringRequest to queue
        queue.add(stringRequest);
    }
}