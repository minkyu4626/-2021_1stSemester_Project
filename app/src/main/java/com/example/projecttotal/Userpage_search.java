package com.example.projecttotal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Userpage_search extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private ImageButton ImageButton_user;
    private ImageButton ImageButton_chat;
    private ImageButton ImageButton_setting;
    private WebView WebView_opgg;
    private WebSettings WebView_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_search);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userLoses");
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);
        // 이미지버튼 뷰 받아오기
        ImageButton_user = findViewById(R.id.ImageButton_user);
        ImageButton_chat = findViewById(R.id.ImageButton_chat);
        ImageButton_setting = findViewById(R.id.ImageButton_setting);
        WebView_opgg = (WebView)findViewById(R.id.WebView_opgg);
        // 웹뷰 시작
        WebView_opgg.setWebViewClient(new WebViewClient());
        WebView_setting = WebView_opgg.getSettings();
        WebView_setting.setJavaScriptEnabled(true);
        WebView_setting.setLoadWithOverviewMode(true);
        WebView_setting.setUseWideViewPort(true);
        WebView_setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        WebView_setting.setCacheMode(WebView_setting.LOAD_NO_CACHE);
        WebView_setting.setDomStorageEnabled(true);
        WebView_opgg.loadUrl("https://www.op.gg/");
        // 클릭이벤트 구현
        ImageButton_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_search.this, Userpage_finduser.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        ImageButton_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_search.this, Userpage_chat.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        ImageButton_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_search.this, Userpage_setting.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
    }

    // WebView 뒤로가기 및 앱 종료 구현
    private int backBtnTime = 0;
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (WebView_opgg.canGoBack()) { // 웹뷰에서 뒤로가기가 가능한 경우
            WebView_opgg.goBack();
        } else { // 더 이상 뒤로가기가 불가능 한 경우
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("앱을 종료하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            builder.setTitle("프로그램 종료");
            builder.show();
        }
    }
}