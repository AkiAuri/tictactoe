package com.jmccanizares.tictactoe.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    private static final String TAG = "ApiHelper";
    private static final String API_URL = "https://srv2054-files.hstgr.io/fc3efc1e54edec0c/files/public_html/jeane_ttt/api.php";

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // Create Game
    public static void createGame(String playerId, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(API_URL + "?action=create_game");
                    HttpURLConnection conn = (HttpURLConnection) url. openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("player_id", playerId);

                    OutputStream os = conn. getOutputStream();
                    os.write(jsonParam.toString(). getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    return response.toString();
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error creating game: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("success") && json.getBoolean("success")) {
                            callback.onSuccess(json. getJSONObject("game"));
                        } else {
                            callback.onError(json.optString("error", "Unknown error"));
                        }
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    callback.onError(exception != null ? exception.getMessage() : "Network error");
                }
            }
        }.execute();
    }

    // Join Game
    public static void joinGame(String gameCode, String playerId, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(API_URL + "?action=join_game");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("game_code", gameCode);
                    jsonParam.put("player_id", playerId);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam. toString().getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    return response.toString();
                } catch (Exception e) {
                    exception = e;
                    Log. e(TAG, "Error joining game: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json. has("success") && json.getBoolean("success")) {
                            callback.onSuccess(json. getJSONObject("game"));
                        } else {
                            callback.onError(json.optString("error", "Unknown error"));
                        }
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    callback.onError(exception != null ? exception. getMessage() : "Network error");
                }
            }
        }. execute();
    }

    // Get Game
    public static void getGame(String gameCode, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(API_URL + "?action=get_game&game_code=" + gameCode);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    return response.toString();
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error getting game: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("success") && json. getBoolean("success")) {
                            callback.onSuccess(json.getJSONObject("game"));
                        } else {
                            callback.onError(json. optString("error", "Unknown error"));
                        }
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e. getMessage());
                    }
                } else {
                    callback. onError(exception != null ? exception.getMessage() : "Network error");
                }
            }
        }.execute();
    }

    // Update Game
    public static void updateGame(JSONObject gameData, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(API_URL + "?action=update_game");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(gameData.toString().getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br. close();

                    return response.toString();
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error updating game: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("success") && json.getBoolean("success")) {
                            callback.onSuccess(json.getJSONObject("game"));
                        } else {
                            callback.onError(json.optString("error", "Unknown error"));
                        }
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    callback.onError(exception != null ?  exception.getMessage() : "Network error");
                }
            }
        }.execute();
    }
}