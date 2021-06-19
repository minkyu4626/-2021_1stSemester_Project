package com.example.projecttotal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate; // 유저의 승률
    private int userWins, userLoses;

    private SummnoerInfo summnoerInfo;

    // 위젯들
    private Button Button_loginPage_login;
    private Button Button_loginPage_singUp;
    private EditText EditText_loginPage_IDInput;
    private EditText EditText_loginPage_PasswordInput;
    private TextView TextView_loginPage_validUser;

    private Boolean notNullInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // 위젯 불러오기
        Button_loginPage_login = findViewById(R.id.Button_loginPage_login);
        Button_loginPage_singUp = findViewById(R.id.Button_loginPage_singUp);
        EditText_loginPage_IDInput = findViewById(R.id.EditText_loginPage_IDInput);
        EditText_loginPage_PasswordInput = findViewById(R.id.EditText_loginPage_PasswordInput);
        TextView_loginPage_validUser = findViewById(R.id.TextView_loginPage_validUser);

        // 입력 된 아이디가 데이터베이스에 존재하는 아이디인지 확인하고
        // 1) 존재하는 경우
        //    입력 된 비밀번호가 일치하는치 확인한다.
        //    일치하는 경우 로그인이 되어 데이터베이스로부터 회원정보를 받아 다음 intant로 넘어갈 때 같이 넘긴다.
        //    일치하지 않는 경우 textview에 "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다!"를 띄움
        // 2) 존재하지 않는 경우
        //    textview에 "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다!" 를 띄운다"

        EditText_loginPage_IDInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    Log.d("input: ", s.toString());
                    notNullInput = true;
                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        EditText_loginPage_PasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    notNullInput = true;
                    Log.d("input: ", s.toString());
                } else {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Button_loginPage_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input이 null인 경우 아래의 toString()함수에서 버그가 발생하므로 notNullInput 변수로 미리 체크한다.
                if (!notNullInput) {
                    TextView_loginPage_validUser.setText("ID, Password를 입력하세요!");
                    return;
                }

                // 각각 ID, password 값을 가지고 있는 변수
                userID = EditText_loginPage_IDInput.getText().toString();
                String Password = EditText_loginPage_PasswordInput.getText().toString();
                // 서버에서 아이디 비번을 확인하여 일치하는 회원이 존재하고 비번이 맞을 때만 로그인이 되게,
                // 아닌 경우에는 "존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다!" 출력
                if (isCorrectMemberInfo(userID, Password)) { // 존재하는 회원이고 pw도 맞는 경우
                    // 회원 정보(소환사명을 얻어와서 intent에 포함시켜서 실행시킨다)
                    summnoerInfo = new SummnoerInfo(userSummoner);
                    summnoerInfo.setInfo();
                    userTier = summnoerInfo.getTier();
                    userWinRate = String.valueOf(summnoerInfo.getWinRate()) + "%";
                    userWins = summnoerInfo.getWin();
                    userLoses = summnoerInfo.getLose();
                    Log.d("유저의 승률", userWinRate);
                    // 새로 받아온 유저 정보들을 통해서 자신이 쓴 글 정보 업데이트 (소환사명, 티어)
                    updateBoardData();
                    Log.d("userTier is ", userTier);
                    Intent intent = new Intent(MainActivity.this, Userpage_finduser.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userSummoner", userSummoner);
                    intent.putExtra("userTier", userTier);
                    intent.putExtra("userWinRate", userWinRate);
                    intent.putExtra("userWins", userWins);
                    intent.putExtra("userLoses", userLoses);
                    startActivity(intent);
                } else { // 존재하지 않는 회원이거나 pw가 틀린 경우
                    TextView_loginPage_validUser.setText("존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다!!");
                }
            }
        });

        // sing up button 클릭 시 화면 전환 구현
        Button_loginPage_singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SingUpPage_Activity.class);
                startActivity(intent);
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
    boolean isCorrectMemberInfo(String id, String pw) { // 입력한 아이디가 존재하는 회원인지, 맞다면 맞는 비밀번호인지 확인하는 함수
        String returnValue = "";
        String url = "http://minkyu4626.dothome.co.kr/isCorrectMemberInfo.php";
        ServerPHP conn = new ServerPHP();
        try {
            returnValue = conn.execute(url, id, pw).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (returnValue.substring(0,1).equals("1")) {
            userSummoner = returnValue.substring(1);
            return true;
        }
        else
            return false;
    }

    private class ServerPHP extends AsyncTask<String, String ,String> {

        @Override
        protected String doInBackground(String... strings) {
            String output = "";
            if (strings[0].contains("isCorrectMemberInfo.php")){
                try {
                    URL url = new URL(strings[0]); // 연결 URL
                    String postData = "id=" + strings[1] + "&pw=" + strings[2];
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    // 데이터 전송
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    if (conn != null) { // 정상 연결 된 경우
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line = br.readLine();
                            output += line;
                            Log.d("php 출력", line);
                            br.close();
                        }
                        else {
                            Log.d("php error!", "error");
                        }
                        conn.disconnect();
                    } else {
                        Log.d("debug", "연결실패!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (strings[0].contains("updateBoardTier.php")) {
                try {
                    URL url = new URL(strings[0]); // 연결 URL
                    Log.d("연결유알엘", strings[0]);
                    String postData = "id=" + userID + "&tier=" + userTier;
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    // 데이터 전송
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    if (conn != null) { // 정상 연결 된 경우
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {}
                        else { Log.d("php error!", "error"); }
                        conn.disconnect();
                    } else { Log.d("debug", "연결실패!"); }
                } catch (Exception e) { e.printStackTrace(); }
            }
            return output;
        }
    }

    private void updateBoardData() {
        String serverURL = "http://minkyu4626.dothome.co.kr/updateBoardTier.php?summoner="+userSummoner+"&winRate="+userWinRate; // POST 방식의 오류? 때문에 3개부터 전달 안되어 GET 으로 전달
        ServerPHP conn = new ServerPHP();
        conn.execute(serverURL);
    }
}