package com.example.projecttotal;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Userpage_finduser extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String userWinRate;
    private int userWins, userLoses;

    private final static String IP_ADDRESS = "minkyu4626.dothome.co.kr";
    private final static String TAG = "project";

    private ArrayList<com.example.projecttotal.BoardData> mArrayList;
    private com.example.projecttotal.BoardAdapter mAdapter;

    //swiperefresh 선언
    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton ImageButton_finduser_chat;
    private ImageButton ImageButton_finduser_search;
    private ImageButton ImageButton_finduser_setting;

    private boolean filter_solo = true;
    private boolean filter_free = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_finduser);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID"); // 로그인한 회원의 아이디 값 받아오기
        userSummoner = intent.getExtras().getString("userSummoner"); // 로그인한 회원의 소환사명 값 받아오기
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        userWins = intent.getExtras().getInt("userWins");
        userLoses = intent.getExtras().getInt("userLoses");

        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);

        //checkBox
        final CheckBox check_solo = (CheckBox)findViewById(R.id.main_solo_rank);
        final CheckBox check_free = (CheckBox)findViewById(R.id.main_free_rank);

        //recyclerview 설정
        // String url = "http://" + IP + "/php파일명.php";
        String url = "http://minkyu4626.dothome.co.kr" + "/getjson.php";

        //Toast.makeText(getApplicationContext(), userID+" "+userSummoner+" "+userTier, Toast.LENGTH_SHORT).show();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        // button들 클릭 이벤트 구현부
        // 우선 view들 불러오기
        ImageButton_finduser_chat = findViewById(R.id.ImageButton_chat);
        ImageButton_finduser_search = findViewById(R.id.ImageButton_search);
        ImageButton_finduser_setting = findViewById(R.id.ImageButton_setting);

        ImageButton_finduser_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.projecttotal.Userpage_finduser.this,Userpage_chat.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        ImageButton_finduser_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.projecttotal.Userpage_finduser.this, com.example.projecttotal.Userpage_search.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });
        ImageButton_finduser_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.projecttotal.Userpage_finduser.this, com.example.projecttotal.Userpage_setting.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);
                startActivity(intent);
            }
        });

        mArrayList = new ArrayList<com.example.projecttotal.BoardData>();

        mAdapter = new com.example.projecttotal.BoardAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        selectDatabase selectDatabase = new selectDatabase(url, null);
        selectDatabase.execute(); // AsyncTask는 .excute()로 실행된다.

        //recyclerview click event 구현관련 설정
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                com.example.projecttotal.BoardData bd = mArrayList.get(position);
                Intent intent = new Intent(getBaseContext(), Board_chat_link.class);
                // 추가 된 2줄
                intent.putExtra("userID", userID);
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("name",bd.getBoard_name());
                intent.putExtra("summoner",bd.getBoard_summoner());
                intent.putExtra("content",bd.getBoard_content());
                intent.putExtra("tier",bd.getBoard_tier());
                intent.putExtra("rank_type",bd.getBoard_rank_type());
                // 여기에 승률, 승수, 패수 추가해야 된다 추가해야 됨
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //게시글 작성 버튼
        Button button = findViewById(R.id.write);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), com.example.projecttotal.Board_write.class);

                intent.putExtra("userID",userID);
                intent.putExtra("userSummoner",userSummoner);
                intent.putExtra("userTier",userTier);
                intent.putExtra("userWinRate", userWinRate);
                intent.putExtra("userWins", userWins);
                intent.putExtra("userLoses", userLoses);


                startActivity(intent);
            }
        });
        //당겨서 새로고침 구현
        swipeRefreshLayout = findViewById(R.id.sl_main);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                //selectDatabase.execute(); //data 가져오는거 실행(확인필요)
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button filter = (Button)findViewById(R.id.filter_type);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_solo.isChecked()) filter_solo = true;
                else filter_solo = false;

                if(check_free.isChecked()) filter_free = true;
                else filter_free = false;
                //Toast.makeText(getApplicationContext(), filter_solo+" "+filter_free, Toast.LENGTH_SHORT).show();

                selectDatabase selectDatabase = new selectDatabase(url, null);
                selectDatabase.execute(); // AsyncTask는 .excute()로 실행된다.

                mAdapter.notifyDataSetChanged();
            }
        });
    }
    class selectDatabase extends AsyncTask<Void, Void, String> {

        private String url1;
        private ContentValues values1;
        String result1; // 요청 결과를 저장할 변수.

        public selectDatabase(String url, ContentValues contentValues) {
            this.url1 = url;
            this.values1 = contentValues;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result1 = requestHttpURLConnection.request(url1, values1); // 해당 URL로 부터 결과물을 얻어온다.
            return result1; // 여기서 당장 실행 X, onPostExcute에서 실행
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //txtView.setText(s); // 파서 없이 전체 출력
            doJSONParser(s); // 파서로 전체 출력
        }
    }

    // 받아온 json 데이터를 파싱합니다..
    public void doJSONParser(String string) {
        try {
            String json_name = "";
            String json_summoner = "";
            String json_content = "";
            String json_tier = "";
            String json_rank_type = "";


            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("board");
            mArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject output = jsonArray.getJSONObject(i);
                json_name = output.getString("name");
                json_summoner = output.getString("summoner");
                json_content = output.getString("content");
                json_tier = output.getString("tier");
                json_rank_type = output.getString("rank_type");

                if(json_rank_type.equals("S") && !filter_solo) continue;
                if(json_rank_type.equals("F") && !filter_free) continue;

                if(json_rank_type.equals("S")) json_rank_type = "솔로랭크";
                else json_rank_type = "자유랭크";

                BoardData testData = new BoardData(json_name+"",json_summoner+"",json_content+"",json_tier+"",json_rank_type+"");

                mArrayList.add(testData);
            }
            mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class RequestHttpURLConnection {

        public String request(String _url, ContentValues _params){

            // HttpURLConnection 참조 변수.
            HttpURLConnection urlConn = null;
            // URL 뒤에 붙여서 보낼 파라미터.
            StringBuffer sbParams = new StringBuffer();

            /**
             * 1. StringBuffer에 파라미터 연결
             * */
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null)
                sbParams.append("");
                // 보낼 데이터가 있으면 파라미터를 채운다.
            else {
                // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
                boolean isAnd = false;
                // 파라미터 키와 값.
                String key;
                String value;

                for(Map.Entry<String, Object> parameter : _params.valueSet()){
                    key = parameter.getKey();
                    value = parameter.getValue().toString();

                    // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                    if (isAnd)
                        sbParams.append("&");

                    sbParams.append(key).append("=").append(value);

                    // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                    if (!isAnd)
                        if (_params.size() >= 2)
                            isAnd = true;
                }
            }

            /**
             * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
             * */
            try{
                URL url = new URL(_url);
                urlConn = (HttpURLConnection) url.openConnection();

                // [2-1]. urlConn 설정.
                urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                // [2-2]. parameter 전달 및 데이터 읽어오기.
                String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
                os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

                // [2-3]. 연결 요청 확인.
                // 실패 시 null을 리턴하고 메서드를 종료.
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // [2-4]. 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null){
                    page += line;
                }

                return page;

            } catch (MalformedURLException e) { // for URL.
                e.printStackTrace();
            } catch (IOException e) { // for openConnection().
                e.printStackTrace();
            } finally {
                if (urlConn != null)
                    urlConn.disconnect();
            }

            return null;
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Userpage_finduser.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final
        com.example.projecttotal.Userpage_finduser.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
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
}

