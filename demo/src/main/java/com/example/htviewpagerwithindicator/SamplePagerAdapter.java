package com.example.htviewpagerwithindicator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
public class SamplePagerAdapter extends PagerAdapter implements HTSlidingTabLayoutAdapter {
    static final String LOG_TAG = "SamplePagerAdapter";
    private Context mContext;
    private List<String> mDataList;
    private final int[] imgSrc = new int[]{R.drawable.tab_img_src_selector_1, R.drawable.tab_img_src_selector_2, R.drawable.tab_img_src_selector_3};

    public SamplePagerAdapter(Context context, List<String> dtaList) {
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
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mDataList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        container.addView(view);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(getPageTitle(position));
        Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
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
