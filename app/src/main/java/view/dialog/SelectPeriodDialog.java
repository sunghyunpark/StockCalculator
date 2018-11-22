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

public class SelectPeriodDialog extends Dialog {

    private SelectPeriodListener selectPeriodListener;

    public SelectPeriodDialog(Context context, SelectPeriodListener selectPeriodListener){
        super(context);
        this.selectPeriodListener = selectPeriodListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.select_period_dialog);

        ButterKnife.bind(this);

    }

    public interface SelectPeriodListener{
        void selectYear(String yearStr1, String yearStr2);
        void selectMonth(String monthStr1, String monthStr2);
    }

    @OnClick({R.id.year_tv, R.id.month_tv}) void Click(View v){
        switch (v.getId()){
            case R.id.year_tv:
                selectPeriodListener.selectYear("연", "년");
                dismiss();
                break;
            case R.id.month_tv:
                selectPeriodListener.selectMonth("월", "개월");
                dismiss();
                break;
        }
    }
}
