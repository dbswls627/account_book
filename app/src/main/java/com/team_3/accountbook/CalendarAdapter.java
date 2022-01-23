package com.team_3.accountbook;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<String> daysOfMonth;
    Context context;


    public CalendarAdapter(ArrayList<String> daysOfMonth, Context context) {
        this.daysOfMonth = daysOfMonth;
        this.context=context;
    }


    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }


    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();        // LayoutParams: 부모 레이아웃 안에서 View(뷰)가 어떻게 배치될지를 정의하는 속성.   .xml 에서 'layout_~~'과 같음.
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);      // 그리드뷰 중 한 줄의 높이 설정

        return new CalendarViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));

        holder.itemView.setOnClickListener((i)->{        // 클릭 테스트용.  ex) '01월 21일' 토스트
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");      // 변환 형식 formatter 구축.
            Toast.makeText(context,  HomeActivity.selectedDate.format(formatter)+" "+daysOfMonth.get(position)+"일", Toast.LENGTH_SHORT).show();
        });
    }



    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfMonth;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);

        }
    }
}
