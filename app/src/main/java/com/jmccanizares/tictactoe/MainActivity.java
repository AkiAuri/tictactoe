package com.jmccanizares.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget. Toast;

import androidx.appcompat. app.AppCompatActivity;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private EditText playerNameInput;
    private Button startButton;
    private String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_main);

        playerNameInput = findViewById(R. id.playerNameInput);
        startButton = findViewById(R.id.startButton);

        // Get or create player ID
        SharedPreferences prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
        playerId = prefs. getString("playerId", null);
        if (playerId == null) {
            playerId = UUID.randomUUID().toString();
            prefs.edit().putString("playerId", playerId).apply();
        }

        String savedName = prefs.getString("playerName", "");
        playerNameInput.setText(savedName);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = playerNameInput.getText().toString(). trim();
                if (playerName.isEmpty()) {
                    Toast.makeText(MainActivity. this, "Please enter your name", Toast.LENGTH_SHORT). show();
                    return;
                }

                // Save player name
                prefs.edit().putString("playerName", playerName).apply();

                // Go to lobby
                Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                intent.putExtra("playerId", playerId);
                intent.putExtra("playerName", playerName);
                startActivity(intent);
            }
        });
    }
}