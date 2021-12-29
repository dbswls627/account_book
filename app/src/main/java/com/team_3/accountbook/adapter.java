package com.team_3.accountbook;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.CumstomViewHolder>{
    private ArrayList<item> arrayList;


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

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull adapter.CumstomViewHolder holder, int position) {
        holder.dt.setText((CharSequence) arrayList.get(position).getMsgDate());
        holder.bd.setText((CharSequence) arrayList.get(position).getMsgBody());
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;
        TextView bd;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            dt = itemView.findViewById(R.id.dt);
            bd = itemView.findViewById(R.id.bd);
        }
    }
}