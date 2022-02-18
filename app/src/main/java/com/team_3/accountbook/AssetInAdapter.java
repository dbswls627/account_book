package com.team_3.accountbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AssetInAdapter extends RecyclerView.Adapter<AssetInAdapter.CumstomViewHolder>{
    private ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances;


    public AssetInAdapter(ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances) {
        this.wayNameAndBalances = wayNameAndBalances;
    }


    @NonNull
    @Override
    public AssetInAdapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_wayinasset, parent, false);
        AssetInAdapter.CumstomViewHolder holder = new AssetInAdapter.CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull AssetInAdapter.CumstomViewHolder holder, int position) {
        holder.mWayName.setText(wayNameAndBalances.get(position).getWayName());
        holder.mWayBalance.setText(wayNameAndBalances.get(position).getWayBalance() + "원");

        holder.itemView.setOnClickListener(View -> {
            
        });
    }


    @Override
    public int getItemCount() {
        return wayNameAndBalances.size();
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mWayName, mWayBalance;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            mWayName = itemView.findViewById(R.id.tv_wayNameInAsset);
            mWayBalance = itemView.findViewById(R.id.tv_wayBalance);
        }
    }
}
