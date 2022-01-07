package com.team_3.accountbook;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapter2 extends RecyclerView.Adapter<adapter2.CumstomViewHolder>{
    private ArrayList<item> arrayList;
    private Context context;

    public adapter2(Context context, ArrayList<item> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @NonNull
    @Override
    public adapter2.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data2, parent, false);
        CumstomViewHolder holder = new CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull adapter2.CumstomViewHolder holder, int position) {
        ArrayList<String> dateArray = new ArrayList<>(); //날짜만 들어있는 리스트
        ArrayList<item> Array = new ArrayList<>();
        for (item item : arrayList) {  //arrayList 의 날짜 값이랑 같은 값만 adapter로 넘겨주기 위함
            if(!dateArray.contains(item.getMsgDate().substring(1, 15))) {//중복제거하기 위한 코드
                dateArray.add(item.getMsgDate().substring(1, 15));
            }
        }
        for (item item : arrayList) {  //arrayList 의 날짜 값이랑 같은 값만 adapter로 넘겨주기 위함
            if(item.getMsgDate().substring(1,15).equals(dateArray.get(position))){
                Array.add(item);
            }
        }

        if(!arrayList.get(position).getMsgBody().equals("") && arrayList.get(position).getMsgAmount()!=-1) {//정규화 예외처리가 나지않았을때
            holder.dt.setText(dateArray.get(position));
        }


        holder.rv.setAdapter(new adapter(Array,arrayList.get(position).getMsgDate().substring(1,15))); //년도 부터 날짜까지 매개변수로 넘김
        holder.rv.setLayoutManager(new LinearLayoutManager(context));
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;
        RecyclerView rv;
        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            dt = itemView.findViewById(R.id.dt2);
            rv = itemView.findViewById(R.id.item_rv);

        }
    }
}