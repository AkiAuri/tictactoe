package com.jmccanizares.tictactoe;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jmccanizares.tictactoe.api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {
    private Button[] boardButtons = new Button[9];
    private TextView player1ScoreText;
    private TextView player2ScoreText;
    private TextView drawScoreText;
    private LinearLayout player1Indicator;
    private LinearLayout player2Indicator;
    private TextView player1SymbolText;
    private TextView player2SymbolText;
    private Button refreshButton;

    private String gameCode;
    private String playerId;
    private String playerName;
    private String player1Id;
    private String player2Id;

    private JSONObject currentGame;
    private Handler pollHandler;
    private Runnable pollRunnable;

    private String playerSymbol;
    private String opponentSymbol;
    private Dialog resultDialog;
    private boolean dialogShownForCurrentRound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameCode = getIntent().getStringExtra("gameCode");
        playerId = getIntent().getStringExtra("playerId");
        playerName = getIntent().getStringExtra("playerName");
        player1Id = getIntent().getStringExtra("player1Id");
        player2Id = getIntent().getStringExtra("player2Id");

        String player1Symbol = getIntent().getStringExtra("player1Symbol");
        String player2Symbol = getIntent().getStringExtra("player2Symbol");

        // Determine player symbol based on player ID
        if (playerId.equals(player1Id)) {
            playerSymbol = player1Symbol;
            opponentSymbol = player2Symbol;
        } else {
            playerSymbol = player2Symbol;
            opponentSymbol = player1Symbol;
        }

        initializeViews(player1Symbol, player2Symbol);

        // Start polling for game updates
        startPolling();
    }

    private void initializeViews(String player1Symbol, String player2Symbol) {
        player1ScoreText = findViewById(R.id.player1ScoreText);
        player2ScoreText = findViewById(R.id.player2ScoreText);
        drawScoreText = findViewById(R.id.drawScoreText);
        player1Indicator = findViewById(R.id.player1Indicator);
        player2Indicator = findViewById(R.id.player2Indicator);
        player1SymbolText = findViewById(R.id.player1SymbolText);
        player2SymbolText = findViewById(R.id.player2SymbolText);
        refreshButton = findViewById(R.id.refreshButton);

        player1SymbolText.setText(player1Symbol);
        player2SymbolText.setText(player2Symbol);

        // Initialize board buttons
        boardButtons[0] = findViewById(R.id.button0);
        boardButtons[1] = findViewById(R.id.button1);
        boardButtons[2] = findViewById(R.id.button2);
        boardButtons[3] = findViewById(R.id.button3);
        boardButtons[4] = findViewById(R.id.button4);
        boardButtons[5] = findViewById(R.id.button5);
        boardButtons[6] = findViewById(R.id.button6);
        boardButtons[7] = findViewById(R.id.button7);
        boardButtons[8] = findViewById(R.id.button8);

        for (int i = 0; i < 9; i++) {
            final int index = i;
            boardButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCellClicked(index);
                }
            });
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameActivity.this, "Refreshing.. .", Toast.LENGTH_SHORT).show();
                forceRefresh();
            }
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton. setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onCellClicked(int position) {
        if (currentGame == null) return;

        try {
            // Check if it's player's turn
            String currentTurn = currentGame.getString("current_turn");
            if (! currentTurn.equals(playerId)) {
                Toast.makeText(this, "Not your turn!", Toast. LENGTH_SHORT).show();
                return;
            }

            // Check if game is still playing
            String status = currentGame.getString("status");
            if (!"playing".equals(status)) {
                Toast.makeText(this, "Game is over!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if cell is empty
            String board = currentGame.getString("board");
            char[] boardArray = board.toCharArray();

            if (boardArray[position] != '_') {
                Toast.makeText(this, "Cell already taken!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make move
            boardArray[position] = playerSymbol.charAt(0);
            currentGame.put("board", new String(boardArray));

            // Check for win or draw
            String winner = checkWinner(boardArray);
            if (winner != null) {
                currentGame.put("status", "finished");

                if (winner.equals("Draw")) {
                    currentGame.put("winner", "Draw");
                    int drawCount = currentGame.getInt("draw_count");
                    currentGame.put("draw_count", drawCount + 1);
                } else if (winner.equals(playerSymbol)) {
                    currentGame.put("winner", playerId);
                    if (playerId.equals(player1Id)) {
                        int score = currentGame.getInt("player1_score");
                        currentGame.put("player1_score", score + 1);
                    } else {
                        int score = currentGame.getInt("player2_score");
                        currentGame.put("player2_score", score + 1);
                    }
                }

                currentGame.put("show_result_dialog", true);
                dialogShownForCurrentRound = false;
            } else {
                // Switch turn
                String nextTurn = playerId.equals(player1Id) ? player2Id : player1Id;
                currentGame.put("current_turn", nextTurn);
            }

            // Update game in database
            ApiHelper.updateGame(currentGame, new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(JSONObject game) {
                    currentGame = game;
                    updateUI(game);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(GameActivity.this, "Error updating game: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(this, "Error: " + e. getMessage(), Toast.LENGTH_SHORT). show();
        }
    }

    private void startPolling() {
        pollHandler = new Handler();
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                ApiHelper. getGame(gameCode, new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject game) {
                        currentGame = game;
                        updateUI(game);

                        // Continue polling
                        pollHandler.postDelayed(pollRunnable, 1000);
                    }

                    @Override
                    public void onError(String error) {
                        // Retry polling
                        pollHandler.postDelayed(pollRunnable, 2000);
                    }
                });
            }
        };
        pollHandler.post(pollRunnable);
    }

    private void forceRefresh() {
        ApiHelper.getGame(gameCode, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject game) {
                currentGame = game;
                updateUI(game);
                Toast.makeText(GameActivity.this, "Refreshed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GameActivity.this, "Error refreshing: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(JSONObject game) {
        try {
            String board = game.getString("board");
            char[] boardArray = board. toCharArray();

            // Update board buttons
            for (int i = 0; i < 9; i++) {
                if (boardArray[i] == '_') {
                    boardButtons[i].setText("");
                } else {
                    boardButtons[i].setText(String.valueOf(boardArray[i]));
                }
            }

            // Update scores
            player1ScoreText.setText(String.valueOf(game.getInt("player1_score")));
            player2ScoreText.setText(String.valueOf(game.getInt("player2_score")));
            drawScoreText.setText(String.valueOf(game.getInt("draw_count")));

            // Update turn indicators
            String status = game.getString("status");
            if ("playing".equals(status)) {
                String currentTurn = game.getString("current_turn");
                if (currentTurn.equals(player1Id)) {
                    player1Indicator.setBackgroundColor(Color.parseColor("#4CAF50"));
                    player2Indicator.setBackgroundColor(Color.parseColor("#CCCCCC"));
                } else {
                    player1Indicator. setBackgroundColor(Color.parseColor("#CCCCCC"));
                    player2Indicator.setBackgroundColor(Color.parseColor("#FF9800"));
                }
            }

            // Show result dialog if game is finished
            boolean showDialog = game.optBoolean("show_result_dialog", false);
            if ("finished".equals(status) && showDialog && !dialogShownForCurrentRound) {
                dialogShownForCurrentRound = true;
                showResultDialog(game);
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Error updating UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showResultDialog(JSONObject game) {
        if (resultDialog != null && resultDialog.isShowing()) {
            return;
        }

        resultDialog = new Dialog(this);
        resultDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resultDialog.setContentView(R.layout.dialog_game_result);
        resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color. TRANSPARENT));
        resultDialog.setCancelable(false);

        TextView resultText = resultDialog.findViewById(R.id.resultText);
        TextView resultMessage = resultDialog.findViewById(R.id.resultMessage);
        Button clearBoardButton = resultDialog.findViewById(R.id.clearBoardButton);

        String resultString = "";
        String messageString = "";

        try {
            String winner = game.optString("winner", null);

            if ("Draw".equals(winner)) {
                resultString = "DRAW! ";
                messageString = "It's a tie!";
                resultText.setTextColor(getResources().getColor(R.color.draw_color));
            } else if (winner != null && winner.equals(playerId)) {
                resultString = "YOU WIN!";
                messageString = "🎉 Congratulations! 🎉";
                resultText.setTextColor(getResources().getColor(R.color. win_color));
            } else {
                resultString = "YOU LOSE!";
                messageString = "Better luck next time!";
                resultText.setTextColor(getResources().getColor(R.color.lose_color));
            }

            resultText.setText(resultString);
            resultMessage.setText(messageString);

        } catch (Exception e) {
            e.printStackTrace();
        }

        clearBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBoard();
                resultDialog.dismiss();
            }
        });

        resultDialog.show();
    }

    private void clearBoard() {
        if (currentGame == null) return;

        try {
            // Reset board
            currentGame.put("board", "_________");
            currentGame.put("status", "playing");
            currentGame.put("winner", JSONObject.NULL);
            currentGame.put("show_result_dialog", false);
            dialogShownForCurrentRound = false;

            // Update in database
            ApiHelper.updateGame(currentGame, new ApiHelper. ApiCallback() {
                @Override
                public void onSuccess(JSONObject game) {
                    currentGame = game;
                    updateUI(game);
                    Toast.makeText(GameActivity. this, "Board cleared!", Toast. LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(GameActivity.this, "Error clearing board: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast. LENGTH_SHORT).show();
        }
    }

    private String checkWinner(char[] board) {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (board[i] != '_' && board[i] == board[i+1] && board[i] == board[i+2]) {
                return String.valueOf(board[i]);
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[i] != '_' && board[i] == board[i+3] && board[i] == board[i+6]) {
                return String.valueOf(board[i]);
            }
        }

        // Check diagonals
        if (board[0] != '_' && board[0] == board[4] && board[0] == board[8]) {
            return String.valueOf(board[0]);
        }
        if (board[2] != '_' && board[2] == board[4] && board[2] == board[6]) {
            return String.valueOf(board[2]);
        }

        // Check for draw
        boolean isFull = true;
        for (char c : board) {
            if (c == '_') {
                isFull = false;
                break;
            }
        }
        if (isFull) {
            return "Draw";
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollHandler != null && pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
        if (resultDialog != null && resultDialog.isShowing()) {
            resultDialog.dismiss();
        }
    }
}