package com.example.projecttotal;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Menu_setting_pwchange extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private TextView TextView_ChangePW;
    private TextView TextView_ChangePW_state;
    private EditText EditText_ChangePW;
    private TextView TextView_ChangePWCheck;
    private TextView TextView_ChangePWCheck_state;
    private EditText EditText_ChangePWCheck;
    private Button Button_changeBtn;

    Boolean PWCheck = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_setting_pwchange);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID"); // user ID 정보 받아오기
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userWins");
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);

        TextView_ChangePW = findViewById(R.id.TextView_ChangePW);
        TextView_ChangePW_state = findViewById(R.id.TextView_ChangePW_state);
        EditText_ChangePW = findViewById(R.id.EditText_ChangePW);

        TextView_ChangePWCheck = findViewById(R.id.TextView_ChangePWCheck);
        TextView_ChangePWCheck_state =findViewById(R.id.TextView_ChangePWCheck_state);
        EditText_ChangePWCheck = findViewById(R.id.EditText_ChangePWCheck);
        Button_changeBtn = findViewById(R.id.Button_changeBtn);

        EditText_ChangePW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer cnt = s.length();
                Log.d("inputsize", cnt.toString());
                if (s != null && s.length() >= 8) {
                    TextView_ChangePW_state.setText("사용가능한 비밀번호");
                    TextView_ChangePW_state.setTextColor(0xFF03A9F4);
                    // 중복확인 입력창 활성화
                    setUseableEditText(EditText_ChangePWCheck, true);

                    // 중복확인 입력창과 비교하는 부분
                    if (s.toString().equals(EditText_ChangePWCheck.getText().toString())) {
                        TextView_ChangePWCheck_state.setText("일치합니다!");
                        TextView_ChangePWCheck_state.setTextColor(0xFF03A9F4);
                        PWCheck = true; // 올바르게 비밀번호 입력이 들어왔음을 알림
                    } else {
                        TextView_ChangePWCheck_state.setText("비밀번호가 일치하지 않습니다!");
                        TextView_ChangePWCheck_state.setTextColor(0xFFEC0707);
                        PWCheck = false;
                    }
                } else {
                    TextView_ChangePW_state.setText("8자리 이상 입력!");
                    TextView_ChangePW_state.setTextColor(0xFFEC0707);
                    // 중복확인 입력창 비활성화 및 초기화
                    EditText_ChangePWCheck.setText("");
                    setUseableEditText(EditText_ChangePWCheck, false);
                    PWCheck = false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 비밀번호 중복체크 입력 확인
        EditText_ChangePWCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    TextView_ChangePWCheck_state.setText("");
                    return;
                }
                if (s != null) {
                    Log.d("password", EditText_ChangePW.getText().toString());
                    Log.d("passwordCheck", s.toString());
                    if (s.toString().equals(EditText_ChangePW.getText().toString()))
                    {
                        Log.d("?", "true");
                        TextView_ChangePWCheck_state.setText("일치합니다!");
                        TextView_ChangePWCheck_state.setTextColor(0xFF03A9F4);
                        PWCheck = true; // 올바르게 비밀번호 입력이 들어왔음을 알림
                    } else {
                        TextView_ChangePWCheck_state.setText("비밀번호가 일치하지 않습니다!");
                        TextView_ChangePWCheck_state.setTextColor(0xFFEC0707);
                        PWCheck = false;
                    }
                } else {
                    PWCheck = false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button_changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "0";
                if (PWCheck) { // 올바르게 비밀번호를 입력 한 경우
                    String url = "http://minkyu4626.dothome.co.kr/changePW.php";
                    String newpw = EditText_ChangePWCheck.getText().toString();
                    ChangePWPHP conn = new ChangePWPHP();
                    try {
                        result = conn.execute(url,newpw).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result.equals("1"))
                        show_successPopUp();
                } else {
                    show_errorPopUp();
                }
            }
        });
    }

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
        builder.setTitle("가입 완료!");
        builder.setMessage("비밀변호 변경이 완료 되었습니다.\n닫기를 누르면 로그인화면으로 이동합니다.");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(com.example.projecttotal.Menu_setting_pwchange.this, MainActivity.class);
                startActivity(intent);
            }
        }).show();
    }

    private class ChangePWPHP extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            String output = "0";
            try {
                URL url = new URL(strings[0]);
                String postData = "id=" + userID + "&newpw=" + strings[1];
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
                if (conn != null)  { // 정상연결 된 경우
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                        output = "1";
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;
        }
    }
}
