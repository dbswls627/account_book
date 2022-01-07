package com.team_3.accountbook;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.CumstomViewHolder>{
    private ArrayList<item> arrayList;
    private String date;


    public adapter(ArrayList<item> arrayList,String date) {
        this.arrayList = arrayList;
        this.date=date;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @NonNull
    @Override
    public adapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data, parent, false);
        CumstomViewHolder holder = new CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull adapter.CumstomViewHolder holder, int position) {

        if(!arrayList.get(position).getMsgBody().equals("") &&   //정규식에 예외처리가 되지 않으면..
                arrayList.get(position).getMsgAmount()!=-1 &&
                arrayList.get(position).getMsgDate().substring(1,15).equals(date)) { //메게변수로 받은 상위 리사이클러뷰의 값과 날짜가 같은 값만
            holder.dt.setText((CharSequence) arrayList.get(position).getMsgDate().substring(15,21));
            holder.bd.setText((CharSequence) arrayList.get(position).getMsgBody());
            holder.amt.setText(arrayList.get(position).getMsgAmount()+"원");
        }
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;
        TextView bd;
        TextView amt;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            dt = itemView.findViewById(R.id.dt);
            bd = itemView.findViewById(R.id.bd);
            amt = itemView.findViewById(R.id.amt);

        }
    }
}