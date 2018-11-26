package view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.investmentkorea.android.stockcalculator.R;

import java.util.ArrayList;

import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.PurchaseModel;
import util.adapter.PurchaseAdapter;

/**
 * 평균 매수 단가 화면
 */
public class PurchaseFragment extends BaseFragment {

    private ArrayList<PurchaseModel> purchaseModelArrayList;
    private PurchaseAdapter purchaseAdapter;
    private PurchaseModel purchaseModel;

    @BindView(R.id.purchase_recyclerView) RecyclerView purchaseRecyclerView;

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

        init();

        return v;
    }

    private void init(){
        purchaseModelArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        purchaseAdapter = new PurchaseAdapter(getContext(), purchaseModelArrayList, new PurchaseAdapter.PurchaseAdapterListener() {
            @Override
            public void addData(int type, int amount, long price) {
                purchaseModel = new PurchaseModel();
                purchaseModel.setType(type);
                purchaseModel.setAmount(amount);
                purchaseModel.setPrice(price);

                purchaseModelArrayList.add(purchaseModel);
                purchaseAdapter.notifyDataSetChanged();
            }

            @Override
            public void initData() {
                if(!purchaseModelArrayList.isEmpty()){
                    purchaseModelArrayList.clear();
                    purchaseAdapter.notifyDataSetChanged();
                }
            }
        });

        purchaseRecyclerView.setLayoutManager(linearLayoutManager);
        purchaseRecyclerView.setAdapter(purchaseAdapter);

    }

    private String getAveragePurchasePrice(ArrayList<PurchaseModel> purchaseModelArrayList){
        long currentAveragePrice = 0;    // 현재 평균 매수 단가
        int currentAmount = 0;    // 현재 총 수량
        int cnt = purchaseModelArrayList.size();

        for(int i=0;i<cnt;i++){
            if(purchaseModelArrayList.get(i).getType() == 1){
                // 매수
                if(i == 0){
                    currentAmount = purchaseModelArrayList.get(i).getAmount();
                    currentAveragePrice = (purchaseModelArrayList.get(i).getPrice() * purchaseModelArrayList.get(i).getAmount()) / currentAmount;
                }else{
                    currentAveragePrice = ((currentAveragePrice * currentAmount) + (purchaseModelArrayList.get(i).getPrice() * purchaseModelArrayList.get(i).getAmount())) / (currentAmount + purchaseModelArrayList.get(i).getAmount());
                    currentAmount += purchaseModelArrayList.get(i).getAmount();
                }
            }else{
                // 매도
                currentAmount -= purchaseModelArrayList.get(i).getAmount();
            }
        }

        return String.valueOf(currentAveragePrice);
    }

    @OnClick({R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.result_btn:
                if(purchaseModelArrayList.isEmpty()){
                    showMessage("데이터가 없습니다.");
                }else{
                    Log.d("resultPurchase", getAveragePurchasePrice(purchaseModelArrayList)+"원");

                }
                break;
        }
    }



}
