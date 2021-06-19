package com.example.projecttotal;

public class BoardData {
    private String board_name;
    private String board_summoner;
    private String board_content;
    private String board_tier;
    private String board_rank_type;

    public String getBoard_name(){
        return board_name;
    }

    public String getBoard_summoner(){
        return board_summoner;
    }

    public String getBoard_content(){
        return board_content;
    }

    public String getBoard_tier(){
        return board_tier;
    }

    public String getBoard_rank_type(){
        return board_rank_type;
    }

    public void setBoard_name(String board_name){
        this.board_name = board_name;
    }

    public void setBoard_summoner(String board_summoner){
        this.board_summoner = board_summoner;
    }

    public void setBoard_content(String board_content){
        this.board_content = board_content;
    }

    public void setBoard_tier(String board_tier){
        this.board_tier = board_tier;
    }

    public void setBoard_rank_type(String board_rank_type){ this.board_rank_type = board_rank_type; }

    public BoardData(String Name, String Summoner, String Content, String Tier, String Rank_type){
        board_name = Name;
        board_summoner = Summoner;
        board_content = Content;
        board_tier = Tier;
        board_rank_type = Rank_type;
    }
}
