package view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.investmentkorea.android.stockcalculator.R;

import java.text.DecimalFormat;

import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import util.Util;


public class CompoundInterestFragment extends BaseFragment {
    private String principalResult = "";    // 원금 문자열에 콤마를 나타내기 위한 변수
    private String principalBeforeStr = "";    // 원금 자릿수가 초과할 때 마지막 문자열 변수

    @BindView(R.id.principal_edit_box) EditText principalEditBox;    // 원금 editText

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
                    principalEditBox.setText(principalResult);
                    Editable e = principalEditBox.getText();
                    // 커서의 위치가 현재 입력된 위치의 끝쪽에 가게 해야 한다.

                    Selection.setSelection(e ,principalResult.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
