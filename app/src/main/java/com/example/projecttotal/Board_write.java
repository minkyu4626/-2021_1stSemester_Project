package com.example.projecttotal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Board_write extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private TextView TextView_contentLength;
    private EditText content;
    private ImageView ImageView_tier;
    private TextView TextView_summoner;
    private TextView TextView_WinRate;
    private TextView TextView_wins;
    private TextView TextView_loses;
    private SummnoerInfo summnoerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        TextView_contentLength = findViewById(R.id.TextView_contentLength);
        content = findViewById(R.id.editText_main_content);
        //check box 툴
        CheckBox is_solo = (CheckBox)findViewById(R.id.solo_rank);
        CheckBox is_free = (CheckBox)findViewById(R.id.free_rank);
        ImageView_tier = findViewById(R.id.ImageView_tier);
        TextView_summoner = findViewById(R.id.TextView_summoner);
        TextView_WinRate = findViewById(R.id.TextView_WinRate);
        TextView_wins = findViewById(R.id.TextView_wins);
        TextView_loses = findViewById(R.id.TextView_loses);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID"); // 로그인한 회원의 아이디 값 받아오기
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userLoses");
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);

        // 소환사 티어, 소환사명, 승률 값 setting
        TextView_summoner.setText(userSummoner);
        TextView_WinRate.setText(userWinRate);
        TextView_wins.setText(String.valueOf(userWins));
        TextView_loses.setText(String.valueOf(userLoses));

        if (userTier.equals("IRON"))
            ImageView_tier.setImageResource(R.drawable.iron);
        else if (userTier.equals("BRONZE"))
            ImageView_tier.setImageResource(R.drawable.bronze);
        else if (userTier.equals("SILVER"))
            ImageView_tier.setImageResource(R.drawable.silver);
        else if (userTier.equals("GOLD"))
            ImageView_tier.setImageResource(R.drawable.gold);
        else if (userTier.equals("PLATINUM"))
            ImageView_tier.setImageResource(R.drawable.platinum);
        else if (userTier.equals("DIAMOND"))
            ImageView_tier.setImageResource(R.drawable.diamond);
        else if (userTier.equals("MASTER"))
            ImageView_tier.setImageResource(R.drawable.master);
        else if (userTier.equals("GRANDMASTER"))
            ImageView_tier.setImageResource(R.drawable.grandmaster);
        else if (userTier.equals("CHALLENGER"))
            ImageView_tier.setImageResource(R.drawable.chanllenger);
        Button button_main_insert = findViewById(R.id.button_main_insert);
        button_main_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rank_type = "S";
                if (!is_solo.isChecked() && !is_free.isChecked()) {
                    show_errorPopUp_case1();
                    return;
                } else if (is_solo.isChecked() && is_free.isChecked()) {
                    show_errorPopUp_case2();
                    return;
                }
                if(is_solo.isChecked()) rank_type = "S";
                if(is_free.isChecked()) rank_type = "F";

                // 승률을 추가로 테이블에 삽입해야 함!!
                insertToDatabase(userID,userSummoner,content.getText().toString(),userTier,rank_type);

                Intent intent = new Intent(getApplicationContext(),Userpage_finduser.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);

                //========================= 댓글 초기화
                FirebaseDatabase.getInstance().getReference().child("reboard").orderByChild("users/"+userID).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String reboardUid;
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                        {
                            ReBoardModel reBoardModel = dataSnapshot.getValue(ReBoardModel.class);
                            if(reBoardModel.users.containsKey(userID)){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                                reboardUid = dataSnapshot.getKey();
                                reBoardModel.users.put(userID, false);

                                FirebaseDatabase.getInstance().getReference().child("reboard").child(reboardUid).setValue(reBoardModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //++
            }
        });
        content.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow( content.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = "최대 몇 30자 / 현재 " + s.length() + "자";
                TextView_contentLength.setText(text);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void insertToDatabase(final String name, String summoner,String content,String tier,String rank_type) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Board_write.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Log.d("Tag : ", s); // php에서 가져온 값을 최종 출력함
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Text_name = (String) params[0];
                    String Text_summoner = (String) params[1];
                    String Text_content = (String) params[2];
                    String Text_tier = (String) params[3];
                    String Text_rank_type = (String) params[4];

                    String link = "http://minkyu4626.dothome.co.kr"+"/insert.php";
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Text_name, "UTF-8");
                    data += "&" + URLEncoder.encode("summoner", "UTF-8") + "=" + URLEncoder.encode(Text_summoner, "UTF-8");
                    data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(Text_content, "UTF-8");
                    data += "&" + URLEncoder.encode("tier", "UTF-8") + "=" + URLEncoder.encode(Text_tier, "UTF-8");
                    data += "&" + URLEncoder.encode("rank_type", "UTF-8") + "=" + URLEncoder.encode(Text_rank_type, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                    outputStreamWriter.write(data);
                    outputStreamWriter.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("tag : ", sb.toString()); // php에서 결과값을 리턴
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(name,summoner,content,tier,rank_type);
    }

    private void show_errorPopUp_case1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작성 실패");
        builder.setMessage("최소 하나의 랭크타입을 선택해주세요!");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
    private void show_errorPopUp_case2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작성 실패");
        builder.setMessage("랭크 타입은 하나만 선택하셔야 합니다!");
        builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}