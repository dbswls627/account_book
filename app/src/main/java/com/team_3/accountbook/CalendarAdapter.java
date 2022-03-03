package com.team_3.accountbook;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<String> daysOfMonth;
    private OnItemClick mCallback;
    public interface OnItemClick {
        void onClick (ArrayList<Cost> arrayList, String md);
    }
    ArrayList<Cost> arrayList = new ArrayList<>();      // arrayList2에서 item 으로 추린 리스트

    Context context;
    String ym;                  // yyyy년 MM월
    AppDatabase db;

    public CalendarAdapter(ArrayList<String> daysOfMonth, String ym, Context context, OnItemClick listener) {
        this.daysOfMonth = daysOfMonth;
        this.context=context;
        this.mCallback = listener;
        this.ym = ym;
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
        String day = daysOfMonth.get(position);
        if(daysOfMonth.get(position).length() == 1){      // 일이 1자리면 앞에 0을 붙여주어 데베랑 값 맞춤
            day = "0" + daysOfMonth.get(position);
        }
        String day1 = day;                              // 한번 더 선언을 안해주면 오류 뜸

        db = AppDatabase.getInstance(context);

        DecimalFormat decFormat = new DecimalFormat("###,###");

        String expense = db.dao().getAmount(ym + " " + day1 + "일", "expense");      // 해당 일 총 지출값
        String income  = db.dao().getAmount(ym + " " + day1 + "일", "income");       // 해당 일 총 수입값

        if(expense != null) { expense = decFormat.format(Integer.parseInt(expense)); }       //null 일때 변환하면 팅김
        if(income != null)  { income  = decFormat.format(Integer.parseInt(income)); }       //null 일때 변환하면 팅김

        holder.dayOfMonth.setText(daysOfMonth.get(position));
        holder.expense.setText(expense);     // 날짜의 총 지출값 출력
        holder.income.setText(income);     // 날짜의 총 수입값 출력
        if(daysOfMonth.get(position).contains("!")){
            holder.layout.setBackground(ContextCompat.getDrawable(context, R.color.lightGray));
            holder.dayOfMonth.setText(daysOfMonth.get(position).replace("!",""));
        }
        holder.itemView.setOnClickListener((i)->{    // 달력 날짜 클릭시
            arrayList.clear();
            arrayList = (ArrayList<Cost>) db.dao().getItemList(ym + " " + day1 + "일");       // 클릭한 날짜의 Cost 테이블 정보만 받아옴

            // 클릭하면 나오는 리스트뷰에 넣을 리스트와 제목테스트(월/일) 매개변수로 전달
            mCallback.onClick(arrayList, ym.substring(5)+day1+"일");   // 만든 arrayList 를 연결해야 하지만 어댑터에서 하지 못함. interface 사용해 HomeActivity 로 리스트를 넘김.
        });
    }



    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfMonth, expense , income;        // 일, 지출액수, 수입액수
        LinearLayout layout;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            expense = itemView.findViewById(R.id.expense);
            income = itemView.findViewById(R.id.income);
        }
    }
}
