package com.example.projecttotal;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder>{

    private ArrayList<com.example.projecttotal.BoardData> mList;

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

    String[] tier_name = {"BRONZE","SILVER","GOLD","PLATINUM","DIAMOND",
            "MASTER","GRANDMASTER","CHALLENGER"};

    public class BoardViewHolder extends RecyclerView.ViewHolder{
        protected TextView summoner;
        protected ImageView tier;
        protected TextView rank_type;

        public BoardViewHolder(View view){
            super(view);
            this.summoner =  view.findViewById(R.id.textView_list_summoner);
            this.tier =  view.findViewById(R.id.imageView_list_tier);
            this.rank_type =  view.findViewById(R.id.textView_list_rank_type);
        }
    }

    public BoardAdapter(ArrayList<com.example.projecttotal.BoardData> list){
        this.mList = list;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list,viewGroup,false);

        BoardViewHolder viewHolder = new BoardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder viewholder, int position){
        viewholder.summoner.setText(mList.get(position).getBoard_summoner());
        viewholder.rank_type.setText(mList.get(position).getBoard_rank_type());

        String _tier = mList.get(position).getBoard_tier();
        int i;
        for(i=0; i<9; i++){
            if(tier_name[i].equals(_tier)) break;
        }

        viewholder.tier.setImageResource(tier_image.get(i+1));
    }

    @Override
    public int getItemCount(){
        return (null != mList? mList.size() :0);
    }
}