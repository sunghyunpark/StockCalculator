package util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import view.CompoundInterestFragment;
import view.ProfitFragment;
import view.PurchaseFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3;

    public MainViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return ProfitFragment.newInstance();
            case 1:
                return PurchaseFragment.newInstance();
            case 2:
                return CompoundInterestFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "실 수익 계산";
            case 1:
                return "평균 매수단가 계산";
            case 2:
                return "복리 계산";
            default:
                return null;
        }
    }
}
