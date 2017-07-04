package com.example.htviewpagerwithindicator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.netease.hearttouch.htviewpagerwithindicator.HTViewPagerWithIndicator;

import java.util.ArrayList;
import java.util.List;

public class DefaultSlidingTabLayoutActivity extends AppCompatActivity implements View.OnClickListener {

    private HTViewPagerWithIndicator mViewPagerForSlider;
    private PagerAdapter mPagerAdapter;
    private List<String> mStringList;

    public static void start(Activity activity, int position) {
        Intent intent = new Intent(activity, DefaultSlidingTabLayoutActivity.class);
        intent.putExtra("gravity", position);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra("gravity", 0) == 0) {
            setContentView(R.layout.activity_sliding_top_tab_layout);
        } else {
            setContentView(R.layout.activity_sliding_bottom_tab_layout);
        }
        mViewPagerForSlider = (HTViewPagerWithIndicator) findViewById(R.id.vp_for_slider);
        View button = findViewById(R.id.button_add);
        if (button != null) {
            button.setOnClickListener(this);
        }
        button = findViewById(R.id.button_change);
        if (button != null) {
            button.setOnClickListener(this);
        }
        mStringList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            mStringList.add(String.valueOf("item" + i));
        }

//        mPagerAdapter = new SamplePagerAdapter(this, mStringList);
        mPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), this, mStringList);
        mViewPagerForSlider.setAdapter(mPagerAdapter);
//        mViewPagerForSlider.setEnableTabViewScroll(false);
//        mViewPagerForSlider.setEnableViewPagerSlide(false);
//        mViewPagerForSlider.setCurrentItem(4);
        mViewPagerForSlider.setDividerColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary));
        mViewPagerForSlider.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary));
//        mViewPagerForSlider.setIndicatorViewShow(false);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                mStringList.add(String.valueOf("item" + mStringList.size()));
                if (mStringList.size() > 0) {
//                    mStringList.remove(0);
                    mPagerAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.button_change:
                for (int i = 0; i < mStringList.size(); i++) {
                    String temp = mStringList.get(i);
                    mStringList.set(i, temp + "new");
                }
                mViewPagerForSlider.notifyTabViewsDataSetChanged();
                break;
        }
    }
}
