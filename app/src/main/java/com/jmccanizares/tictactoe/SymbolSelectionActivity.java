package com.jmccanizares.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jmccanizares.tictactoe.api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class SymbolSelectionActivity extends AppCompatActivity {
    private Button selectXButton;
    private Button selectOButton;
    private TextView statusText;
    private TextView instructionText;

    private String gameCode;
    private String playerId;
    private String playerName;
    private String player1Id;
    private String player2Id;

    private JSONObject currentGame;
    private Handler pollHandler;
    private Runnable pollRunnable;
    private boolean symbolSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_selection);

        gameCode = getIntent(). getStringExtra("gameCode");
        playerId = getIntent().getStringExtra("playerId");
        playerName = getIntent().getStringExtra("playerName");
        player1Id = getIntent().getStringExtra("player1Id");
        player2Id = getIntent().getStringExtra("player2Id");

        selectXButton = findViewById(R.id.selectXButton);
        selectOButton = findViewById(R.id.selectOButton);
        statusText = findViewById(R.id.statusText);
        instructionText = findViewById(R.id.instructionText);

        statusText.setText("Game Code: " + gameCode);
        instructionText.setText("Choose your symbol!");

        selectXButton. setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSymbol("X");
            }
        });

        selectOButton. setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSymbol("O");
            }
        });

        startPolling();
    }

    private void selectSymbol(String symbol) {
        if (symbolSelected) return;

        symbolSelected = true;
        selectXButton.setEnabled(false);
        selectOButton.setEnabled(false);

        ApiHelper.getGame(gameCode, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject game) {
                try {
                    currentGame = game;

                    if (playerId.equals(player1Id)) {
                        game.put("player1_symbol", symbol);
                        game.put("player2_symbol", symbol. equals("X") ? "O" : "X");
                    } else {
                        game.put("player2_symbol", symbol);
                        game.put("player1_symbol", symbol.equals("X") ?  "O" : "X");
                    }

                    String p1Symbol = game.optString("player1_symbol", null);
                    String p2Symbol = game.optString("player2_symbol", null);

                    if (p1Symbol != null && ! p1Symbol.equals("null") &&
                            p2Symbol != null && !p2Symbol.equals("null")) {
                        game.put("status", "playing");
                    }

                    ApiHelper.updateGame(game, new ApiHelper.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject updatedGame) {
                            instructionText.setText("Waiting for opponent.. .");
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(SymbolSelectionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            symbolSelected = false;
                            selectXButton.setEnabled(true);
                            selectOButton.setEnabled(true);
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(SymbolSelectionActivity.this, "Error: " + e.getMessage(), Toast. LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast. makeText(SymbolSelectionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                symbolSelected = false;
                selectXButton. setEnabled(true);
                selectOButton.setEnabled(true);
            }
        });
    }

    private void startPolling() {
        pollHandler = new Handler();
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                ApiHelper. getGame(gameCode, new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject game) {
                        try {
                            currentGame = game;

                            if (! symbolSelected) {
                                String mySymbol = playerId.equals(player1Id) ?
                                        game.optString("player1_symbol", null) :
                                        game.optString("player2_symbol", null);

                                if (mySymbol != null && !mySymbol. equals("null")) {
                                    symbolSelected = true;
                                    selectXButton.setEnabled(false);
                                    selectOButton.setEnabled(false);
                                    instructionText.setText("Opponent chose first!\nYou are: " + mySymbol);
                                }
                            }

                            String status = game.getString("status");
                            String p1Symbol = game.optString("player1_symbol", null);
                            String p2Symbol = game.optString("player2_symbol", null);

                            if ("playing".equals(status) &&
                                    p1Symbol != null && !p1Symbol.equals("null") &&
                                    p2Symbol != null && !p2Symbol.equals("null")) {
                                startGameActivity(game);
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

    private void startGameActivity(JSONObject game) {
        try {
            Intent intent = new Intent(SymbolSelectionActivity.this, GameActivity.class);
            intent. putExtra("gameCode", game. getString("game_code"));
            intent.putExtra("playerId", playerId);
            intent.putExtra("playerName", playerName);
            intent.putExtra("player1Id", game.getString("player1_id"));
            intent.putExtra("player2Id", game.getString("player2_id"));
            intent.putExtra("player1Symbol", game.getString("player1_symbol"));
            intent.putExtra("player2Symbol", game.getString("player2_symbol"));
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