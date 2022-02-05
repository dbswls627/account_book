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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<String> daysOfMonth;
    Context context;
    AppDatabase db;
    private OnItemClick mCallback;
    ArrayList<item> arrayList = new ArrayList<>();
    ArrayList<Cost> arrayList2 = new ArrayList<>();
    public interface OnItemClick {
        void onClick (ArrayList<item> arrayList);
    }

    public CalendarAdapter(ArrayList<String> daysOfMonth, Context context,OnItemClick listener) {
        this.daysOfMonth = daysOfMonth;
        this.context=context;
        this.mCallback = listener;
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
        db =AppDatabase.getInstance(context);
        holder.itemView.setOnClickListener((i)->{        // 클릭 테스트용.  ex) '01월 21일' 토스트
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY년 MM월");      // 변환 형식 formatter 구축.
            arrayList2 = (ArrayList<Cost>) db.dao().getDate(HomeActivity.selectedDate.format(formatter)+" "+daysOfMonth.get(position)+"일"); //클릭한 날짜만 받아옴
            arrayList.clear();
            arrayList2.forEach(it -> {  //클릭한 날짜의 값을 adapter에 연결할 arrayList에 뿌려줌
                arrayList.add(new item(it.getUseDate(), it.getContent(), it.getAmount())); //받아온 Cost 데이터를 item에 맞게 뿌려줌
            });
            mCallback.onClick(arrayList);   //만든 arrayList를 연결 해야하는데 어뎁터에서 하지못하고 Homeactivity에서 해줘야 하기 때문에 interface 사용
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
