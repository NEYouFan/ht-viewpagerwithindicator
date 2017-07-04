package com.example.htviewpagerwithindicator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.hearttouch.htviewpagerwithindicator.HTSlidingTabLayoutAdapter;

import java.util.List;


/**
 * Created by stone on 16/4/12.
 */
public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter implements HTSlidingTabLayoutAdapter {
    static final String LOG_TAG = "SampleFragmentPagerAdapter";
    private Context mContext;
    private SparseArray<Fragment> mFragments = new SparseArray<Fragment>();
    private List<String> mDataList;
    private final int[] imgSrc = new int[]{R.drawable.tab_img_src_selector_1, R.drawable.tab_img_src_selector_2, R.drawable.tab_img_src_selector_3};

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context, List<String> dtaList) {
        super(fm);
        mContext = context;
        mDataList = dtaList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }


    public int getItemPosition(Object object) {
        return POSITION_NONE;//强制界面更新
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mDataList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments.get(position) != null)
            return mFragments.get(position);
        else {
            Fragment fragment = BlankFragment.newInstance();
            mFragments.put(position, fragment);
            return fragment;
        }
    }


    @Override
    public View getTabView(int position, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.tab_item, null);
    }

    @Override
    public int getIndicatorWidth(int position) {
        return position % 2 == 0 ? 0 : 100;
    }

    @Override
    public void onTabViewStatusChanged(View tabView, int position, boolean isSelected) {
        tabView.findViewById(R.id.textView).setSelected(isSelected);
        tabView.findViewById(R.id.imageView).setSelected(isSelected);
//        tabView.setSelected(isSelected);
    }

    @Override
    public void onTabViewBindData(View tabView, int position) {
        ((TextView) tabView.findViewById(R.id.textView)).setText(getPageTitle(position));
        ((ImageView) tabView.findViewById(R.id.imageView)).setImageResource(imgSrc[position % imgSrc.length]);
        if (mDataList.size() % 3 == 0) tabView.findViewById(R.id.mark).setVisibility(View.GONE);
    }

}
