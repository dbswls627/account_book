package com.team_3.accountbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AssetInAdapter extends RecyclerView.Adapter<AssetInAdapter.CumstomViewHolder>{
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    private ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances;
    private List<String> assetNameList;
    private Context context;
    private OnItemClickInAssetAc mClickInAssetAc;
    private AppDatabase db;
    public interface OnItemClickInAssetAc{
        void listItemClick(String name, String doFlag);
    }



    public AssetInAdapter(ArrayList<AssetNameWayNameAndBalance> wayNameAndBalances, OnItemClickInAssetAc mClickInAssetAc, Context context) {
        this.wayNameAndBalances = wayNameAndBalances;
        this.mClickInAssetAc = mClickInAssetAc;
        this.context = context;
    }

    public AssetInAdapter(List<String> assetNameList, OnItemClickInAssetAc mClickInAssetAc, Context context) {
        this.assetNameList = assetNameList;
        this.mClickInAssetAc = mClickInAssetAc;
        this.context = context;
    }



    @NonNull
    @Override
    public AssetInAdapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = null;
        db = AppDatabase.getInstance(context);

        if(context instanceof AssetsActivity){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_wayinasset, parent, false);
        }
        else if(context instanceof AssetForEditActivity){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_wayinasset_foredit, parent, false);
        }
        else if(context instanceof EditAssetOrSortActivity){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_edit_asset, parent, false);
        }

        AssetInAdapter.CumstomViewHolder holder = new AssetInAdapter.CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull AssetInAdapter.CumstomViewHolder holder, int position) {
        if(context instanceof AssetsActivity){
            String formatBalance = myFormatter.format(wayNameAndBalances.get(position).getWayBalance());
            String wayName = wayNameAndBalances.get(position).getWayName();
            holder.mWayName.setText(wayName);
            holder.mWayBalance.setText(formatBalance + "원");
            if(!db.dao().getWayNotiState(wayName)){        // 자동저장 데이터가 없으면 알림을 지움
                holder.mExistAutoData.setVisibility(View.INVISIBLE);
            }
            else{
                holder.mExistAutoData.setVisibility(View.VISIBLE);                      // 자동저장 데이터가 있으면 알림을 띄움
            }

            holder.itemView.setOnClickListener(View -> {
                if(db.dao().getWayNotiState(wayName)){     // way 의 자동저장 데이터가 있으면 알림을 지우게 함.
                    db.dao().updateWayNotiState(wayName, false);
                }
                mClickInAssetAc.listItemClick(wayName, "");
            });
        }
        else if(context instanceof AssetForEditActivity){
            holder.mWayName_edit.setText(wayNameAndBalances.get(position).getWayName());

            holder.itemView.setOnClickListener(View -> {
                mClickInAssetAc.listItemClick(holder.mWayName_edit.getText().toString(), "");
            });
        }
        else if(context instanceof EditAssetOrSortActivity){
            holder.mAssetName.setText(assetNameList.get(position));

            holder.mAssetLayout.setOnClickListener(View -> {
                mClickInAssetAc.listItemClick(holder.mAssetName.getText().toString(), "click");
            });
            holder.mDeleteAsset.setOnClickListener(View -> {
                mClickInAssetAc.listItemClick(holder.mAssetName.getText().toString(), "delete");
            });
        }


    }


    // 한가지 알아낸 사실: 어댑터의 getItemCount() 메소드는 onCreateViewHolder() 메소드보다 먼저 실행된다.
    @Override
    public int getItemCount() {
        int listSize = 0;

        if(context instanceof EditAssetOrSortActivity){
            listSize = assetNameList.size();
        }
        else if(context instanceof AssetsActivity || context instanceof AssetForEditActivity){
            listSize = wayNameAndBalances.size();
        }

        return listSize;
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mWayName, mWayBalance;
        private ImageView mExistAutoData;
        private TextView mWayName_edit;

        private LinearLayout mAssetLayout;
        private TextView mAssetName;
        private ImageView mDeleteAsset;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            mWayName = itemView.findViewById(R.id.tv_wayNameInAsset);
            mWayBalance = itemView.findViewById(R.id.tv_wayBalance);
            mExistAutoData = itemView.findViewById(R.id.existAutoData_way);
            mWayName_edit = itemView.findViewById(R.id.tv_wayNameInAsset2);

            mAssetLayout = itemView.findViewById(R.id.goTo_editForAsset);
            mAssetName = itemView.findViewById(R.id.tv_editAsset);
            mDeleteAsset = itemView.findViewById(R.id.deleteAsset);
        }
    }
}
