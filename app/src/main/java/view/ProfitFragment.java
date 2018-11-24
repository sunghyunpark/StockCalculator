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

import java.math.BigDecimal;
import java.text.DecimalFormat;

import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import util.Util;

/**
 * 실 수익 계산 화면
 */
public class ProfitFragment extends BaseFragment {

    private DecimalFormat format = new DecimalFormat("###,###");
    private String purchasePriceStr = "";    // 매수가 콤마 적용에 사용되는 변수
    private String sellingPriceStr = "";    // 매도가 콤마 적용에 사용되는 변수
    private String amountStr = "";    // 수량 콤마 적용에 사용되는 변수

    @BindView(R.id.purchase_price_edit_box) EditText purchasePriceEditBox;    // 매수가 EditText
    @BindView(R.id.selling_price_edit_box) EditText sellingPriceEditBox;    // 매도가 EditText
    @BindView(R.id.amount_edit_box) EditText amountEditBox;    // 수량 EditText
    @BindView(R.id.tax_tv) TextView taxTv;    // 세금 EditText
    @BindView(R.id.charge_edit_box) EditText chargeEditBox;    // 수수료 EditText
    @BindView(R.id.result_layout) ViewGroup resultLayout;   // 결과 영역
    @BindView(R.id.result_of_profit_tv) TextView resultOfProfitTv;
    @BindView(R.id.result_of_rate_tv) TextView resultOfRate;

    public static ProfitFragment newInstance() {
        ProfitFragment fragment = new ProfitFragment();
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
        View v = inflater.inflate(R.layout.fragment_profit, container, false);
        ButterKnife.bind(this, v);

        init();

        return v;
    }

