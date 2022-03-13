package com.team_3.accountbook;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class adapter2 extends RecyclerView.Adapter<adapter2.CumstomViewHolder>{
    private ArrayList<Cost> arrayList;
    private Context context;
    private ArrayList<String> dateArray;

    public adapter2(Context context, ArrayList<Cost> arrayList, ArrayList<String> dateArray) {
        this.context = context;
        this.arrayList = arrayList;
        this.dateArray = dateArray;
    }



    @Override
    public int getItemCount() {
        return dateArray.size();
    }



    @NonNull
    @Override
    public adapter2.CumstomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data2, parent, false);
        CumstomViewHolder holder = new CumstomViewHolder(view);

        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull adapter2.CumstomViewHolder holder, int position) {
        ArrayList<Cost> costDataArray = new ArrayList<>();          // 같은 상위날짜로 묶은 결제 정보를 담는 리스트

        for (Cost cost : arrayList) {       // arrayList 의 날짜 값이랑 같은 값만 adapter 로 넘겨주기 위함
            if(cost.getUseDate().substring(0, 14).equals(dateArray.get(position))){
                costDataArray.add(cost);
            }
        }

        if(context instanceof ListInAssetActivity){
            holder.dt.setText(dateArray.get(position).replaceAll("[0-9]*년|[0-9]*월| ",""));      // "dd일"만 남기고 삭제

            String numDate = dateArray.get(position).replaceAll("[년월일 ]","");
            makeKoreanDay(holder, getDayOfWeek(numDate));   // 요일에 맞는 TextView 세팅
            holder.rv.setAdapter(new adapter(costDataArray, (adapter.OnItemClickInListInAsset) context)); // 년도 부터 일까지 매개변수로 넘김
            holder.rv.setLayoutManager(new LinearLayoutManager(context));
        }
        else{
            holder.dt.setText(dateArray.get(position));
            holder.rv.setAdapter(new adapter(costDataArray)); // 년도 부터 일까지 매개변수로 넘김
            holder.rv.setLayoutManager(new LinearLayoutManager(context));
        }


    }



    public class CumstomViewHolder extends RecyclerView.ViewHolder {
        TextView dt, dtInfo;        // 날짜(yyyy년 MM월 dd일)
        RecyclerView rv;    // 하위 리스트

        public CumstomViewHolder(@NonNull View itemView) {
            super(itemView);
            dt = itemView.findViewById(R.id.date_data2);
            dtInfo = itemView.findViewById(R.id.dayInfo_data2);
            rv = itemView.findViewById(R.id.item_rv);
        }
    }



    @SuppressLint("LongLogTag")
    private int getDayOfWeek(String selectDate){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        Date date = null;

        try { date = dtf.parse(selectDate); }
        catch (Exception e) { Log.d("error_Adapter2_getDatOfWeek()", "parsing dateFormat Error"); }
        c.setTime(date);

        return c.get(Calendar.DAY_OF_WEEK);
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void makeKoreanDay(adapter2.CumstomViewHolder holder, int dayType){
        switch (dayType){
            case 1:
                holder.dtInfo.setText("일");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekend_sunday));
                break;
            case 2:
                holder.dtInfo.setText("월");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekday));
                break;
            case 3:
                holder.dtInfo.setText("화");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekday));
                break;
            case 4:
                holder.dtInfo.setText("수");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekday));
                break;
            case 5:
                holder.dtInfo.setText("목");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekday));
                break;
            case 6:
                holder.dtInfo.setText("금");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekday));
                break;
            case 7:
                holder.dtInfo.setText("토");
                holder.dtInfo.setBackground(context.getResources().getDrawable(R.drawable.weekend_saturday));
                break;
        }

    }
}