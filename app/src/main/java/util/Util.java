package util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.investmentkorea.android.stockcalculator.R;

import java.text.DecimalFormat;

public class Util {

    public static void showToast(Context context, String message){
        //토스트를 중앙에 띄워준다.
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    /**
     * 문자열에 통화적용을 위해 컴마를 표기한다.
     * @param string 통화적용을 위한 문자열
     * @param ignoreZero 값이 0일 경우 공백을 리턴한다.
     * @return 통화적용이 된 문자열
     */
    public static String makeStringWithComma(String string, boolean ignoreZero)
    {
        if (string.length() == 0) {
            return "";
        }
        try {
            if (string.indexOf(".") >= 0) {
                double value = Double.parseDouble(string);
                if (ignoreZero && value == 0){
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,##0.00");
                return format.format(value);
            }else{
                long value = Long.parseLong(string);
                if (ignoreZero && value == 0){
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,###");
                return format.format(value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }
}
