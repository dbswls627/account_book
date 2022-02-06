package com.team_3.accountbook;


import android.content.Context;
import android.content.Intent;
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
    private Context context;

    public adapter(ArrayList<item> arrayList) {
        this.arrayList = arrayList;
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
        context = parent.getContext();

        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull adapter.CumstomViewHolder holder, int position) {
        holder.dt.setText((CharSequence) arrayList.get(position).getMsgDate().substring(14, 19));
        holder.bd.setText((CharSequence) arrayList.get(position).getMsgBody());
        holder.amt.setText(arrayList.get(position).getMsgAmount() + "원");

        // 리스트 항목 클릭시~
        if (context instanceof MainActivity) {  //호출한 액티비티가 MainActivity(메세지 액티비티)일 경우
            holder.itemView.setOnClickListener((view -> {
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("amount", arrayList.get(position).getMsgAmount());
                intent.putExtra("date", arrayList.get(position).getMsgDate());
                intent.putExtra("body", arrayList.get(position).getMsgBody());

                context.startActivity(intent);      // ~AddActivity 로 넘어감
            }));
        }
    }



    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;        // 시간(HH:mm)
        TextView bd;        // 내용
        TextView amt;       // 금액

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);

            dt = itemView.findViewById(R.id.dt);
            bd = itemView.findViewById(R.id.bd);
            amt = itemView.findViewById(R.id.amt);
        }
    }
}