    private void init(){
        // 매수가
        purchasePriceEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(purchasePriceStr)) {
                    // 숫자에 Comma를 추가해주는 메소드 호출
                    purchasePriceStr = Util.makeStringWithComma(s.toString().replace(",",""),true);
                    purchasePriceEditBox.setText(purchasePriceStr);
                    Editable e = purchasePriceEditBox.getText();
                    Selection.setSelection(e ,purchasePriceStr.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 매도가
        sellingPriceEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(sellingPriceStr)) {
                    // 숫자에 Comma를 추가해주는 메소드 호출
                    sellingPriceStr = Util.makeStringWithComma(s.toString().replace(",",""),true);
                    sellingPriceEditBox.setText(sellingPriceStr);
                    Editable e = sellingPriceEditBox.getText();
                    Selection.setSelection(e ,sellingPriceStr.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 수량
        amountEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(amountStr)) {
                    // 숫자에 Comma를 추가해주는 메소드 호출
                    amountStr = Util.makeStringWithComma(s.toString().replace(",",""),true);
                    amountEditBox.setText(amountStr);
                    Editable e = amountEditBox.getText();
                    Selection.setSelection(e ,amountStr.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /*
    * 실 수익 계산
    * 매수가 a, 매도가 b, 수량 c, 수수료 x(%), 세금 y(%)
    * 소수점은 버린다.(스펙)
    * 수식 > c(b - a - ((a * x) / 100) - ((b * x) / 100) - ((b * y) / 100))
     */
    private BigDecimal getProfit(int purchasePrice, int sellingPrice, int amount, double tax, double charge){
        BigDecimal purchasePriceBigDecimal = new BigDecimal(String.valueOf(purchasePrice));
        BigDecimal sellingPriceBigDecimal = new BigDecimal(String.valueOf(sellingPrice));
        BigDecimal chargeBigDecimal = new BigDecimal(String.valueOf(charge));

        return new BigDecimal(String.valueOf(amount)).multiply(
                sellingPriceBigDecimal.subtract(purchasePriceBigDecimal)
                        .subtract(purchasePriceBigDecimal.multiply(chargeBigDecimal)
                                .divide(new BigDecimal(String.valueOf(100))))
                        .subtract(sellingPriceBigDecimal.multiply(chargeBigDecimal)
                                .divide(new BigDecimal(String.valueOf(100))))
                        .subtract(sellingPriceBigDecimal.multiply(new BigDecimal(String.valueOf(tax)))
                                .divide(new BigDecimal(String.valueOf(100))))).setScale(0, BigDecimal.ROUND_DOWN);
    }

    /*
    * 수익률 계산
    * 매수가 a, 매도가 b
    * 수식 > ((b - a) / a) * 100
     */
    private BigDecimal getRate(int purchasePrice, int sellingPrice){
        BigDecimal purchasePriceBigDecimal = new BigDecimal(String.valueOf(purchasePrice));
        BigDecimal sellingPriceBigDecimal = new BigDecimal(String.valueOf(sellingPrice));

        return sellingPriceBigDecimal.subtract(purchasePriceBigDecimal)
                .divide(purchasePriceBigDecimal, 10, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(String.valueOf(100)))
                .setScale(1, BigDecimal.ROUND_DOWN);
    }

    /*
    * 실 수익 결과값 적용
     */
    private void setResultOfProfit(BigDecimal profit){
        BigDecimal bd = new BigDecimal(String.valueOf(0));
        BigDecimal bd2 = new BigDecimal(String.valueOf(10000000000L));    // 100억
        BigDecimal bd3 = new BigDecimal(String.valueOf(-10000000000L));    // -100억
        if(profit.compareTo(bd) == -1){
            // 0 보다 작을 경우
            resultOfProfitTv.setTextColor(getContext().getResources().getColor(R.color.colorBlue));
        }else if(profit.compareTo(bd) == 0){
            // 0 인 경우
            resultOfProfitTv.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
        }else if(profit.compareTo(bd) == 1){
            // 0보다 큰 경우
            resultOfProfitTv.setTextColor(getContext().getResources().getColor(R.color.colorRed));
        }

        if(profit.compareTo(bd2) == 1){
            resultOfProfitTv.setText("100억 초과");
        }else if(profit.compareTo(bd3) == -1){
            resultOfProfitTv.setText("-100억 미만");
        }else{
            resultOfProfitTv.setText(format.format(Long.parseLong(String.valueOf(profit))));
        }
    }

    /*
    * 수익률 결과값
     */
    private void setResultOfRate(BigDecimal rate){
        BigDecimal bd = new BigDecimal(String.valueOf(0));
        BigDecimal bd2 = new BigDecimal(String.valueOf(10000));
        BigDecimal bd3 = new BigDecimal(String.valueOf(-10000));
        if(rate.compareTo(bd) == -1){
            // 0 보다 작을 경우
            resultOfRate.setTextColor(getContext().getResources().getColor(R.color.colorBlue));
        }else if(rate.compareTo(bd) == 0){
            // 0 인 경우
            resultOfRate.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
        }else if(rate.compareTo(bd) == 1){
            // 0보다 큰 경우
            resultOfRate.setTextColor(getContext().getResources().getColor(R.color.colorRed));
        }

        if(rate.compareTo(bd2) == 1){
            resultOfRate.setText("10,000% 초과");
        }else if(rate.compareTo(bd3) == -1){
            resultOfRate.setText("-10,000% 미만");
        }else{
            resultOfRate.setText(String.valueOf(rate));
        }
    }


    @OnClick({R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.result_btn:
                if(purchasePriceEditBox.getText().toString().equals("") || sellingPriceEditBox.getText().toString().equals("") || amountEditBox.getText().toString().equals("") ||
                        chargeEditBox.getText().toString().equals("")){
                    showMessage("정보를 입력해주세요.");
                }else if((Integer.parseInt(purchasePriceEditBox.getText().toString().replace(",","")) > 3000000) || (Integer.parseInt(purchasePriceEditBox.getText().toString().replace(",","")) < 100) ||
                        (Integer.parseInt(sellingPriceEditBox.getText().toString().replace(",","")) > 3000000) || (Integer.parseInt(sellingPriceEditBox.getText().toString().replace(",","")) < 100) ||
                        (Integer.parseInt(amountEditBox.getText().toString().replace(",","")) > 10000000) || Integer.parseInt(amountEditBox.getText().toString().replace(",","")) < 1 ||
                        (Double.parseDouble(chargeEditBox.getText().toString()) > 1) || (Double.parseDouble(chargeEditBox.getText().toString()) < 0)){
                    showMessage("입력 값을 다시 확인해주세요.");
                }else {
                    int purchasePrice = Integer.parseInt(purchasePriceEditBox.getText().toString().replace(",",""));
                    int sellingPrice = Integer.parseInt(sellingPriceEditBox.getText().toString().replace(",",""));
                    int amount = Integer.parseInt(amountEditBox.getText().toString().replace(",",""));
                    double tax = Double.parseDouble(taxTv.getText().toString());
                    double charge = Double.parseDouble(chargeEditBox.getText().toString());

                    Log.d("profitResult", "\n매수가 : "+purchasePrice+"\n매도가 : "+sellingPrice+"\n수량 : "+amount+"\n세금 : "+tax+"\n수수료 : "+charge+
                    "\n실 수익 : "+getProfit(purchasePrice, sellingPrice, amount, tax, charge));

                    resultLayout.setVisibility(View.VISIBLE);
                    setResultOfProfit(getProfit(purchasePrice, sellingPrice, amount, tax, charge));
                    setResultOfRate(getRate(purchasePrice, sellingPrice));
                }

                break;
        }
    }

}
