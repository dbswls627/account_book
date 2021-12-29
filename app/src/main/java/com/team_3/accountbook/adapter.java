package com.team_3.accountbook;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.CumstomViewHolder>{
    private ArrayList<String> arrayList;

    public adapter(ArrayList<String> arrayList) {
        this.arrayList=arrayList;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public adapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data,parent,false);
        CumstomViewHolder holder = new CumstomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.CumstomViewHolder holder, int position) {

        holder.text.setText(arrayList.get(position));
    }

    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.text);
        }
    }
}