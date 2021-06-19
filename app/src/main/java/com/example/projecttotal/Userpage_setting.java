package com.example.projecttotal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Userpage_setting extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private ImageButton ImageButton_user;
    private ImageButton ImageButton_chat;
    private ImageButton ImageButton_search;
    private TextView TextView_PWChange;
    private TextView TextView_SummonerChange;
    private TextView TextView_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_setting);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userLoses");
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);
        // 뷰 받아오기
        ImageButton_user = findViewById(R.id.ImageButton_user);
        ImageButton_chat = findViewById(R.id.ImageButton_chat);
        ImageButton_search = findViewById(R.id.ImageButton_search);
        TextView_PWChange = findViewById(R.id.TextView_PWChange);
        TextView_SummonerChange = findViewById(R.id.TextView_SummonerChange);
        TextView_logout = findViewById(R.id.TextView_logout);
        // 클릭이벤트 구현
        ImageButton_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_setting.this, Userpage_finduser.class);
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
                Intent intent = new Intent(Userpage_setting.this, Userpage_chat.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        ImageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_setting.this, Userpage_search.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });

        // 메뉴 내의 버튼 클릭 이벤트 구현
        TextView_PWChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_setting.this, Menu_setting_pwchange.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        TextView_SummonerChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_setting.this, Menu_setting_summonerchange.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        TextView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    public void show() {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("로그아웃");
        builder2.setMessage("로그아웃하시겠습니까?");
        builder2.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Userpage_setting.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder2.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder2.show();
    }

}
