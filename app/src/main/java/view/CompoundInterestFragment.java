package view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CompoundInterestModel;
import util.Util;
import util.adapter.CompoundInterestResultAdapter;
import view.dialog.SelectPeriodDialog;


public class CompoundInterestFragment extends BaseFragment {
    // double 로 자바에서 큰 자릿수의 계산을 하게되면 오차가 생기는 이슈가 있어 BigDecimal 로 계산한다.
    private static final BigDecimal MAX_OF_PRINCIPAL = new BigDecimal(String.valueOf(100000000000L));    // 1000억
    private static final BigDecimal MAX_OF_RATE = new BigDecimal(String.valueOf(1000000));    // 100만
    private String principalResult = "";    // 원금 문자열에 콤마를 나타내기 위한 변수
    private String principalBeforeStr = "";    // 원금 자릿수가 초과할 때 마지막 문자열 변수
    private boolean isYearMode = true;

    private ArrayList<CompoundInterestModel> compoundInterestModelArrayList;
    private CompoundInterestResultAdapter compoundInterestResultAdapter;

    @BindView(R.id.principal_edit_box) EditText principalEditBox;    // 원금 editText
    @BindView(R.id.rate_edit_box) EditText rateEditBox;    // 수익 editText
    @BindView(R.id.no_edit_box) EditText noEditBox;    // 기간 차수(년, 월) editText
    @BindView(R.id.year_month_select_tv) TextView yearMonthSelectTv;    // 연,월 TextView
    @BindView(R.id.year_month_selected_tv) TextView yearMonthSelectedTv;    // 년,개월 TextView
    @BindView(R.id.result_recyclerView) RecyclerView resultRecyclerView;    // 결과 recyclerView

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
        compoundInterestModelArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        compoundInterestResultAdapter = new CompoundInterestResultAdapter(compoundInterestModelArrayList);

        resultRecyclerView.setLayoutManager(linearLayoutManager);
        resultRecyclerView.setNestedScrollingEnabled(false);
        resultRecyclerView.setAdapter(compoundInterestResultAdapter);

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
                    if(principalResult.length() > 13){    // 17인 이유는 , 까지 포함된 자릿수이기 때문이다.
                        showMessage("최대 10억단위까지 입력 가능합니다.");
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

        // 고정 수익 EditText
        rateEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 고정 수익의 값을 30까지만 입력
                if(s.toString().length() > 0){
                    if(Integer.parseInt(s.toString()) > 30){
                        rateEditBox.setText(null);
                        showMessage("수익은 최대 30%까지만 입력 가능합니다.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 년, 개월 EditText
        noEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 년/개월 값을 50까지만 입력
                if(s.toString().length() > 0){
                    if(Integer.parseInt(s.toString()) > 50){
                        noEditBox.setText(null);
                        showMessage("최대 50까지만 입력 가능합니다.");
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
    private BigDecimal getPrincipal(long principal, double rate, int period){
        double rateAndPeriod = 1.0;

        if(period == 0){    // 0회차의 경우 원금과 동일하므로 원금을 반환
            return new BigDecimal(String.valueOf(principal));
        }else {    // 1회차 이상부터는 수식에 맞게 n승.
            for(int i=0;i<period;i++){
                rateAndPeriod *= (1 + (rate / 100));
            }
        }
        return new BigDecimal(String.valueOf(principal)).multiply(new BigDecimal(String.valueOf(rateAndPeriod)));
    }

    /*
    * 수익률 = ((최종원금 - 원금) / 원금) * 100
    * result = ((An - A) / A) * 100
    *
     */
    private BigDecimal getYearOrMonthRate(BigDecimal resultOfPrincipal, long principal, int period){
        BigDecimal principalBigDecimal = new BigDecimal(String.valueOf(principal));
        if(period == 0){  // 0회차인 경우 원금과 동일하므로 수익률은 0%
            return new BigDecimal(String.valueOf(0));
        }else{  // 1회차 이상
            return resultOfPrincipal.subtract(principalBigDecimal)
                    .divide(principalBigDecimal)
                    .multiply(new BigDecimal(String.valueOf(100)))
                    .setScale(1, BigDecimal.ROUND_DOWN);
        }
    }

    @OnClick({R.id.year_month_select_tv, R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.year_month_select_tv:
                SelectPeriodDialog selectPeriodDialog = new SelectPeriodDialog(getContext(), new SelectPeriodDialog.SelectPeriodListener() {
                    @Override
                    public void selectYear(String yearStr1, String yearStr2) {
                        // 연-년 을 선택한 경우 동시에 '연/년'으로 변경한다.
                        isYearMode = true;
                        yearMonthSelectTv.setText(yearStr1);
                        yearMonthSelectedTv.setText(yearStr2);
                    }

                    @Override
                    public void selectMonth(String monthStr1, String monthStr2) {
                        // 월-개월 을 선택한 경우 동시에 '월/개월'로 변경한다.
                        isYearMode = false;
                        yearMonthSelectTv.setText(monthStr1);
                        yearMonthSelectedTv.setText(monthStr2);
                    }
                });
                selectPeriodDialog.show();
                break;
            case R.id.result_btn:
                if(principalEditBox.getText().toString().trim().equals("") || rateEditBox.getText().toString().trim().equals("") || noEditBox.getText().toString().trim().equals("")){
                    showMessage("정보를 입력해주세요.");
                }else{
                    if(!compoundInterestModelArrayList.isEmpty())
                        compoundInterestModelArrayList.clear();

                    long principal = Long.parseLong(principalEditBox.getText().toString().replace(",",""));    // 원금
                    double rate = Double.parseDouble(rateEditBox.getText().toString());    // 고정값 수익
                    BigDecimal yearOrMonthRate;   // 결과값에 사용될 매년 혹은 매월 수익률
                    int period = Integer.parseInt(noEditBox.getText().toString());    // 차수

                    // 첫번째 item 은 설명 문구 이므로 먼저 리스트에 넣는다.
                    CompoundInterestModel emptyModel = new CompoundInterestModel();
                    String yearOrMonth = isYearMode ? "년" : "개월";
                    emptyModel.setNo(yearOrMonth);
                    emptyModel.setSum("원금");
                    emptyModel.setRate("수익률(%)");
                    compoundInterestModelArrayList.add(emptyModel);

                    CompoundInterestModel compoundInterestModel;
                    for(int i=1;i<=period+1;i++){
                        compoundInterestModel = new CompoundInterestModel();
                        compoundInterestModel.setNo(String.valueOf(i-1));

                        if(getPrincipal(principal, rate, i-1).compareTo(MAX_OF_PRINCIPAL) == 1){
                            compoundInterestModel.setSum("1000억 초과");
                        }else{
                            compoundInterestModel.setSum(String.valueOf(getPrincipal(principal,rate, i-1)));
                        }

                        yearOrMonthRate = getYearOrMonthRate(getPrincipal(principal,rate, i-1), principal, i-1);
                        if(yearOrMonthRate.compareTo(MAX_OF_RATE) == 1){
                            compoundInterestModel.setRate("1,000,000% 초과");
                        }else{
                            compoundInterestModel.setRate(String.valueOf(yearOrMonthRate)+"%");
                        }
                        compoundInterestModelArrayList.add(compoundInterestModel);
                        Log.d("calculateResult", "\n차수 : "+(i-1)+"\n원금 : "+principal+"\n최종원금 : "+getPrincipal(principal, rate, i) + "\n수익률 : "+yearOrMonthRate );
                    }

                    resultRecyclerView.setVisibility(View.VISIBLE);
                    compoundInterestResultAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
