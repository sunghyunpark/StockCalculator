package com.investmentkorea.android.stockcalculator;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import util.adapter.MainViewPagerAdapter;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_tab_layout) TabLayout mainTabLayout;
    @BindView(R.id.main_pager) ViewPager mainPager;
    @BindView(R.id.adView) AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainPager);

        initAdView();
    }

    /*
    구글 애드몹 광고 
     */
    private void initAdView(){
        // addTestDevice("DEB0E0796D7384F00CEFFDACCABF99A5") 테스트 광고를 띄울 땐 logcat 에서 addTestDevice 를 필터링을 해보면 해당 디바이스 id가 나온다.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
