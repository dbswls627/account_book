package com.team_3.accountbook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AssetInAdapter extends RecyclerView.Adapter<AssetInAdapter.CumstomViewHolder>{
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    private ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances;
    private Context context;
    private OnItemClickInAssetAc mClickInAssetAc;
    public interface OnItemClickInAssetAc{
        void listItemClick(String wayName);
    }



    public AssetInAdapter(ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances, OnItemClickInAssetAc mClickInAssetAc) {
        this.wayNameAndBalances = wayNameAndBalances;
        this.mClickInAssetAc = mClickInAssetAc;
    }



    @NonNull
    @Override
    public AssetInAdapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_wayinasset, parent, false);
        AssetInAdapter.CumstomViewHolder holder = new AssetInAdapter.CumstomViewHolder(view);
        context = parent.getContext();

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull AssetInAdapter.CumstomViewHolder holder, int position) {
        String formatBalance = myFormatter.format(wayNameAndBalances.get(position).getWayBalance());
        holder.mWayName.setText(wayNameAndBalances.get(position).getWayName());
        holder.mWayBalance.setText(formatBalance + "원");

        holder.itemView.setOnClickListener(View -> {
            mClickInAssetAc.listItemClick(holder.mWayName.getText().toString());
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
