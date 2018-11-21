package view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.investmentkorea.android.stockcalculator.R;

import butterknife.ButterKnife;

public class PurchaseFragment extends Fragment {

    public static PurchaseFragment newInstance() {
        PurchaseFragment fragment = new PurchaseFragment();
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
        View v = inflater.inflate(R.layout.fragment_purchase, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

}
