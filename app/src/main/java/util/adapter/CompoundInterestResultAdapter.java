package util.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.investmentkorea.android.stockcalculator.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.CompoundInterestModel;

public class CompoundInterestResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_ITEM = 1;
    private ArrayList<CompoundInterestModel> compoundInterestModelArrayList;
    private DecimalFormat format;

    public CompoundInterestResultAdapter(ArrayList<CompoundInterestModel> compoundInterestModelArrayList){
        this.compoundInterestModelArrayList = compoundInterestModelArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_compound_interest_result_item, parent, false);
            return new Result_VH(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    private CompoundInterestModel getItem(int position) {
        return compoundInterestModelArrayList.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof Result_VH) {
            final CompoundInterestModel currentItem = getItem(position);
            final Result_VH VHitem = (Result_VH)holder;

            VHitem.noOfPeriodTv.setText(currentItem.getNo()+"");

            format = new DecimalFormat("###,###");
            VHitem.principalTv.setText(getPrincipal(currentItem.getSum()));

            VHitem.yearOrMonthRateTv.setText(currentItem.getRate()+"");
        }
    }

    private String getPrincipal(String principal){
        try{
            return format.format(Double.parseDouble(principal));
        }catch (NumberFormatException nfe){
            return principal;
        }
    }

    public class Result_VH extends RecyclerView.ViewHolder{

        @BindView(R.id.no_of_period_tv) TextView noOfPeriodTv;
        @BindView(R.id.principal_tv) TextView principalTv;
        @BindView(R.id.year_or_month_rate_tv) TextView yearOrMonthRateTv;

        private Result_VH(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return compoundInterestModelArrayList.size();
    }
}
