package com.example.projecttotal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Board_chat_link extends AppCompatActivity {
    private String userID; // 내 아이디
    private String userSummoner; // 내 소환사명
    private String userTier; // 내 티어
    private String userWinRate; // 내 승률
    private String destID; // 상대 아이디
    private String name; // 상대 소환사명
    private String destTier;

    private int destWins, destLoses;
    private SummnoerInfo destInfo;
    private SummnoerInfo myInfo;

    private EditText editText; //++
    private RecyclerView recyclerView; //++
    private Button button; //++
    private String reboardUid; //++
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");//++{
    private TextView TextView_wins;
    private TextView TextView_loses;
    private TextView TextView_winRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_link);

        Bundle extras = getIntent().getExtras();

        List<Integer> tier_image = Arrays.asList(
                R.drawable.iron,
                R.drawable.bronze,
                R.drawable.silver,
                R.drawable.gold,
                R.drawable.platinum,
                R.drawable.diamond,
                R.drawable.master,
                R.drawable.grandmaster,
                R.drawable.chanllenger
        );

        String[] tier_name = {"BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND",
                "MASTER", "GRANDMASTER", "CHALLENGER"};
        userID = extras.getString("userID"); // 내 아이디
        userSummoner = extras.getString("userSummoner"); // 내 소환사명
        destID = extras.getString("name"); // 상대 아이디
        name = extras.getString("summoner"); // 상대 소환사명
        destInfo = new SummnoerInfo(name);
        myInfo = new SummnoerInfo(userSummoner);
        destInfo.setInfo();
        myInfo.setInfo();

        destWins = destInfo.getWin(); userTier = myInfo.getTier();
        destLoses = destInfo.getLose(); userWinRate = String.valueOf(myInfo.getWinRate()) + "%";

        String content = extras.getString("content"); // 작성 글
        String tier = extras.getString("tier"); // 티어
        String rank_type = extras.getString("rank_type"); // 랭크타입
        Log.d("랭크타입", rank_type);
        Log.d("체크해야 하는 것들", "내id:" + userID + " 내소환사명:" + userSummoner + " 상대id:" + destID + " 상대소환사명:" + name
        + " 상대 wins:" + destWins + " 상대 Loses:" + destLoses);
        ImageView tier_i = (ImageView) findViewById(R.id.image_main_tier);
        TextView textView_s = (TextView) findViewById(R.id.Text_main_summoner);
        TextView textView_r = (TextView) findViewById(R.id.Text_main_rank_type);
        TextView textView_c = (TextView) findViewById(R.id.Text_main_content);
        Button button_chat_link = (Button) findViewById(R.id.button_chat_link);
        TextView_wins = findViewById(R.id.TextView_wins);
        TextView_loses = findViewById(R.id.TextView_loses);
        TextView_winRate = findViewById(R.id.TextView_winRate);

        button_chat_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Board_chat_link.this, MessageActivity.class);
                // 내 아이디와 소환사명
                intent.putExtra("userID", userID);
                intent.putExtra("destinationUid", destID);
                // 상대 아이디와 소환사명
                intent.putExtra("userSummoner", userSummoner);
                intent.putExtra("destSummoner", name);
                intent.putExtra("destTier", tier);
                intent.putExtra("userTier", userTier);
                intent.putExtra("userWinRate", userWinRate);
                startActivity(intent);
            }
        });
        int i;
        for (i = 0; i < 9; i++) {
            if (tier_name[i].equals(tier)) break;
        }

        tier_i.setImageResource(tier_image.get(i + 1));
        textView_s.setText(name);
        textView_r.setText(rank_type);
        textView_c.setText("'"+content+"'");
        TextView_wins.setText(String.valueOf(destWins));
        TextView_loses.setText(String.valueOf(destLoses));
        TextView_winRate.setText(String.valueOf(destInfo.getWinRate())+"%");
        init();
        sendMsg();
    }

    public void onClick(View view) {
        finish();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_reboard);
        editText = (EditText) findViewById(R.id.msg_input_reboard);//++
        button = findViewById(R.id.sendBtn_reboard); //++

        if (editText.getText().toString() == null) button.setEnabled(false);
        else button.setEnabled(true);


        checkChatRoom();
    }

    private void sendMsg() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReBoardModel reBoardModel = new ReBoardModel();
                reBoardModel.users.put(destID, true);

                //push() 데이터가 쌓이기 위해 채팅방 key가 생성
                if (reboardUid == null) {
                    Toast.makeText(Board_chat_link.this, "댓글 입력", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("reboard").push().setValue(reBoardModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                } else {
                    sendMsgToDataBase();
                }
            }
        });
    }

    //작성한 댓글을 데이터베이스에 보낸다.
    private void sendMsgToDataBase() {
        if (!editText.getText().toString().equals("")) {
            ChatModel.Comment comment = new ChatModel.Comment();
            comment.uid = userSummoner;    //소환사명으로 변경 userSummoner
            comment.message = editText.getText().toString();
            comment.timestamp = ServerValue.TIMESTAMP;
            FirebaseDatabase.getInstance().getReference().child("reboard").child(reboardUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editText.setText("");
                }
            });
        }
    }

    private void checkChatRoom() {
        //자신 key == true 일때 ReboardModel 가져온다.
        /* ReboardModel
        public Map<String,Boolean> users = new HashMap<>(); //게시글 유저
        public Map<String, ChatModel.Comment> comments = new HashMap<>(); //댓글 메세지
        */
        FirebaseDatabase.getInstance().getReference().child("reboard").orderByChild("users/" + destID).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    ReBoardModel reBoardModel = dataSnapshot.getValue(ReBoardModel.class);
                    reboardUid = dataSnapshot.getKey();
                    button.setEnabled(true);

                    //동기화
                    recyclerView.setLayoutManager(new LinearLayoutManager(Board_chat_link.this));
                    recyclerView.setAdapter(new RecyclerViewAdapter());

                    //메시지 보내기
                    sendMsgToDataBase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //===============댓글 창===============//
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            getDestUid();
        }

        //상대방 uid 하나(single) 읽기
        private void getDestUid() {
            FirebaseDatabase.getInstance().getReference().child("users").child(destID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //댓글 내용 읽어들임
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //댓글 내용 읽어들임
        private void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("reboard").child(reboardUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        comments.add(dataSnapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ViewHolder viewHolder = ((ViewHolder) holder);

            viewHolder.textViewName.setText(comments.get(position).uid);
            viewHolder.linearLayoutDest.setVisibility(View.VISIBLE);
            viewHolder.textViewMsg.setText(comments.get(position).message);
            viewHolder.linearLayoutRoot.setGravity(Gravity.LEFT);
            viewHolder.textViewTimeStamp.setText(getDateTime(position));
        }

        public String getDateTime(int position) {
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            return time;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewMsg;   //댓글 내용
            public TextView textViewName;
            public TextView textViewTimeStamp;
            public ImageView imageViewProfile;
            public LinearLayout linearLayoutDest;
            public LinearLayout linearLayoutRoot;
            //   public LinearLayout linearLayoutTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewMsg = (TextView) itemView.findViewById(R.id.messageItem_textView_message_reboard);
                textViewName = (TextView) itemView.findViewById(R.id.messageItem_textView_name_reboard);
                textViewTimeStamp = (TextView) itemView.findViewById(R.id.messageItem_textView_timestamp_reboard);
                // imageViewProfile = (ImageView)itemView.findViewById(R.id.item_messagebox_ImageView_profile);
                linearLayoutDest = (LinearLayout) itemView.findViewById(R.id.messageItem_linearLayout_dest_reboard);
                linearLayoutRoot = (LinearLayout) itemView.findViewById(R.id.messageItem_linearLayout_main_reboard);
                //      linearLayoutTime = (LinearLayout)itemView.findViewById(R.id.me);
            }
        }
    }
}
