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
import java.util.List;

public class AssetOutAdapter extends RecyclerView.Adapter<AssetOutAdapter.CumstomViewHolder> {
    private List<AssetNameWayNameAndBalance> ANWNList;
    private ArrayList<String> assetNameList;
    private Context context;
    private ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances = new ArrayList<>();


    public AssetOutAdapter(List<AssetNameWayNameAndBalance> ANWNList, ArrayList<String> assetNameList, Context context) {
        this.ANWNList = ANWNList;
        this.assetNameList = assetNameList;
        this.context = context;
    }


    @NonNull
    @Override
    public AssetOutAdapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_assetrv, parent, false);
        CumstomViewHolder holder = new CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull AssetOutAdapter.CumstomViewHolder holder, int position) {
        wayNameAndBalances.clear();
        for (int i = 0; i < ANWNList.size(); i++) {
            if(assetNameList.get(position).equals(ANWNList.get(i).getAssetName())){
                wayNameAndBalances.add(new AssetNameWayNameAndBalance(
                        assetNameList.get(position), ANWNList.get(i).getWayName(), ANWNList.get(i).getWayBalance()));
            }
        }
//        for (AssetNameWayNameAndBalance AnWnb : ANWNList) {
//            if(AnWnb.getAssetName().equals(assetNameList.get(position))){
//                wayNameAndBalances.add(AnWnb);
//            }
//        }

        holder.mAssetNameInRV.setText(assetNameList.get(position));

        holder.mSortInWay.setAdapter(new AssetInAdapter(wayNameAndBalances));
        holder.mSortInWay.setLayoutManager(new LinearLayoutManager(context));
    }


    @Override
    public int getItemCount() {
        return assetNameList.size();
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView mAssetNameInRV;
        RecyclerView mSortInWay;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            mAssetNameInRV = itemView.findViewById(R.id.rv_tv_assetName);
            mSortInWay = itemView.findViewById(R.id.rv_wayInAsset);
        }
    }
}
