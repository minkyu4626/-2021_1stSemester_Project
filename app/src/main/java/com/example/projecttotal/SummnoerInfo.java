package com.example.projecttotal;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SummnoerInfo {
    private String APIKey = "RGAPI-b443a628-6cdf-4bd8-abd0-fa2f39a07580";
    private String summonerName;
    private String tier; private int win = 0; private int lose = 0;
    SummnoerInfo(String summonerName) {
        this.summonerName = summonerName;
    }

    private class ServerPHP extends AsyncTask<String, String, String> {
        String result = "";
        @Override
        protected String doInBackground(String... strings) {
            String jsonString = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(5000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = br.readLine();
                        jsonString += line;
                        br.close();
                    }
                    conn.disconnect();
                } else {
                    Log.d("에러!", "연결실패!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strings[0].contains("https://kr.api.riotgames.com/lol/league")) {
                jsonParsing_setUserInfo(jsonString);
            }
            else if (strings[0].contains("https://kr.api.riotgames.com/lol/summoner")) {
                result = jsonParsing_getSummnoerID(jsonString);
            }
            return result;
        }
    }

    private void jsonParsing_setUserInfo(String jsonString) {
        try {
            JSONArray jsonArr = new JSONArray(jsonString);
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                if (jsonObj.getString("queueType").equals("RANKED_SOLO_5x5")) {
                    tier = jsonObj.getString("tier");
                    win = Integer.parseInt(jsonObj.getString("wins"));
                    lose = Integer.parseInt(jsonObj.getString("losses"));
                }
            }
            Log.d("tier", tier);
            Log.d("win", String.valueOf(win));
            Log.d("lose", String.valueOf(lose));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String jsonParsing_getSummnoerID(String jsonString) {
        String summonerID = "";
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            summonerID = jsonObj.getString("id");
            Log.d("summonerID", summonerID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summonerID;
    }
    private String getSummonerID(String summonerName) {
        String result = "";
        String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"
                + summonerName + "?api_key=RGAPI-b443a628-6cdf-4bd8-abd0-fa2f39a07580";
        ServerPHP conn = new ServerPHP();
        try {
            result = conn.execute(requestURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setInfo() {
        String summonerID = getSummonerID(summonerName);
        String requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/"
                + summonerID + "/?api_key=" + APIKey;
        ServerPHP conn = new ServerPHP();
        try {
            conn.execute(requestURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Tier", tier);
        Log.d("win", String.valueOf(win));
        Log.d("lose", String.valueOf(lose));
    }

    public String getTier() {return tier;}

    public double getWinRate() {
        return Math.round((double)win/(win+lose)*10000)/100.0;
    }

    public int getWin() { return win;}
    public int getLose() { return lose;}
}