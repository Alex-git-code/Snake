package org.alexcode.snake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private Context context;
    private List<Player> playersList;

    public PlayerAdapter(Context context, List<Player> playersList) {
        this.context = context;
        this.playersList = playersList;
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.best_players, null);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        Player player = playersList.get(position);
        holder.textPlayerName.setText(player.getName());
        holder.textPlayerScore.setText(player.getHiScore());
    }

    @Override
    public int getItemCount() {
        return playersList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textPlayerName, textPlayerScore;

        public PlayerViewHolder(View playerView) {
            super(playerView);
            textPlayerName = playerView.findViewById(R.id.textPlayerName);
            textPlayerScore = playerView.findViewById(R.id.textPlayerScore);
        }
    }
}
