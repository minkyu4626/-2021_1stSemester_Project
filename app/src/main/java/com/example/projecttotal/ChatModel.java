package com.example.projecttotal;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
    public Map<String,Comment> comments = new HashMap<>(); //채팅 메시지
    //============
    public Map<String, Boolean> summoners = new HashMap<>(); //채팅 소환사

    public static class Comment
    {
        public String uid;
        public String message;
        public Object timestamp;
    }
}