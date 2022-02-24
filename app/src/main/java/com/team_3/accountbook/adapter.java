package com.team_3.accountbook;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.CumstomViewHolder>{
    private DecimalFormat myFormatter = new DecimalFormat("###,###");
    private ArrayList<Cost> arrayList;
    private Context context;
    private String formatAmount = "";
    private OnItemClickInListInAsset mOnItemClick;
    public interface OnItemClickInListInAsset {
        void onClick (Cost cost);
    }

    public adapter(ArrayList<Cost> arrayList) {
        this.arrayList = arrayList;
    }

    public adapter(ArrayList<Cost> arrayList, OnItemClickInListInAsset OnItemClick) {
        this.arrayList = arrayList;
        this.mOnItemClick = OnItemClick;
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    @NonNull
    @Override
    public adapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        CumstomViewHolder holder = null;

        if (context instanceof HomeActivity){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_detail_forhome, parent, false);
            holder = new CumstomViewHolder(view);
        }
        else if (context instanceof ListInAssetActivity){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_detail_forasset, parent, false);
            holder = new CumstomViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data, parent, false);
            holder = new CumstomViewHolder(view);
        }

        return holder;
    }



    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull adapter.CumstomViewHolder holder, int position) {
        if (context instanceof HomeActivity){       // HomeActivity 에서의 세팅
            holder.mSortName_dh.setText(arrayList.get(position).getSortName());
            holder.mBody_dh.setText(arrayList.get(position).getContent());
            holder.mTime_dh.setText(arrayList.get(position).getUseDate().substring(14, 19));
            formatAmount = myFormatter.format(arrayList.get(position).getAmount());
            holder.mAmount_dh.setText(formatAmount + "원");
            holder.mWayName_dh.setText(arrayList.get(position).getFK_wayName());

            if(arrayList.get(position).getDivision().equals("income")){ holder.mAmount_dh.setTextColor(ContextCompat.getColor(context, R.color.green)); }
            else if(arrayList.get(position).getDivision().equals("expense")){ holder.mAmount_dh.setTextColor(ContextCompat.getColor(context, R.color.red)); }
        }
        else if (context instanceof ListInAssetActivity){   // ListInAssetActivity 에서의 세팅
            holder.mSortName_da.setText(arrayList.get(position).getSortName());
            holder.mBody_da.setText(arrayList.get(position).getContent());
            holder.mTime_da.setText(arrayList.get(position).getUseDate().substring(14, 19));
            formatAmount = myFormatter.format(arrayList.get(position).getAmount());
            holder.mAmount_da.setText(formatAmount + "원");
            formatAmount = myFormatter.format(arrayList.get(position).getBalance());
            holder.mBalance_da.setText("("+formatAmount+")");

            if(arrayList.get(position).getDivision().equals("income")){ holder.mAmount_da.setTextColor(ContextCompat.getColor(context, R.color.green)); }
            else if(arrayList.get(position).getDivision().equals("expense")){ holder.mAmount_da.setTextColor(ContextCompat.getColor(context, R.color.red)); }
            holder.itemView.setOnClickListener((View -> {
                Cost cost = arrayList.get(position);
                mOnItemClick.onClick(cost);
            }));
        }
        else{       // 그 외 세팅
            holder.dt.setText(arrayList.get(position).getUseDate().substring(14, 19));
            holder.bd.setText(arrayList.get(position).getContent());
            formatAmount = myFormatter.format(arrayList.get(position).getAmount());
            holder.amt.setText(formatAmount + "원");

            if(arrayList.get(position).getDivision().equals("income")){ holder.amt.setTextColor(ContextCompat.getColor(context, R.color.green)); }
            else if(arrayList.get(position).getDivision().equals("expense")){ holder.amt.setTextColor(ContextCompat.getColor(context, R.color.red)); }
        }

        // 리스트 항목 클릭시~
        if (context instanceof MainActivity) {      // 호출한 액티비티가 MainActivity(메세지 액티비티)일 경우
            holder.itemView.setOnClickListener((view -> {
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("amount", arrayList.get(position).getAmount());
                intent.putExtra("date", arrayList.get(position).getUseDate());
                intent.putExtra("body", arrayList.get(position).getContent());
                intent.putExtra("ms", arrayList.get(position).getMs());

                context.startActivity(intent);      // ~AddActivity 로 넘어감
            }));
        }
    }



    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt;        // 시간(HH:mm)
        TextView bd;        // 내용
        TextView amt;       // 금액

        TextView mSortName_dh, mBody_dh, mTime_dh, mAmount_dh, mWayName_dh;
        TextView mSortName_da, mBody_da, mTime_da, mAmount_da, mBalance_da;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            dt = itemView.findViewById(R.id.dt);
            bd = itemView.findViewById(R.id.bd);
            amt = itemView.findViewById(R.id.amt);

            mSortName_dh = itemView.findViewById(R.id.detailHome_sortName);
            mBody_dh = itemView.findViewById(R.id.detailHome_body);
            mTime_dh = itemView.findViewById(R.id.detailHome_time);
            mAmount_dh = itemView.findViewById(R.id.detailHome_amount);
            mWayName_dh = itemView.findViewById(R.id.detailHome_wayName);

            mSortName_da = itemView.findViewById(R.id.detailAsset_sortName);
            mBody_da = itemView.findViewById(R.id.detailAsset_body);
            mTime_da = itemView.findViewById(R.id.detailAsset_time);
            mAmount_da = itemView.findViewById(R.id.detailAsset_amount);
            mBalance_da = itemView.findViewById(R.id.detailAsset_balance);
        }
    }
}