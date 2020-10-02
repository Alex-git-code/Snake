package org.alexcode.snake;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNameDialog extends AppCompatDialogFragment {
    private EditText editTextPlayerName;
    private String playerName, newPlayerName;
    private int playerId;
    private editPlayerNameListener listener ;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (editPlayerNameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement editPlayerNameListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        playerName = PlayerPreferences.getPlayerName();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_edit_player_name, null);
        builder.setView(view)
                .setTitle("Edit Player Name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newPlayerName = editTextPlayerName.getText().toString();
                        if(newPlayerName.length() > 15) {
                            Toast.makeText(getContext(), "Player name maximum length can be 15 characters. including spaces.", Toast.LENGTH_SHORT).show();
                        } else {
                            playerId = PlayerPreferences.getPlayerId();
                            if (!newPlayerName.equals(playerName)) {
                                updatePlayerName();
                                Toast.makeText(getContext(), "Player name has been changed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        editTextPlayerName = view.findViewById(R.id.dialogEditPlayerName);
        editTextPlayerName.setText(playerName);
        return builder.create();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public interface editPlayerNameListener {
        void applyEntryText(String newUserName);
    }

    private void updatePlayerName() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).updatePlayerName(playerId, newPlayerName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200) {
                    if(response.body().getStatus().equals("ok")) {
                        if(response.body().getResultCode() == 1) {
                            listener.applyEntryText(newPlayerName);
                            PlayerPreferences.setPlayerNewName(newPlayerName);
                            Log.d("UPDATE PLAYER NAME", "Player name updated. Player id: " + playerId + " New Player Name: " + newPlayerName);
                        }
                    } else {
                        Log.d("UPDATE PLAYER NAME", "Cannot update player name. Player id: " + playerId + " New Player Name: " +newPlayerName);
                    }
                } else {
                    Log.d("UPDATE PLAYER NAME", "Cannot connect to database.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("UPDATE PLAYER NAME", "Api Call fail. The error is: " + t);
            }
        });
    }
}
