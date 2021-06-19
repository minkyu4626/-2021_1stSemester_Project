package com.example.projecttotal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Menu_setting_summonerchange extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private Boolean inputCheck = false;
    TextView TextView_summonerName_check;
    EditText EditText_summonerName;
    Button Button_summonerName_check;
    Button Button_changeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_setting_summonerchagne);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userWins");
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);

        TextView_summonerName_check = findViewById(R.id.TextView_summonerName_check);
        EditText_summonerName = findViewById(R.id.EditText_summonerName);
        Button_summonerName_check = findViewById(R.id.Button_summonerName_check);
        Button_changeBtn = findViewById(R.id.Button_changeBtn);

        Button_summonerName_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EditText_summonerName.getText() == null) {
                    TextView_summonerName_check.setText("입력하세요!");
                    TextView_summonerName_check.setTextColor(0xFFEC0707);
                    return;
                } else {
                    if (isExistSummoner(EditText_summonerName.getText().toString())) {
                        TextView_summonerName_check.setText("존재하는 소환사명!");
                        TextView_summonerName_check.setTextColor(0xFF03A9F4);
                        // 소환사명이 결정됬으므로 입력창과 확인 버튼 다시 클릭 못하도록 변경
                        setUseableEditText(EditText_summonerName, false);
                        Button_summonerName_check.setClickable(false);
                        inputCheck = true; // 소환사명이 올바르게 입력 됬음을 알림!
                    } else {
                        TextView_summonerName_check.setText("존재하지 않는 소환사명!");
                        TextView_summonerName_check.setTextColor(0xFFEC0707);
                    }
                }
            }
        });

        Button_changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String summoner = EditText_summonerName.getText().toString();
                if (inputCheck) {
                    PhpCommunicate conn = new PhpCommunicate();
                    conn.execute("http://minkyu4626.dothome.co.kr/changeSummoner.php", summoner);
                    show_successPopUp();
                }
                else
                    show_errorPopUp();
            }
        });
    }

    // lolName이 존재하는 소환사명인지 확인하는 함수
    private Boolean isExistSummoner(String name) {
        PhpCommunicate PHP = new PhpCommunicate();
        String requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"
                + name + "?api_key=RGAPI-b443a628-6cdf-4bd8-abd0-fa2f39a07580";
        String returnValue = "";
        try {
            returnValue = PHP.execute(requestURL).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (returnValue != null && returnValue.equals("1")) {
                return true;
            } else {
                return false;
            }
        }
    }
    // EditText의 상태(입력 가능/불가능) 변경하는 함수
    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }

    private void show_errorPopUp() { // 에러 팝업을 띄우는 함수
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error!");
        builder.setMessage("모든 내용을 올바르게 입력하세요!\n");
        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void show_successPopUp() { // pw 변경 완료을 알리는 팝업을 띄우는 함수
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("변경 완료!");
        builder.setMessage("소환사 정보변경이 완료 되었습니다.\n닫기를 누르면 로그인화면으로 이동합니다.");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(com.example.projecttotal.Menu_setting_summonerchange.this, MainActivity.class);
                startActivity(intent);
            }
        }).show();
    }
    private class PhpCommunicate extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            String output = "0";
            try {
                // 연결 url 설정
                URL url = new URL(strings[0]);
                // connection 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if (strings[0].contains("http://minkyu4626.dothome.co.kr")) { // 서버 php와 통신하는 경우
                    String postData = "id=" + userID + "&newsummoner=" + strings[1];
                    Log.d("postData", postData);
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.connect();
                    // 데이터 전송
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                }
                // 연결 되었으면?
                if (conn != null) {
                    // 연결된 코드가 리턴되게 되면
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        output = "1";
                        Log.d("debug", output);
                    }
                    conn.disconnect();
                } else {
                    Log.d("debug", "연결 실패");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return output;
        }
    }
}
