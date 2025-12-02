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
    private static final String API_BASE_URL = "https://adu-cs.com/jeane_ttt/api.php";

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // Create Game
    public static void createGame(String playerId, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;
            private int responseCode = -1;

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(API_BASE_URL + "?action=create_game");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("player_id", playerId);

                    OutputStream os = conn. getOutputStream();
                    os.write(jsonParam.toString(). getBytes("UTF-8"));
                    os. flush();
                    os.close();

                    responseCode = conn.getResponseCode();
                    Log.d(TAG, "Create Game Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        br.close();

                        Log.d(TAG, "Create Game Response: " + response.toString());
                        return response.toString();
                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn. getErrorStream(), "UTF-8"));
                        StringBuilder error = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            error.append(line);
                        }
                        br.close();
                        Log.e(TAG, "Create Game Error Response: " + error.toString());
                        exception = new Exception("HTTP " + responseCode + ": " + error.toString());
                        return null;
                    }
                } catch (Exception e) {
                    exception = e;
                    Log. e(TAG, "Error creating game", e);
                    return null;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
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
                        callback.onError("JSON parsing error: " + e.getMessage() + "\nResponse: " + result);
                    }
                } else {
                    String errorMsg = "Network error";
                    if (exception != null) {
                        errorMsg = exception.getMessage();
                    } else if (responseCode != -1) {
                        errorMsg = "HTTP " + responseCode;
                    }
                    callback.onError(errorMsg);
                }
            }
        }. execute();
    }

    // Join Game
    public static void joinGame(String gameCode, String playerId, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;
            private int responseCode = -1;

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(API_BASE_URL + "?action=join_game");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(15000);
                    conn. setReadTimeout(15000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("game_code", gameCode);
                    jsonParam.put("player_id", playerId);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.flush();
                    os. close();

                    responseCode = conn.getResponseCode();
                    Log.d(TAG, "Join Game Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        br.close();

                        Log. d(TAG, "Join Game Response: " + response.toString());
                        return response.toString();
                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        StringBuilder error = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            error.append(line);
                        }
                        br.close();
                        Log.e(TAG, "Join Game Error: " + error.toString());
                        exception = new Exception("HTTP " + responseCode + ": " + error.toString());
                        return null;
                    }
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error joining game", e);
                    return null;
                } finally {
                    if (conn != null) {
                        conn. disconnect();
                    }
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
                    String errorMsg = "Network error";
                    if (exception != null) {
                        errorMsg = exception.getMessage();
                    } else if (responseCode != -1) {
                        errorMsg = "HTTP " + responseCode;
                    }
                    callback.onError(errorMsg);
                }
            }
        }.execute();
    }

    // Get Game
    public static void getGame(String gameCode, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;
            private int responseCode = -1;

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(API_BASE_URL + "?action=get_game&game_code=" + gameCode);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(15000);
                    conn. setReadTimeout(15000);

                    responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response. append(line);
                        }
                        br.close();
                        return response.toString();
                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        StringBuilder error = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            error.append(line);
                        }
                        br. close();
                        Log.e(TAG, "Get Game Error: " + error.toString());
                        exception = new Exception("HTTP " + responseCode + ": " + error.toString());
                        return null;
                    }
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error getting game", e);
                    return null;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
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
                    String errorMsg = "Network error";
                    if (exception != null) {
                        errorMsg = exception.getMessage();
                    } else if (responseCode != -1) {
                        errorMsg = "HTTP " + responseCode;
                    }
                    callback. onError(errorMsg);
                }
            }
        }.execute();
    }

    // Update Game
    public static void updateGame(JSONObject gameData, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private Exception exception;
            private int responseCode = -1;
            private String requestData = "";

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection conn = null;
                try {
                    requestData = gameData.toString();
                    Log.d(TAG, "Update Game Request: " + requestData);

                    URL url = new URL(API_BASE_URL + "?action=update_game");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn. setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(gameData.toString().getBytes("UTF-8"));
                    os. flush();
                    os.close();

                    responseCode = conn.getResponseCode();
                    Log.d(TAG, "Update Game Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response. append(line);
                        }
                        br.close();

                        Log.d(TAG, "Update Game Response: " + response.toString());
                        return response.toString();
                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                        StringBuilder error = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            error.append(line);
                        }
                        br.close();
                        Log.e(TAG, "Update Game Error Response: " + error.toString());
                        exception = new Exception("HTTP " + responseCode + ": " + error.toString());
                        return null;
                    }
                } catch (Exception e) {
                    exception = e;
                    Log.e(TAG, "Error updating game.  Request was: " + requestData, e);
                    return null;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
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
                            String errorMsg = json.optString("error", "Unknown error");
                            Log.e(TAG, "Update failed: " + errorMsg);
                            callback.onError(errorMsg);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error: " + e.getMessage() + "\nResponse: " + result);
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    String errorMsg = "Network error";
                    if (exception != null) {
                        errorMsg = exception.getMessage();
                    } else if (responseCode != -1) {
                        errorMsg = "HTTP " + responseCode;
                    }
                    Log.e(TAG, "Update game final error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }
        }.execute();
    }
}