package com.jmccanizares.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android. widget. ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat. app.AppCompatActivity;

import com.jmccanizares.tictactoe.api. ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LobbyActivity extends AppCompatActivity {
    private Button createGameButton;
    private Button joinGameButton;
    private EditText gameCodeInput;
    private ProgressBar progressBar;

    private String playerId;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        playerId = getIntent().getStringExtra("playerId");
        playerName = getIntent().getStringExtra("playerName");

        createGameButton = findViewById(R.id.createGameButton);
        joinGameButton = findViewById(R.id.joinGameButton);
        gameCodeInput = findViewById(R.id.gameCodeInput);
        progressBar = findViewById(R.id.progressBar);

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + playerName + "!");

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        joinGameButton. setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });
    }

    private void createGame() {
        progressBar.setVisibility(View. VISIBLE);
        createGameButton.setEnabled(false);

        ApiHelper.createGame(playerId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject game) {
                progressBar.setVisibility(View.GONE);

                try {
                    Intent intent = new Intent(LobbyActivity.this, WaitingConnectionActivity.class);
                    intent.putExtra("gameCode", game.getString("game_code"));
                    intent.putExtra("playerId", playerId);
                    intent. putExtra("playerName", playerName);
                    intent.putExtra("isCreator", true);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Toast.makeText(LobbyActivity.this, "Error: " + e.getMessage(), Toast. LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View. GONE);
                createGameButton.setEnabled(true);
                Toast.makeText(LobbyActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinGame() {
        String gameCode = gameCodeInput.getText().toString().trim(). toUpperCase();
        if (gameCode.isEmpty()) {
            Toast.makeText(this, "Please enter game code", Toast.LENGTH_SHORT). show();
            return;
        }

        progressBar. setVisibility(View.VISIBLE);
        joinGameButton.setEnabled(false);

        ApiHelper.joinGame(gameCode, playerId, new ApiHelper. ApiCallback() {
            @Override
            public void onSuccess(JSONObject game) {
                progressBar.setVisibility(View. GONE);

                try {
                    Intent intent = new Intent(LobbyActivity.this, WaitingConnectionActivity.class);
                    intent.putExtra("gameCode", game.getString("game_code"));
                    intent.putExtra("playerId", playerId);
                    intent.putExtra("playerName", playerName);
                    intent.putExtra("isCreator", false);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Toast. makeText(LobbyActivity. this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                joinGameButton.setEnabled(true);
                Toast.makeText(LobbyActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}