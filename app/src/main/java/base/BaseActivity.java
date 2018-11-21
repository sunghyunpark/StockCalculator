package base;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.investmentkorea.android.stockcalculator.R;

import util.Util;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void showLoading(){
        hideLoading();
        mProgressDialog = Util.showLoadingDialog(this);
    }

    protected void hideLoading(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    protected void showMessage(String message){
        Util.showToast(getApplicationContext(), message);
    }
}
