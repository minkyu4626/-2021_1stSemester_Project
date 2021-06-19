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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {
    private String chatRoomUid; //채팅방 하나 id
    private String myuid;       //나의 id
    private String destUid;     //상대방 uid
    private String destTier; // 상대방 티어
    private String userSummoner; //본인 소환사명
    private String destSummoner; //상대방 소환사명
    private String userTier, userWinRate, userWins, userLoses;
    private RecyclerView recyclerView;
    private Button button;
    private EditText editText;

    private FirebaseDatabase firebaseDatabase;

    private User destUser;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageActivity.this, Userpage_chat.class);
        intent.putExtra("userID", myuid);
        intent.putExtra("userSummoner", userSummoner);
        intent.putExtra("userTier", userTier);
        intent.putExtra("userWinRate", userWinRate);
        intent.putExtra("userWins", userWins);
        intent.putExtra("userLoses", userLoses);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message);

        init();
        sendMsg();
    }

    private void init()
    {
        myuid = getIntent().getStringExtra("userID");
        /*
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         */
        destUid = getIntent().getStringExtra("destinationUid");        //채팅 상대
        destTier = getIntent().getStringExtra("destTier"); //상대방 티어
        userSummoner = getIntent().getStringExtra("userSummoner"); //본인 소환사명
        destSummoner = getIntent().getStringExtra("destSummoner"); //상대방 소환사명
        userTier = getIntent().getStringExtra("userTier");
        userWinRate = getIntent().getStringExtra("userWinRate");

        Log.d("인텐트 전달 확인 ", "userID:" + myuid + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);
        TextView textView_list_summoner = findViewById(R.id.textView_list_summoner);
        TextView TextView_WinRate = findViewById(R.id.TextView_WinRate);
        ImageView imageView_list_tier = findViewById(R.id.imageView_list_tier);
        textView_list_summoner.setText(destSummoner);
        // TextView_WinRate.setText(destWinrate);
        if (destTier.equals("IRON"))
            imageView_list_tier.setImageResource(R.drawable.iron);
        else if (destTier.equals("BRONZE"))
            imageView_list_tier.setImageResource(R.drawable.bronze);
        else if (destTier.equals("SILVER"))
            imageView_list_tier.setImageResource(R.drawable.silver);
        else if (destTier.equals("GOLD"))
            imageView_list_tier.setImageResource(R.drawable.gold);
        else if (destTier.equals("PLATINUM"))
            imageView_list_tier.setImageResource(R.drawable.platinum);
        else if (destTier.equals("DIAMOND"))
            imageView_list_tier.setImageResource(R.drawable.diamond);
        else if (destTier.equals("MASTER"))
            imageView_list_tier.setImageResource(R.drawable.master);
        else if (destTier.equals("GRANDMASTER"))
            imageView_list_tier.setImageResource(R.drawable.grandmaster);
        else if (destTier.equals("CHALLENGER"))
            imageView_list_tier.setImageResource(R.drawable.chanllenger);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_chat);
        button=(Button)findViewById(R.id.sendBtn);
        editText = (EditText)findViewById(R.id.msg_input);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if(editText.getText().toString() == null) button.setEnabled(false);
        else button.setEnabled(true);

        checkChatRoom();
    }
    /*
        //푸시알람...구현x
        private void sendGcm()
        {
            final Gson gson = new Gson();

            final NotificationModel notificationModel = new NotificationModel();

            firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        notificationModel.notification.title = name;
                        notificationModel.to = destUser.pushToken;
                        notificationModel.notification.text = editText.getText().toString();

                        notificationModel.data.title = name;
                        notificationModel.data.text = editText.getText().toString();
                    }

                    //서버에 알림 데이터를 json 형태로
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
                    Request request = new Request.Builder()
                            .header("Context-Type","application/json")
                            .addHeader("Authorization", "서버키")
                            .url("서버 url")
                            .post(requestBody)
                            .build();

                    OkHttpClient okHttpClient = new OkHttpClient();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            //서버에 토큰을 db에 저장하고, 저장한 토큰을 가지고 서버에서 FirebaseMessagingService 에 메시지를 보낸다.
        }
    */
    private void sendMsg()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(myuid,true);
                chatModel.users.put(destUid,true);
                chatModel.summoners.put(userSummoner, true);
                chatModel.summoners.put(destSummoner, true);

                //push() 데이터가 쌓이기 위해 채팅방 key가 생성
                if(chatRoomUid == null){
                    Toast.makeText(MessageActivity.this, "채팅방 생성", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                    firebaseDatabase.getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }else{
                    sendMsgToDataBase();
                }
            }
        });
    }
    //작성한 메시지를 데이터베이스에 보낸다.
    private void sendMsgToDataBase()
    {
        if(!editText.getText().toString().equals(""))
        {
            ChatModel.Comment comment = new ChatModel.Comment();
            comment.uid = userSummoner;    //소환사명으로 변경 userSummoner
            comment.message = editText.getText().toString();
            comment.timestamp = ServerValue.TIMESTAMP;
            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editText.setText("");
                }
            });
        }
    }

    private void checkChatRoom()
    {
        //자신 key == true 일때 chatModel 가져온다.
        /* chatModel
        public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
        public Map<String, ChatModel.Comment> comments = new HashMap<>(); //채팅 메시지
        */
        firebaseDatabase.getReference().child("chatrooms").orderByChild("users/"+myuid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destUid)){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                        chatRoomUid = dataSnapshot.getKey();
                        button.setEnabled(true);

                        //동기화
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());

                        //메시지 보내기
                        sendMsgToDataBase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //===============채팅 창===============//
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
    {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter(){
            comments = new ArrayList<>();

            getDestUid();
        }

        //상대방 uid 하나(single) 읽기
        private void getDestUid()
        {
            firebaseDatabase.getReference().child("users").child(destUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    destUser = snapshot.getValue(User.class);

                    //채팅 내용 읽어들임
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //채팅 내용 읽어들임
        private void getMessageList()
        {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        comments.add(dataSnapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size()-1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ViewHolder viewHolder = ((ViewHolder)holder);

            if(comments.get(position).uid.equals(userSummoner)) //나의 uid 이면 -> 나의 소환사명이면
            {
                //나의 말풍선 오른쪽으로
                viewHolder.textViewMsg.setText(comments.get(position).message);
                viewHolder.textViewMsg.setBackgroundResource(R.drawable.bubbleright);
                viewHolder.linearLayoutDest.setVisibility(View.INVISIBLE);        //상대방 레이아웃
                viewHolder.linearLayoutRoot.setGravity(Gravity.RIGHT);
               // viewHolder.linearLayoutTime.setGravity(Gravity.RIGHT);
            }else{
                //상대방 말풍선 왼쪽
            /*    Glide.with(holder.itemView.getContext())
                        .load(destUser.profileImgUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.imageViewProfile);     // profile 사진
             */
                viewHolder.textViewName.setText(comments.get(position).uid);
                viewHolder.linearLayoutDest.setVisibility(View.VISIBLE);
                viewHolder.textViewMsg.setBackgroundResource(R.drawable.bubbleleft);
                viewHolder.textViewMsg.setText(comments.get(position).message);
                viewHolder.linearLayoutRoot.setGravity(Gravity.LEFT);
               // viewHolder.linearLayoutTime.setGravity(Gravity.LEFT);
            }
                viewHolder.textViewTimeStamp.setText(getDateTime(position));
        }

        public String getDateTime(int position)
        {
            long unixTime=(long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            return time;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView textViewMsg;   //메시지 내용
            public TextView textViewName;
            public TextView textViewTimeStamp;
            public ImageView imageViewProfile;
            public LinearLayout linearLayoutDest;
            public LinearLayout linearLayoutRoot;
         //   public LinearLayout linearLayoutTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewMsg = (TextView)itemView.findViewById(R.id.messageItem_textView_message);
                textViewName = (TextView)itemView.findViewById(R.id.messageItem_textView_name);
                textViewTimeStamp = (TextView)itemView.findViewById(R.id.messageItem_textView_timestamp);
               // imageViewProfile = (ImageView)itemView.findViewById(R.id.item_messagebox_ImageView_profile);
                linearLayoutDest = (LinearLayout)itemView.findViewById(R.id.messageItem_linearLayout_dest);
                linearLayoutRoot = (LinearLayout)itemView.findViewById(R.id.messageItem_linearLayout_main);
          //      linearLayoutTime = (LinearLayout)itemView.findViewById(R.id.me);
            }
        }
    }
}
