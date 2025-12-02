package com.jmccanizares.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jmccanizares.tictactoe.api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitingConnectionActivity extends AppCompatActivity {
    private TextView gameCodeText;
    private TextView statusText;
    private ProgressBar progressBar;

    private String gameCode;
    private String playerId;
    private String playerName;
    private boolean isCreator;

    private Handler pollHandler;
    private Runnable pollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_connection);

        gameCode = getIntent().getStringExtra("gameCode");
        playerId = getIntent().getStringExtra("playerId");
        playerName = getIntent().getStringExtra("playerName");
        isCreator = getIntent().getBooleanExtra("isCreator", false);

        gameCodeText = findViewById(R.id.gameCodeText);
        statusText = findViewById(R.id.statusText);
        progressBar = findViewById(R.id.progressBar);

        gameCodeText.setText("Game Code: " + gameCode);

        if (isCreator) {
            statusText.setText("Waiting for opponent to join...\nShare the game code!");
        } else {
            statusText.setText("Connecting to game...");
        }

        startPolling();
    }

    private void startPolling() {
        pollHandler = new Handler();
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                ApiHelper.getGame(gameCode, new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject game) {
                        try {
                            String player2Id = game.optString("player2_id", null);
                            String status = game.getString("status");

                            if (player2Id != null && ! player2Id.equals("null") && !"waiting".equals(status)) {
                                statusText.setText("Both players connected!\nStarting game...");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startSymbolSelection(game);
                                    }
                                }, 1000);
                            } else {
                                pollHandler.postDelayed(pollRunnable, 1000);
                            }
                        } catch (JSONException e) {
                            pollHandler.postDelayed(pollRunnable, 2000);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pollHandler.postDelayed(pollRunnable, 2000);
                    }
                });
            }
        };
        pollHandler.postDelayed(pollRunnable, 500);
    }

    private void startSymbolSelection(JSONObject game) {
        try {
            Intent intent = new Intent(WaitingConnectionActivity.this, SymbolSelectionActivity. class);
            intent.putExtra("gameCode", game.getString("game_code"));
            intent. putExtra("playerId", playerId);
            intent.putExtra("playerName", playerName);
            intent.putExtra("player1Id", game.getString("player1_id"));
            intent.putExtra("player2Id", game.getString("player2_id"));
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
    }
}