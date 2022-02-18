package com.team_3.accountbook;

import android.content.Context;
import android.content.Intent;
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


    public AssetInAdapter(ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances) {
        this.wayNameAndBalances = wayNameAndBalances;
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
            Intent intent = new Intent(context, ListInAssetActivity.class);
            intent.putExtra("wayName", holder.mWayName.getText());
            Toast.makeText(context, holder.mWayName.getText()+"(으)로 이동", Toast.LENGTH_SHORT).show();   // 테스트용

            context.startActivity(intent);
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
