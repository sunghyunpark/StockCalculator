package view;

import android.os.Bundle;
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

/**
 * 실 수익 계산 화면
 */
public class ProfitFragment extends BaseFragment {

    @BindView(R.id.purchase_price_edit_box) EditText purchasePriceEditBox;
    @BindView(R.id.selling_price_edit_box) EditText sellingPriceEditBox;
    @BindView(R.id.amount_edit_box) EditText amountEditBox;
    @BindView(R.id.tax_tv) TextView taxTv;
    @BindView(R.id.charge_edit_box) EditText chargeEditBox;
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

        return v;
    }

    private void init(){

    }

    private double getProfit(int purchasePrice, int sellingPrice, int amount, double tax, double charge){
        return ((double)amount * ((double)sellingPrice - (double)purchasePrice - (((double)purchasePrice * charge) / 100.0) - (((double)sellingPrice * charge) / 100.0) - (((double)sellingPrice * tax) / 100.0)));
    }

    @OnClick({R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.result_btn:
                int purchasePrice = Integer.parseInt(purchasePriceEditBox.getText().toString());
                int sellingPrice = Integer.parseInt(sellingPriceEditBox.getText().toString());
                int amount = Integer.parseInt(amountEditBox.getText().toString());
                double tax = Double.parseDouble(taxTv.getText().toString());
                double charge = Double.parseDouble(chargeEditBox.getText().toString());

                showMessage(""+getProfit(purchasePrice, sellingPrice, amount, tax, charge));

                break;
        }
    }

}
