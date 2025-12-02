package com.jmccanizares.tictactoe.models;

public class Game {
    private int id;
    private String gameCode;
    private String player1Id;
    private String player2Id;
    private String player1Symbol;
    private String player2Symbol;
    private int player1Score;
    private int player2Score;
    private int drawCount;
    private String currentTurn;
    private String board;
    private String status; // waiting, symbol_selection, playing, finished
    private String winner;
    private boolean showResultDialog;

    public Game() {
        this.board = "_________";
        this. status = "waiting";
        this.player1Score = 0;
        this.player2Score = 0;
        this.drawCount = 0;
        this.showResultDialog = false;
    }

    public Game(String gameCode, String player1Id) {
        this.gameCode = gameCode;
        this.player1Id = player1Id;
        this.board = "_________";
        this.status = "waiting";
        this.player1Score = 0;
        this. player2Score = 0;
        this.drawCount = 0;
        this.showResultDialog = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer1Symbol() {
        return player1Symbol;
    }

    public void setPlayer1Symbol(String player1Symbol) {
        this.player1Symbol = player1Symbol;
    }

    public String getPlayer2Symbol() {
        return player2Symbol;
    }

    public void setPlayer2Symbol(String player2Symbol) {
        this.player2Symbol = player2Symbol;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isShowResultDialog() {
        return showResultDialog;
    }

    public void setShowResultDialog(boolean showResultDialog) {
        this.showResultDialog = showResultDialog;
    }

    public char[] getBoardArray() {
        return board.toCharArray();
    }

    public void setBoardArray(char[] boardArray) {
        this.board = new String(boardArray);
    }
}