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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingUpPage_Activity extends AppCompatActivity {
    // 서버 php 파일의 결과를 받아오기 위한 인스턴스
    PhpCommunicate PHP;

    // 불러와야 하는 위젯들
    EditText EditText_ID;
    EditText EditText_Password;
    EditText EditText_PasswordCheck;
    EditText EditText_lolName;
    Button Button_ID_check;
    Button Button_lolName_check;
    Button Button_signUpPage_signUp;
    TextView TextView_ID_check;
    TextView TextView_PasswordCheck_check;
    TextView TextView_lolName_check;
    TextView TextView_Password_valid;

    //내부에서 사용 되는 변수들
    // 각각 입력들이 올바르게 들어왔는지 checking 할 수 있게 해주는 변수들
    private Boolean IDCheck = false;
    private Boolean PWCheck = false;
    private Boolean lolCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singup_page);
        Intent intent = getIntent();

        // 위젯들 불러오기
        EditText_ID = findViewById(R.id.EditText_ID);
        EditText_Password = findViewById(R.id.EditText_Password);
        EditText_PasswordCheck = findViewById(R.id.EditText_PasswordCheck);
        EditText_lolName = findViewById(R.id.EditText_lolName);
        TextView_ID_check = findViewById(R.id.TextView_ID_check);
        TextView_PasswordCheck_check = findViewById(R.id.TextView_PasswordCheck_check);
        TextView_lolName_check = findViewById(R.id.TextView_lolName_check);
        TextView_Password_valid = findViewById(R.id.TextView_Password_valid);
        Button_ID_check = findViewById(R.id.Button_ID_check);
        Button_lolName_check = findViewById(R.id.Button_lolName_check);
        Button_signUpPage_signUp = findViewById(R.id.Button_signUpPage_signUp);

        // ID 중복체크 버튼의 클릭시 동작 구현부
        Button_ID_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 ID 얻어온다
                String inputID = EditText_ID.getText().toString();
                if (inputID == null || inputID.equals("")) {
                    TextView_ID_check.setTextColor(0xFFEC0707);
                    TextView_ID_check.setText("입력하세요");
                    return;
                }
                // 중복되는 아이디인지 아닌지 확인
                if (!isDuplicatedID(inputID)) { // 중복 X 인 경우
                    TextView_ID_check.setText("사용가능한 아이디입니다!");
                    TextView_ID_check.setTextColor(0xFF03A9F4);
                    // 사용할 아이디를 결정한 것으로 보고 중복확인 및 ID 입력창 클릭 못하도록 설정
                    setUseableEditText(EditText_ID, false);
                    Button_ID_check.setClickable(false);
                    IDCheck = true; // 올바른 ID가 입력 됨을 IDCheck를 true로 저장하여 알림
                } else { // 중복되는 경우
                    IDCheck = false;
                    TextView_ID_check.setText("이미 존재하는 아이디입니다!");
                    TextView_ID_check.setTextColor(0xFFEC0707);
                }
            }
        });

        // 비밀번호 입력 체크
        EditText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer cnt = s.length();
                Log.d("inputsize", cnt.toString());
                if (s != null && s.length() >= 8) {
                    TextView_Password_valid.setText("사용가능한 비밀번호");
                    TextView_Password_valid.setTextColor(0xFF03A9F4);
                    // 중복확인 입력창 활성화
                    setUseableEditText(EditText_PasswordCheck, true);

                    // 중복확인 입력창과 비교하는 부분
                    if (s.toString().equals(EditText_PasswordCheck.getText().toString())) {
                        TextView_PasswordCheck_check.setText("일치합니다!");
                        TextView_PasswordCheck_check.setTextColor(0xFF03A9F4);
                        PWCheck = true; // 올바르게 비밀번호 입력이 들어왔음을 알림
                    } else {
                        TextView_PasswordCheck_check.setText("비밀번호가 일치하지 않습니다!");
                        TextView_PasswordCheck_check.setTextColor(0xFFEC0707);
                        PWCheck = false;
                    }
                } else {
                    TextView_Password_valid.setText("8자리 이상 입력!");
                    TextView_Password_valid.setTextColor(0xFFEC0707);
                    // 중복확인 입력창 비활성화 및 초기화
                    EditText_PasswordCheck.setText("");
                    setUseableEditText(EditText_PasswordCheck, false);
                    PWCheck = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 비밀번호 중복체크 입력 확인
        EditText_PasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) {
                    TextView_PasswordCheck_check.setText("");
                    return;
                }
                if (s != null) {
                    Log.d("password", EditText_Password.getText().toString());
                    Log.d("passwordCheck", s.toString());
                    if (s.toString().equals(EditText_Password.getText().toString()))
                    {
                        Log.d("?", "true");
                        TextView_PasswordCheck_check.setText("일치합니다!");
                        TextView_PasswordCheck_check.setTextColor(0xFF03A9F4);
                        PWCheck = true; // 올바르게 비밀번호 입력이 들어왔음을 알림
                    } else {
                        TextView_PasswordCheck_check.setText("비밀번호가 일치하지 않습니다!");
                        TextView_PasswordCheck_check.setTextColor(0xFFEC0707);
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

        // 소환사 이름 입력 및 존재하는 소환사 명인지 확인 하는 것 구현
        Button_lolName_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EditText_lolName.getText() == null) {
                    TextView_lolName_check.setText("입력하세요!");
                    TextView_lolName_check.setTextColor(0xFFEC0707);
                    return;
                } else {
                    if (isExistSummoner(EditText_lolName.getText().toString())) {
                        TextView_lolName_check.setText("존재하는 소환사명!");
                        TextView_lolName_check.setTextColor(0xFF03A9F4);
                        // 소환사명이 결정됬으므로 입력창과 확인 버튼 다시 클릭 못하도록 변경
                        setUseableEditText(EditText_lolName, false);
                        Button_lolName_check.setClickable(false);
                        lolCheck = true; // 소환사명이 올바르게 입력 됬음을 알림!
                    } else {
                        TextView_lolName_check.setText("존재하지 않는 소환사명!");
                        TextView_lolName_check.setTextColor(0xFFEC0707);
                    }
                }
            }
        });

        Button_signUpPage_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IDCheck && PWCheck && lolCheck) { //  입력이 모두 올바르게 들어온 경우
                    // 서버의 php파일을 통해 db에 입력한 데이터 넣음.(POST방식으로 인자 전달)
                    String url = "http://minkyu4626.dothome.co.kr/memberData_insert.php";
                    String id = EditText_ID.getText().toString();
                    String pw = EditText_Password.getText().toString();
                    String summoner = EditText_lolName.getText().toString();
                    InsertDataPHP conn = new InsertDataPHP();
                    String result = ""; // 정상적으로 입력됬는지 확인하게 해주는 변수
                    try {
                        result = conn.execute(url, id, pw, summoner).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 팝업을 띄워 회원가입이 완료됬음을 알리고 로그인 화면으로 이동하게 함
                    if (result.equals("1"))
                        show_successPopUp();
                    else Log.d("result는 ", result);
                } else {
                    // 팝업을 띄워 모든 입력을 제대로 입력하라고 알림
                    show_errorPopUp();
                }
            }
        });
    }

    // 입력한 ID가 이미 존재하는 아이디인지 아닌지 확인하는 함수
    public boolean isDuplicatedID(String ID) {
        PHP = new PhpCommunicate();
        String url = "http://minkyu4626.dothome.co.kr/duplicatedID.php?id=" + ID;
        String tORf="";
        try {
            tORf = PHP.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tORf == null || tORf.equals("1")) {
                return true;
            } else {
                return false;
            }
        }
    }

    // lolName이 존재하는 소환사명인지 확인하는 함수수
    private Boolean isExistSummoner(String name) {
        PHP = new PhpCommunicate();
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
    // '가입하기' 버튼 클릭시 에러 팝업을 띄우는 함수
    private void show_errorPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error!");
        builder.setMessage("모든 내용을 입력하세요!\n(ID, Password, 소환사명)");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
    // '가입하기' 버튼 클릭시 가입 완료 팝업을 띄우는 함수
    private void show_successPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("가입 완료!");
        builder.setMessage("화원가입이 완료 되었습니다.\n닫기를 누르면 로그인화면으로 이동합니다.");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(com.example.projecttotal.SingUpPage_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        }).show();
    }


    private class PhpCommunicate extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String output = "";

            if (strings[0].contains("http://minkyu4626.dothome.co.kr")) { // 서버와의 통신하는 경우
                try {
                    // 연결 url 설정
                    URL url = new URL(strings[0]);

                    // connection 객체 생성
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    // 연결 되었으면?
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        // 연결된 코드가 리턴되게 되면
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line = br.readLine();
                            output += line;
                            Log.d("debug", line);
                            br.close();
                        }
                        conn.disconnect();
                    } else {
                        Log.d("debug", "연결 실패");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else if (strings[0].contains("https://kr.api.riotgames.com")) { // RiotAPI를 사용하는 경우
                try {
                    // 연결 url 설정
                    URL url = new URL(strings[0]);

                    // connection 객체 생성
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    // 연결 되었으면?
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        // 연결된 코드가 리턴되게 되면
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            output += "1";
                            Log.d("debug", output);
                        } else {
                            output += "0";
                        }
                        conn.disconnect();
                    } else {
                        Log.d("debug", "연결 실패");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return output;
        }
    }

    private class InsertDataPHP extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String output = "";
            String id = (String)strings[1];
            String pw = (String)strings[2];
            String summoner = (String)strings[3];
            try {
                URL url = new URL(strings[0]); // 연결 URL
                String postData = "id=" + id + "&pw=" + pw + "&summoner=" + summoner;
                Log.d("postData", postData);
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
                    output = "-1";
                    Log.d("debug", "연결실패!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;
        }
    }

}

