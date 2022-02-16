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
    private ArrayList<String> dateArray;

    public adapter2(Context context, ArrayList<item> arrayList, ArrayList<String> dateArray) {
        this.context = context;
        this.arrayList = arrayList;
        this.dateArray = dateArray;
    }



    @Override
    public int getItemCount() {
        return dateArray.size();
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
        ArrayList<item> Array = new ArrayList<>();          // 같은 상위날짜로 묶은 결제 정보를 담는 리스트

        for (item item : arrayList) {       // arrayList 의 날짜 값이랑 같은 값만 adapter 로 넘겨주기 위함
            if(item.getUseDate().substring(0, 14).equals(dateArray.get(position))){
                Array.add(item);
            }
        }

        holder.dt.setText(dateArray.get(position));

        holder.rv.setAdapter(new adapter(Array)); // 년도 부터 일까지 매개변수로 넘김
        holder.rv.setLayoutManager(new LinearLayoutManager(context));
    }



    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;        // 날짜(yyyy년 MM월 dd일)
        RecyclerView rv;    // 하위 리스트

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);

            dt = itemView.findViewById(R.id.dt2);
            rv = itemView.findViewById(R.id.item_rv);
        }
    }
}