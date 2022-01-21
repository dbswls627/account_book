package com.team_3.accountbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;

public class AddActivity extends AppCompatActivity {

    EditText mEditSum;
    TextView mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mEditSum = findViewById(R.id.edit_sum);
        mTestView = findViewById(R.id.testView);

        mEditSum.addTextChangedListener(new NumberTextWatcher(mEditSum));
    }


    class NumberTextWatcher implements TextWatcher {
        private DecimalFormat df;          // 10진수의 값을 원하는 형식으로 변형해 주는 DecimalFormat 클래스 객체 df
        private DecimalFormat dfnd;        // ~ ~ DecimalFormat 클래스 객체 dfnd
        private boolean hasFractionalPart;
        private EditText edit_sum;


        public NumberTextWatcher(EditText et){       // 생성자.
            df = new DecimalFormat("#,###.##");        // "#,###.##" 형식 지정
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###");         // "#,###" 형식 지정
            this.edit_sum = et;                        // 매개변수로 받은 EditText 를 edit_sum 에 저장
            hasFractionalPart = false;                 // 플래그로 사용하는 boolean 값을 false 로.
        }


        public void afterTextChanged(Editable s) {
            edit_sum.removeTextChangedListener(this);

            try {
                int inilen, endlen;                     // 초기 문자열 길이, 최종 문자열 길이
                int cp = edit_sum.getSelectionStart();  // 커서 위치값 cp (맨왼쪽부터 0ㅊ1ㅊ2ㅊ3...)

                inilen = edit_sum.getText().length();   // edittext 에 입력된 문자열 길이 (',' 추가 전 길이)
                String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");   // 입력된 값 s에서 숫자만 추출
                Number n = df.parse(v);                 // 문자열 v를 Number 형 n으로 치환 (Number: 숫자계의 Object 클래스)

                if (hasFractionalPart) {                // 소수점 여부에 따라서 포맷을 달리함.
                    edit_sum.setText(df.format(n));
                }
                else {
                    edit_sum.setText(dfnd.format(n));
                }

                endlen = edit_sum.getText().length();   // 입력된 전체 길이 (새로운 ',' 추가 길이)
                int sel = (cp + (endlen - inilen));     // ★현재 커서 위치 (???)

                if (sel > 0) {
                    edit_sum.setSelection(sel);
                }
                else {             // 커서가 맨 왼쪽으로 가게되면 맨 오른쪽으로 이동시킴
                    edit_sum.setSelection(edit_sum.getText().length());
                }
                    // -★ test 용 ★-
                mTestView.setText(edit_sum.getText());
            }
            catch (NumberFormatException nfe) {
                // do nothing?
            }
            catch (ParseException e) {
                // do nothing?
            }
            edit_sum.addTextChangedListener(this);
        }


        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 소수점을 가지고 있다면
            if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
                hasFractionalPart = true;
            }
            else {  // 소수점이 없다면
                hasFractionalPart = false;
            }
        }
    }


}