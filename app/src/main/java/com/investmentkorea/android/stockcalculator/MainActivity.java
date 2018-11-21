package com.investmentkorea.android.stockcalculator;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import util.adapter.MainViewPagerAdapter;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_tab_layout) TabLayout mainTabLayout;
    @BindView(R.id.main_pager) ViewPager mainPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainPager);
    }
}
