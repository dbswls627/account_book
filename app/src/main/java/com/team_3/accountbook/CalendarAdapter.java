package com.team_3.accountbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<String> daysOfMonth;
    private List<String> yyyyMM;
    private OnItemClick mCallback;
    public interface OnItemClick {
        void onClick (ArrayList<Cost> arrayList, String yyyyMM, String dd, int dayType);
    }
    ArrayList<Cost> arrayList = new ArrayList<>();

    Context context;
    String nowYM, preYearMonth, nextYearMonth;             // yyyy년 MM월
    AppDatabase db;


    public CalendarAdapter(ArrayList<String> daysOfMonth, List<String> yyyyMM, String ym, String preYm, String nextYm, Context context, OnItemClick listener) {
        this.daysOfMonth = daysOfMonth;
        this.yyyyMM = yyyyMM;
        this.context=context;
        this.mCallback = listener;
        this.nowYM = ym;
        this.preYearMonth = preYm;
        this.nextYearMonth =nextYm;
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
        if(daysOfMonth.get(position).length() == 1 || (daysOfMonth.get(position).length() == 2 && daysOfMonth.get(position).contains("?"))){      // 일이 1자리면 앞에 0을 붙여주어 데베랑 값 맞춤
            day = "0" + daysOfMonth.get(position);
        }
        day = day.replaceAll("[?!]","");
        String day1 = day;                              // 한번 더 선언을 안해주면 오류 뜸

        db = AppDatabase.getInstance(context);

        DecimalFormat decFormat = new DecimalFormat("###,###");

        if(daysOfMonth.get(position).contains("!")){        // 이전월 일 때~
            String preExpense = db.dao().getAmount(preYearMonth + " " + day1 + "일", "expense");      // 전월 해당 일 총 지출값
            String preIncome  = db.dao().getAmount(preYearMonth + " " + day1 + "일", "income");       // 전월 해당 일 총 수입값

            if(preExpense != null) { preExpense = decFormat.format(Integer.parseInt(preExpense)); }         //null 일때 변환하면 팅김
            if(preIncome != null)  { preIncome  = decFormat.format(Integer.parseInt(preIncome)); }          //null 일때 변환하면 팅김

            holder.layout.setBackground(ContextCompat.getDrawable(context, R.color.hardLightGray));
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.dayOfMonth.setText(day1);
            holder.expense.setText(preExpense);             // 날짜의 총 지출값 출력
            holder.expense.setTextColor(ContextCompat.getColor(context, R.color.redGray));
            holder.income.setText(preIncome);               // 날짜의 총 수입값 출력

            holder.itemView.setOnClickListener((i)->{       // 달력 날짜 클릭시
                arrayList.clear();
                arrayList = (ArrayList<Cost>) db.dao().getItemList(preYearMonth + " " + day1 + "일");       // 클릭한 날짜의 Cost 테이블 정보만 받아옴
                // 클릭하면 나오는 리스트뷰에 넣을 리스트와 제목테스트(월/일) 매개변수로 전달
                mCallback.onClick(arrayList, yyyyMM.get(0), day1+"일", getDayOfWeek(replaceYMText(preYearMonth)+day1));   // 만든 arrayList 를 연결해야 하지만 어댑터에서 하지 못함. interface 사용해 HomeActivity 로 리스트를 넘김.
            });
        }

        else if(daysOfMonth.get(position).contains("?")){   // 다음월 일 때~
            String nextExpense = db.dao().getAmount(nextYearMonth + " " + day1 + "일", "expense");    // 다음월 해당 일 총 지출값
            String nextIncome  = db.dao().getAmount(nextYearMonth + " " + day1 + "일", "income");     // 다음월 해당 일 총 수입값

            if(nextExpense != null) { nextExpense = decFormat.format(Integer.parseInt(nextExpense)); }      //null 일때 변환하면 팅김
            if(nextIncome != null)  { nextIncome  = decFormat.format(Integer.parseInt(nextIncome)); }       //null 일때 변환하면 팅김

            holder.layout.setBackground(ContextCompat.getDrawable(context, R.color.hardLightGray));
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.dayOfMonth.setText(day1);
            holder.expense.setTextColor(ContextCompat.getColor(context, R.color.redGray));
            holder.expense.setText(nextExpense);            // 날짜의 총 지출값 출력
            holder.income.setText(nextIncome);              // 날짜의 총 수입값 출력

            holder.itemView.setOnClickListener((i)->{       // 달력 날짜 클릭시
                arrayList.clear();
                arrayList = (ArrayList<Cost>) db.dao().getItemList(nextYearMonth + " " + day1 + "일");       // 클릭한 날짜의 Cost 테이블 정보만 받아옴

                // 클릭하면 나오는 리스트뷰에 넣을 리스트와 제목테스트(월/일) 매개변수로 전달
                mCallback.onClick(arrayList, yyyyMM.get(2), day1+"일", getDayOfWeek(replaceYMText(nextYearMonth)+day1));   // 만든 arrayList 를 연결해야 하지만 어댑터에서 하지 못함. interface 사용해 HomeActivity 로 리스트를 넘김.
            });
        }

        else{                                                // 현재월 일 때~
            String expense = db.dao().getAmount(nowYM + " " + day1 + "일", "expense");                      // 현재 해당 일 총 지출값
            String income  = db.dao().getAmount(nowYM + " " + day1 + "일", "income");                       // 현재 해당 일 총 수입값

            if(expense != null) { expense = decFormat.format(Integer.parseInt(expense)); }                  //null 일때 변환하면 팅김
            if(income != null)  { income  = decFormat.format(Integer.parseInt(income)); }                   //null 일때 변환하면 팅김

            holder.dayOfMonth.setText(daysOfMonth.get(position));
            holder.expense.setText(expense);                // 날짜의 총 지출값 출력
            holder.income.setText(income);                  // 날짜의 총 수입값 출력
            holder.itemView.setOnClickListener((i)->{       // 달력 날짜 클릭시
                arrayList.clear();
                arrayList = (ArrayList<Cost>) db.dao().getItemList(nowYM + " " + day1 + "일");       // 클릭한 날짜의 Cost 테이블 정보만 받아옴
                // 클릭하면 나오는 리스트뷰에 넣을 리스트와 제목테스트(월/일) 매개변수로 전달
                mCallback.onClick(arrayList, yyyyMM.get(1), day1+"일", getDayOfWeek(replaceYMText(nowYM)+day1));   // 만든 arrayList 를 연결해야 하지만 어댑터에서 하지 못함. interface 사용해 HomeActivity 로 리스트를 넘김.
            });
        }

    }



    @SuppressLint("LongLogTag")
    private int getDayOfWeek(String selectDate){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        Date date = null;

        try { date = dtf.parse(selectDate); }
        catch (Exception e) { Log.d("error_CalenderAdapter_getDatOfWeek()", "parsing dateFormat Error"); }
        c.setTime(date);

        return c.get(Calendar.DAY_OF_WEEK);
    }



    private String replaceYMText(String allText){
        String afterText = allText.replaceAll("[년 월]","");

        return afterText;
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
