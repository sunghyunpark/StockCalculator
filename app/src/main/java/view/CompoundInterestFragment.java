package view;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.investmentkorea.android.stockcalculator.R;

import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CompoundInterestModel;
import util.Util;
import view.dialog.SelectPeriodDialog;


public class CompoundInterestFragment extends BaseFragment {
    private String principalResult = "";    // 원금 문자열에 콤마를 나타내기 위한 변수
    private String principalBeforeStr = "";    // 원금 자릿수가 초과할 때 마지막 문자열 변수

    @BindView(R.id.principal_edit_box) EditText principalEditBox;    // 원금 editText
    @BindView(R.id.rate_edit_box) EditText rateEditBox;    // 수익 editText
    @BindView(R.id.no_edit_box) EditText noEditBox;    // 기간 차수(년, 월) editText
    @BindView(R.id.year_month_select_tv) TextView yearMonthSelectTv;    // 연,월 TextView
    @BindView(R.id.year_month_selected_tv) TextView yearMonthSelectedTv;    // 년,개월 TextView

    public static CompoundInterestFragment newInstance() {
        CompoundInterestFragment fragment = new CompoundInterestFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compound_interest, container, false);
        ButterKnife.bind(this, v);

        init();

        return v;
    }

    private void init(){
        // 원금 EditText
        principalEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 숫자가 추가되었을때에 Comma를 추가해준다.
                if (!s.toString().equals(principalResult)) {
                    // 숫자에 Comma를 추가해주는 메소드 호출
                    principalResult = Util.makeStringWithComma(s.toString().replace(",",""),true);
                    if(principalResult.length() > 17){    // 17인 이유는 , 까지 포함된 자릿수이기 때문이다.
                        showMessage("최대 1조까지만 입력 가능합니다.");
                        principalEditBox.setText(principalBeforeStr);
                    }else{
                        principalEditBox.setText(principalResult);
                        Editable e = principalEditBox.getText();
                        principalBeforeStr = principalResult;    // 마지막으로 입력된 자릿수를 저장해둔다.
                        Selection.setSelection(e ,principalResult.length());    // 커서의 위치가 현재 입력된 위치의 끝쪽에 가게 해야 한다.
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
    * 복리 계산 결과값.
    * 최종원금 = 원금*(1 + (수익 / 100))^차수
    * A = a(1 + (r/100))^n
     */
    private long getPrincipal(long principal, double rate, int period){
        double rateAndPeriod = 1;
        if(period == 0){
            return principal;
        }else {
            for(int i=0;i<period;i++){
                rateAndPeriod *= (1 + (rate / 100));
            }
        }
        return (long) (principal * rateAndPeriod);
    }

    @OnClick({R.id.year_month_select_tv, R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.year_month_select_tv:
                SelectPeriodDialog selectPeriodDialog = new SelectPeriodDialog(getContext(), new SelectPeriodDialog.SelectPeriodListener() {
                    @Override
                    public void selectYear(String yearStr1, String yearStr2) {
                        yearMonthSelectTv.setText(yearStr1);
                        yearMonthSelectedTv.setText(yearStr2);
                    }

                    @Override
                    public void selectMonth(String monthStr1, String monthStr2) {
                        yearMonthSelectTv.setText(monthStr1);
                        yearMonthSelectedTv.setText(monthStr2);
                    }
                });
                selectPeriodDialog.show();
                break;
            case R.id.result_btn:
                long principal = Long.parseLong(principalEditBox.getText().toString().replace(",",""));    // 원금
                double rate = Double.parseDouble(rateEditBox.getText().toString());    // 고정값 수익
                double yearOrMonthRate;    // 결과값에 사용될 매년 혹은 매월 수익률
                int period = Integer.parseInt(noEditBox.getText().toString());    // 차수
                CompoundInterestModel compoundInterestModel;

                for(int i=0;i<period;i++){
                    compoundInterestModel = new CompoundInterestModel();
                    compoundInterestModel.setNo(i);
                    compoundInterestModel.setSum(principal);
                    compoundInterestModel.setRate(rate);
                    if(i==0){
                        yearOrMonthRate = 0.0;
                    }else{
                        yearOrMonthRate = ((getPrincipal(principal,rate, i) - principal) / 100);
                    }
                    Log.d("fda", "차수 : "+i+"\n원금 : "+getPrincipal(principal, rate, i) + "\n수익률 : "+yearOrMonthRate );
                }

                showMessage("원금 : "+principal+"\n수익 : "+rate + "\n차수 : "+period+"\n최종원금 : "+getPrincipal(principal, rate, period));
                break;
        }
    }

}
