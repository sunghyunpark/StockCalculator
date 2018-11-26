package util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

            VHitem.amountTv.setText(format.format(currentItem.getAmount()));

            VHitem.priceTv.setText(format.format(currentItem.getPrice()));

            /*
            해당 item 이 매수/매도 인지 분기처리
             */
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

            /*
            입력된 item 이 없는 경우에는 '매수'로만 셋팅이 되어야하므로 선택 버튼을 사용 못하도록 설정
             */
            if(getItemCount() == 2){
                VHitem.selectTypeTv.setEnabled(false);
            }else{
                VHitem.selectTypeTv.setEnabled(true);
            }
        }
    }

    /*
    해당 item 의 매수/매도 여부
     */
    private boolean isPurchase(int position){
        return getItem(position).getType() > 0;
    }

    /*
    선택 / 매수 / 매도 에 따라 반환값을 반환한다.
     */
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
        private String amountStr = "";

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
                        if(purchasePriceStr.length() > 9){    // 9자리 이상으로 입력되었을 때
                            Util.showToast(context, "입력 값을 다시 확인해주세요.");
                            priceEditBox.setText(null);
                        }else {
                            priceEditBox.setText(purchasePriceStr);
                            Editable e = priceEditBox.getText();
                            Selection.setSelection(e ,purchasePriceStr.length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /*
            수량 EditText
             */
            amountEditBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    if (!s.toString().equals(amountStr)) {
                        // 숫자에 Comma를 추가해주는 메소드 호출
                        amountStr = Util.makeStringWithComma(s.toString().replace(",",""),true);
                        if(amountStr.length() > 10){    // 10자리 이상으로 입력되었을 때
                            Util.showToast(context, "입력 값을 다시 확인해주세요.");
                            amountEditBox.setText(null);
                        }else{
                            amountEditBox.setText(amountStr);
                            Editable e = amountEditBox.getText();
                            Selection.setSelection(e ,amountStr.length());

                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        @OnClick({R.id.add_btn, R.id.init_btn, R.id.select_type_tv}) void Click(View v){
            switch (v.getId()){
                case R.id.add_btn:
                    int type = getType(selectTypeTv.getText().toString());    // 0(선택), 1(매수), -1(매도)
                    String amountStr = amountEditBox.getText().toString().trim();
                    String priceStr = priceEditBox.getText().toString().trim();

                    /*
                    추가 버튼을 누른 뒤에는 각 bottom 요소들을 초기화 해준다.
                     */
                    if(type == 0){
                        Util.showToast(context, "매수/매도를 선택해주세요.");
                    }else if(amountStr.equals("")){
                        Util.showToast(context, "수량을 입력해주세요.");
                    }else if((type == 1) && (priceStr.equals(""))){
                        Util.showToast(context, "가격을 입력해주세요.");
                    }else{
                        try{
                            if((Integer.parseInt(priceStr.replace(",","")) > 3000000) || (Integer.parseInt(priceStr.replace(",","")) < 100)){
                                Util.showToast(context, "가격 값을 다시 확인해주세요.");
                            }else if(Integer.parseInt(amountStr.replace(",","")) > 10000000 || Integer.parseInt(amountStr.replace(",","")) < 1){
                                Util.showToast(context, "수량 값을 다시 확인해주세요.");
                            }else{
                                purchaseAdapterListener.addData(getType(selectTypeTv.getText().toString()), Integer.parseInt(amountStr.replace(",", "")), Long.parseLong(priceStr.replace(",", "")));
                                initView();
                            }
                        }catch (NumberFormatException NFE){
                            // 매도의 경우는 가격이 없기 때문에 예외처리한다.
                            purchaseAdapterListener.addData(getType(selectTypeTv.getText().toString()), Integer.parseInt(amountStr.replace(",", "")), 0);
                            initView();
                        }
                    }
                    break;
                case R.id.init_btn:
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("초기화");
                    alert.setMessage("초기화 하시겠습니까?");
                    alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            purchaseAdapterListener.initData();
                            initView();
                        }
                    });
                    alert.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.

                                }
                            });
                    alert.show();
                    break;
                case R.id.select_type_tv:
                    /*
                    선택을 누르면 매수/매도 선택 다이얼로그를 노출시키고 선택한 값들을 interface 를 통해 받아온다.
                     */
                    SelectPurchaseOrSellDialog selectPurchaseOrSellDialog = new SelectPurchaseOrSellDialog(context, new SelectPurchaseOrSellDialog.SelectPurchaseOrSellListener() {
                        @Override
                        public void selectPurchase(String Str) {
                            // 매수 선택
                            selectTypeTv.setText(Str);
                            selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorRed));

                            priceEditBox.setEnabled(true);
                        }

                        @Override
                        public void selectSell(String Str) {
                            // 매도 선택
                            selectTypeTv.setText(Str);
                            selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorBlue));

                            priceEditBox.setText(null);
                            priceEditBox.setEnabled(false);
                        }
                    });
                    selectPurchaseOrSellDialog.show();
                    break;

            }
        }

        /*
        바텀 뷰를 초기화한다.
         */
        private void initView(){
            amountEditBox.setText(null);
            priceEditBox.setText(null);
            if(getItemCount() == 2){
                // 입력된 아이템이 없는 경우 '매수'로 선택되고, 가격 EditText 는 입력 가능하게 한다.
                selectTypeTv.setText("매수");
                selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorRed));
                priceEditBox.setEnabled(true);
            }else{
                // 입력된 아이템이 있는 경우
                selectTypeTv.setText("선택");
                selectTypeTv.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
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

