package com.team_3.accountbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WayAndSortAdapter extends RecyclerView.Adapter<WayAndSortAdapter.CumstomViewHolder>{
    private List<String> wayList;
    private String flag;
    private touchItem mTouchItem;
    public interface touchItem{
        void clickItem(String itemName, String flag);
    }

    public WayAndSortAdapter(List<String> wayList, String flag, touchItem mTouchItem) {
        this.wayList = wayList;
        this.flag = flag;
        this.mTouchItem = mTouchItem;
    }

    @NonNull
    @Override
    public WayAndSortAdapter.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_way, parent, false);
        CumstomViewHolder holder = new CumstomViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull WayAndSortAdapter.CumstomViewHolder holder, int position) {
        holder.mWayName.setText(wayList.get(position));

        holder.itemView.setOnClickListener((view -> {
            mTouchItem.clickItem(wayList.get(position), flag);
        }));
    }


    @Override
    public int getItemCount() {
        return wayList.size();
    }


    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView mWayName;

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            mWayName = itemView.findViewById(R.id.tv_wayName);
        }
    }
}
