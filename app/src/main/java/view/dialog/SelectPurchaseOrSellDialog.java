package view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.investmentkorea.android.stockcalculator.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPurchaseOrSellDialog extends Dialog {

    private SelectPurchaseOrSellListener selectPurchaseOrSellListener;

    public SelectPurchaseOrSellDialog(Context context, SelectPurchaseOrSellListener selectPurchaseOrSellListener){
        super(context);
        this.selectPurchaseOrSellListener = selectPurchaseOrSellListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.select_purchase_or_sell_dialog);

        ButterKnife.bind(this);

    }

    public interface SelectPurchaseOrSellListener{
        void selectPurchase(String Str);
        void selectSell(String Str);
    }

    @OnClick({R.id.purchase_tv, R.id.sell_tv}) void Click(View v){
        switch (v.getId()){
            case R.id.purchase_tv:
                selectPurchaseOrSellListener.selectPurchase("매수");
                dismiss();
                break;
            case R.id.sell_tv:
                selectPurchaseOrSellListener.selectSell("매도");
                dismiss();
                break;
        }
    }
}
