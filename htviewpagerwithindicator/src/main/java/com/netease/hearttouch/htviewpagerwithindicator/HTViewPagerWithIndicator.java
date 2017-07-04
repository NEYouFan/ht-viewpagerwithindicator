/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package com.netease.hearttouch.htviewpagerwithindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;


/**
 * 支持自定义滑动指示器的ViewPager，基于SlidingTabLayout实现
 */
public class HTViewPagerWithIndicator extends ViewPager {

    private final SlidingTabLayout mSlidingTabLayout;

    /** 是否允许页面滑动,默认允许 */
    private boolean mIsEnableSlide = true;
    private DataSetObserver mDataSetObserver;

    /** SlidingTabLayout的对齐方式,0-top(默认),1-bottom */
    private int mSlidingTabLayoutGravity;

    public HTViewPagerWithIndicator(Context context) {
        this(context, null);
    }

    public HTViewPagerWithIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 添加SlidingTabLayout
        readStyleParameters(attrs);
        ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
        lp.height = ViewPager.LayoutParams.WRAP_CONTENT;
        lp.width = ViewPager.LayoutParams.MATCH_PARENT;
        lp.gravity = mSlidingTabLayoutGravity == 0 ? Gravity.TOP : Gravity.BOTTOM;
        mSlidingTabLayout = new SlidingTabLayout(getContext(), attrs);
        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mSlidingTabLayout.notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        };
        addView(mSlidingTabLayout, -1, lp);
    }


    private void readStyleParameters(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HTViewPagerWithIndicator);
        mSlidingTabLayoutGravity = typedArray.getInt(R.styleable.HTViewPagerWithIndicator_htTabLayoutGravity, 0);
        typedArray.recycle();
    }

    /** 是否允许ViewPager页面横向滑动,默认允许 */
    public void setEnableViewPagerSlide(boolean enableSlide) {
        mIsEnableSlide = enableSlide;
    }

    /** 是否允许TabView横向滑动,默认允许 */
    public void setEnableTabViewScroll(boolean enableScroll) {
        mSlidingTabLayout.setEnableScroll(enableScroll);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        if (child instanceof SlidingTabLayout) {
            final LayoutParams lp = (LayoutParams) params;
            //防止SlidingTabLayout在pagerAdapter替换时被remove掉{@link ViewPager#removeNonDecorViews}
            lp.isDecor |= true;
        }
        super.addView(child, index, params);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        mSlidingTabLayout.setViewPager(this);//该方法需要在PagerAdapter设置以后调用
    }

    /**
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(SlidingTabLayout.TabColorizer tabColorizer) {
        mSlidingTabLayout.getTabStrip().setCustomTabColorizer(tabColorizer);
    }


    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mSlidingTabLayout.getTabStrip().setSelectedIndicatorColors(colors);
    }

    /** 设置滑动指示器是否可见 */
    public void setIndicatorViewShow(boolean indicatorShow) {
        mSlidingTabLayout.getTabStrip().setIndicatorViewShow(indicatorShow);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        mSlidingTabLayout.getTabStrip().setDividerColors(colors);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsEnableSlide && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mIsEnableSlide && super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getAdapter() != null && mDataSetObserver != null ) {
            getAdapter().unregisterDataSetObserver(mDataSetObserver);
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getAdapter() != null && mDataSetObserver != null ) {
            getAdapter().registerDataSetObserver(mDataSetObserver);
        }
    }

    /** TabView数据内容改变,轻量级的数据内容改变 */
    public void notifyTabViewsDataSetChanged() {
        mSlidingTabLayout.notifyTabViewsDataSetChanged();
    }

    /**
     * 指定位置的TabView数据内容改变,轻量级的数据内容改变
     * @param position TabView的索引
     */
    public void notifyTabViewDataSetChanged(int position) {
        mSlidingTabLayout.notifyTabViewDataSetChanged(position);
    }

}
