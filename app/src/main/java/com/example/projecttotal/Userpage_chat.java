package com.example.projecttotal;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class Userpage_chat extends AppCompatActivity {
    private String userID;
    private String userSummoner;
    private String userTier;
    private String destSummoner;
    private String userWinRate;
    private int userWins, userLoses;

    private ImageButton ImageButton_user;
    private ImageButton ImageButton_search;
    private ImageButton ImageButton_setting;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_chat);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        userSummoner = intent.getExtras().getString("userSummoner");
        userTier = intent.getExtras().getString("userTier");
        userWinRate = intent.getExtras().getString("userWinRate");
        SummnoerInfo summoner = new SummnoerInfo(userSummoner);
        summoner.setInfo();
        userWins = summoner.getWin();
        userLoses = summoner.getLose();
        Log.d("인텐트 전달 확인 ", "userID:" + userID + " userSummoner:" + userSummoner +
                " userTier:" + userTier + " userWinRate:" + userWinRate + " userWins:" + userWins + " userLoses:" + userLoses);
        // recyclerview 설정
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView2.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        // 이미지버튼 뷰 받아오기
        ImageButton_user = findViewById(R.id.ImageButton_user);
        ImageButton_search = findViewById(R.id.ImageButton_search);
        ImageButton_setting = findViewById(R.id.ImageButton_setting);
        // 클릭이벤트 구현
        ImageButton_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Userpage_chat.this, Userpage_finduser.class);
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
                Intent intent = new Intent(Userpage_chat.this, Userpage_search.class);
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
                Intent intent = new Intent(Userpage_chat.this, Userpage_setting.class);
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
    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private String uid;
        private List<ChatModel> chatModels = new ArrayList<>();
        private ArrayList<String> destinationUsers = new ArrayList<>();
        private String lastMessageKey;
        private String lastMessageKey2;

        public ChatRecyclerViewAdapter(){
            uid = userID;

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModels.clear();
                    for(DataSnapshot item:snapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));
                    }

                    for(int i=0; i<chatModels.size(); i++){
                        //메세지를 내림차순 정렬 후 마자막 메세지를 메세지의 키값을 가져옴
                        Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
                        commentMap.putAll(chatModels.get(i).comments);
                        lastMessageKey = (String) commentMap.keySet().toArray()[0];

                        long unixtimei = (long)chatModels.get(i).comments.get(lastMessageKey).timestamp;
                        double timei = Long.valueOf(unixtimei).doubleValue();

                        for(int j=i+1; j<chatModels.size(); j++){
                            //메세지를 내림차순 정렬 후 마자막 메세지를 메세지의 키값을 가져옴
                            Map<String,ChatModel.Comment> commentMap2 = new TreeMap<>(Collections.reverseOrder());
                            commentMap2.putAll(chatModels.get(j).comments);
                            lastMessageKey2 = (String) commentMap2.keySet().toArray()[0];

                            long unixtimej = (long)chatModels.get(j).comments.get(lastMessageKey2).timestamp;
                            double timej = Long.valueOf(unixtimej).doubleValue();
                            if(timei < timej){
                                ChatModel temp = new ChatModel();
                                temp = chatModels.get(j);
                                chatModels.set(j,chatModels.get(i));
                                chatModels.set(i,temp);
                            }

                        }
                    }
                    notifyDataSetChanged();;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder)holder;

            String destinationUid = null;
            destSummoner = null;
            for(String user: chatModels.get(position).users.keySet()){
                if(!user.equals(uid)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            for(String user: chatModels.get(position).summoners.keySet()){
                if(!user.equals(userSummoner)){
                    destSummoner = user;
                }
            }
            SummnoerInfo summnoerInfo = new SummnoerInfo(destSummoner);
            summnoerInfo.setInfo();
            String destTier = summnoerInfo.getTier();
            //메세지를 내림차순 정렬 후 마자막 메세지를 메세지의 키값을 가져옴
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            //_title은 채팅룸 이름. 현재는 상대방 1명만 나옴
            customViewHolder.textView_title.setText(destSummoner);
            //customViewHolder.textView_title.setText(chatModels.get(position).comments.get(lastMessageKey).uid);
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);
            // 티어 이미지 변경
            if (destTier.equals("IRON"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.iron);
            else if (destTier.equals("BRONZE"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.bronze);
            else if (destTier.equals("SILVER"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.silver);
            else if (destTier.equals("GOLD"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.gold);
            else if (destTier.equals("PLATINUM"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.platinum);
            else if (destTier.equals("DIAMOND"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.diamond);
            else if (destTier.equals("MASTER"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.master);
            else if (destTier.equals("GRANDMASTER"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.grandmaster);
            else if (destTier.equals("CHANLLENGER"))
                customViewHolder.ImageView_tier.setImageResource(R.drawable.chanllenger);
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(position));
                    intent.putExtra("userSummoner", userSummoner);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userTier", userTier);
                    intent.putExtra("destSummoner", destSummoner);
                    intent.putExtra("destTier", destTier);
                    intent.putExtra("userWinRate", userWinRate);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.slide_in_left,R.anim.slide_out_right);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            //============================= 채팅방 나가기

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //오랫동안 눌럿을 떄 이벤트 발생
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("나가기 후 복구가 불가능 합니다.");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+userID).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String chatRoomUid;
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                                    {
                                        ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                                        if(chatModel.users.containsKey(destinationUsers.get(position))){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                                            chatRoomUid = dataSnapshot.getKey();
                                            chatModel.users.put(userID, false);

                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            ChatModel.Comment comment = new ChatModel.Comment();
                                            comment.uid = "";    //소환사명으로 변경 userSummoner
                                            comment.message = userSummoner + "님이 채팅방을 나갔습니다";
                                            comment.timestamp = ServerValue.TIMESTAMP;
                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                        }
                                    }
                                    new Handler().postDelayed(new Runnable(){
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "채팅방을 나갔습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }, 1000);
                                    Intent intent = new Intent(Userpage_chat.this,Userpage_chat.class);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userSummoner", userSummoner);
                                    intent.putExtra("userTier", userTier);
                                    intent.putExtra("userWinRate", userWinRate);
                                    intent.putExtra("userWins", userWins);
                                    intent.putExtra("userLoses", userLoses);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            return;
                        }

                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    builder.setTitle("채팅방을 나가시겠습니까?");
                    builder.show();
                    return true;
                }
            });
            //====================================

            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;
            public ImageView ImageView_tier;
            public CustomViewHolder(View view) {
                super(view);

                textView_last_message=(TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_title=(TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_timestamp=(TextView)view.findViewById(R.id.chatitem_textview_timestamp);
                ImageView_tier = (ImageView)view.findViewById(R.id.ImageView_tier);
            }
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
