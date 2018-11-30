package view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.investmentkorea.android.stockcalculator.R;

import java.text.DecimalFormat;
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
    private int beforeAmount = 0;    // 직전 수량과 비교하기 위한 변수
    private DecimalFormat format = new DecimalFormat("###,###");

    @BindView(R.id.purchase_recyclerView) RecyclerView purchaseRecyclerView;
    @BindView(R.id.result_tv) TextView resultTv;

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
                // 아이템 추가
                if(type == 1){
                    beforeAmount = beforeAmount + amount;
                }else{
                    // 총 보유 수량보다 매도 수량이 많은 경우 분기처리.
                    if((beforeAmount - amount) < 0){
                        showMessage("총 수량보다 매도 수량이 많습니다.");
                        return;
                    }else{
                        beforeAmount = beforeAmount - amount;
                    }
                }

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
                    beforeAmount = 0;
                    purchaseModelArrayList.clear();
                    purchaseAdapter.notifyDataSetChanged();
                    resultTv.setVisibility(View.GONE);
                }
            }
        });

        purchaseRecyclerView.setNestedScrollingEnabled(false);
        purchaseRecyclerView.setLayoutManager(linearLayoutManager);
        purchaseRecyclerView.setAdapter(purchaseAdapter);

    }

    /*
    평균 매수 단가 구하는 메소드
     */
    private long getAveragePurchasePrice(ArrayList<PurchaseModel> purchaseModelArrayList){
        long currentAveragePrice = 0;    // 현재 평균 매수 단가
        int currentAmount = 0;    // 현재 총 수량
        int cnt = purchaseModelArrayList.size();

        for(int i=0;i<cnt;i++){
            if(purchaseModelArrayList.get(i).getType() == 1){
                // 매수
                if(i == 0){
                    // 첫번째 입력은 무조건 매수이며, 입력된 데이터들을 셋팅
                    currentAmount = purchaseModelArrayList.get(i).getAmount();
                    currentAveragePrice = (purchaseModelArrayList.get(i).getPrice() * purchaseModelArrayList.get(i).getAmount()) / currentAmount;
                }else{
                    // 첫번째 입력 이후 부터의 매수는 현재까지의 평균 단가와 현재까지의 총 수량에 같이 더해지면서 계산된다.
                    currentAveragePrice = ((currentAveragePrice * currentAmount) + (purchaseModelArrayList.get(i).getPrice() * purchaseModelArrayList.get(i).getAmount())) / (currentAmount + purchaseModelArrayList.get(i).getAmount());
                    currentAmount += purchaseModelArrayList.get(i).getAmount();
                }
            }else{
                // 매도
                currentAmount -= purchaseModelArrayList.get(i).getAmount();
            }
        }

        // 수량이 0인 경우에는 단가가 없기 때문에 0으로 반환
        return (currentAmount > 0) ? currentAveragePrice : 0;
    }

    @OnClick({R.id.result_btn}) void Click(View v){
        switch (v.getId()){
            case R.id.result_btn:
                if(purchaseModelArrayList.isEmpty()){
                    showMessage("데이터가 없습니다.");
                }else{
                    Log.d("resultPurchase", getAveragePurchasePrice(purchaseModelArrayList)+"원");
                    resultTv.setVisibility(View.VISIBLE);
                    resultTv.setText("평균단가(원) : "+format.format(getAveragePurchasePrice(purchaseModelArrayList))+"원");
                }
                break;
        }
    }



}
