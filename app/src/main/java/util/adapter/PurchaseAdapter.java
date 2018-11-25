package util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.investmentkorea.android.stockcalculator.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.PurchaseModel;
import util.Util;
import view.dialog.SelectPurchaseOrSellDialog;

public class PurchaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_BOTTOM = 2;

    private Context context;
    private ArrayList<PurchaseModel> purchaseModelArrayList;
    private DecimalFormat format = new DecimalFormat("###,###");
    private PurchaseAdapterListener purchaseAdapterListener;

    public PurchaseAdapter(Context context, ArrayList<PurchaseModel> purchaseModelArrayList, PurchaseAdapterListener purchaseAdapterListener){
        this.context = context;
        this.purchaseModelArrayList = purchaseModelArrayList;
        this.purchaseAdapterListener = purchaseAdapterListener;
    }

    public interface PurchaseAdapterListener{
        void addData(int type, int amount, long price);
        void initData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_purchase_item, parent, false);
            return new Item_VH(v);
        }else if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_purchase_header, parent, false);
            return new Header_VH(v);
        }else if(viewType == TYPE_BOTTOM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_purchase_bottom, parent, false);
            return new Bottom_VH(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    private PurchaseModel getItem(int position) {
        return purchaseModelArrayList.get(position-1);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof Item_VH) {
            final PurchaseModel currentItem = getItem(position);
            final Item_VH VHitem = (Item_VH)holder;

            VHitem.noTv.setText(String.valueOf(position));

            VHitem.amountTv.setText(String.valueOf(currentItem.getAmount()));

            VHitem.priceTv.setText(format.format(currentItem.getPrice()));

            if(isPurchase(position)){
                VHitem.selectTypeTv.setText("매수");
                VHitem.selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorRed));
            }else{
                VHitem.selectTypeTv.setText("매도");
                VHitem.selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorBlue));
            }


        }else if(holder instanceof Header_VH){
        }else if(holder instanceof Bottom_VH){
            final Bottom_VH VHitem = (Bottom_VH)holder;

            VHitem.noTv.setText(String.valueOf(purchaseModelArrayList.size()+1));
        }
    }

    /*
    해당 item 의 매수/매도 여부
     */
    private boolean isPurchase(int position){
        return getItem(position).getType() > 0;
    }

    private int getType(String typeStr){
        if(typeStr.equals("선택")){
            return 0;
        }else if(typeStr.equals("매수")){
            return 1;
        }else if(typeStr.equals("매도")){
            return -1;
        }else{
            return 0;
        }
    }


    public class Header_VH extends RecyclerView.ViewHolder{

        private Header_VH(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class Item_VH extends RecyclerView.ViewHolder{

        @BindView(R.id.no_tv) TextView noTv;
        @BindView(R.id.select_type_tv) TextView selectTypeTv;
        @BindView(R.id.amount_tv) TextView amountTv;
        @BindView(R.id.price_tv) TextView priceTv;

        private Item_VH(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class Bottom_VH extends RecyclerView.ViewHolder{
        private String purchasePriceStr = "";

        @BindView(R.id.add_btn) TextView addBtn;
        @BindView(R.id.init_btn) TextView initBtn;
        @BindView(R.id.no_tv) TextView noTv;
        @BindView(R.id.select_type_tv) TextView selectTypeTv;
        @BindView(R.id.amount_edit_box) EditText amountEditBox;
        @BindView(R.id.price_edit_box) EditText priceEditBox;

        private Bottom_VH(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);

            /*
            가격 EditText
             */
            priceEditBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(purchasePriceStr)) {
                        // 숫자에 Comma를 추가해주는 메소드 호출
                        purchasePriceStr = Util.makeStringWithComma(s.toString().replace(",",""),true);
                        priceEditBox.setText(purchasePriceStr);
                        Editable e = priceEditBox.getText();
                        Selection.setSelection(e ,purchasePriceStr.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        @OnClick({R.id.add_btn, R.id.init_btn, R.id.select_type_tv}) void Click(View v){
            switch (v.getId()){
                case R.id.add_btn:
                    /*
                    추가 버튼을 누른 뒤에는 각 bottom 요소들을 초기화 해준다.
                     */
                    if(getType(selectTypeTv.getText().toString()) == 0){
                        Util.showToast(context, "매수/매도를 선택해주세요.");
                    }else if(amountEditBox.getText().toString().trim().equals("")){
                        Util.showToast(context, "수량을 입력해주세요.");
                    }else if(priceEditBox.getText().toString().trim().equals("")){
                        Util.showToast(context, "가격을 입력해주세요.");
                    }else{
                        purchaseAdapterListener.addData(getType(selectTypeTv.getText().toString()), Integer.parseInt(amountEditBox.getText().toString()), Long.parseLong(priceEditBox.getText().toString().replace(",", "")));
                        initView();
                    }
                    break;
                case R.id.init_btn:
                    purchaseAdapterListener.initData();
                    initView();
                    break;
                case R.id.select_type_tv:
                    /*
                    선택을 누르면 매수/매도 선택 다이얼로그를 노출시키고 선택한 값들을 interface 를 통해 받아온다.
                     */
                    SelectPurchaseOrSellDialog selectPurchaseOrSellDialog = new SelectPurchaseOrSellDialog(context, new SelectPurchaseOrSellDialog.SelectPurchaseOrSellListener() {
                        @Override
                        public void selectPurchase(String Str) {
                            selectTypeTv.setText(Str);
                            selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorRed));
                        }

                        @Override
                        public void selectSell(String Str) {
                            selectTypeTv.setText(Str);
                            selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorBlue));
                        }
                    });
                    selectPurchaseOrSellDialog.show();
                    break;

            }
        }

        private void initView(){
            amountEditBox.setText(null);
            priceEditBox.setText(null);
            selectTypeTv.setText("선택");
            selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorBlack));
            noTv.setText(String.valueOf(purchaseModelArrayList.size()+1));
        }
    }

    private boolean isPositionHeader(int position){
        return position == 0;
    }

    private boolean isPositionBottom(int position){
        return position == getItemCount()-1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)){
            return TYPE_HEADER;
        }else if(isPositionBottom(position)){
            return TYPE_BOTTOM;
        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return purchaseModelArrayList.size()+2;
    }
}

