/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netease.hearttouch.htviewpagerwithindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.TextView;


/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p/>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p/>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link HTViewPagerWithIndicator#setSelectedIndicatorColors(int...)} and {@link HTViewPagerWithIndicator#setDividerColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p/>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
class SlidingTabLayout extends HorizontalScrollView {

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);

    }

    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private int mTabViewLayoutId;
    private int mTabViewTextViewId;

    private ViewPager mViewPager;
    private final SlidingTabStrip mTabStrip;

    /**************************************/
    /** 是否允许滑动 */
    private boolean isEnableScroll = true;
    /** 自定义的TabView数据源对象 */
    private HTSlidingTabLayoutAdapter mSlidingTabLayoutAdapter;
    /** 记录最后被选中的tabView的位置 */
    private int mLastPosition = 0;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        super(context, null);
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        setOverScrollMode(OVER_SCROLL_NEVER);
        setClipToPadding(false);
        setClipChildren(false);
        readStyleParameters(attrs);

        mTabStrip = new SlidingTabStrip(context, attrs);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        mTabStrip.setGravity(Gravity.CENTER);

    }

    private void readStyleParameters(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HTViewPagerWithIndicator);
        mTitleOffset = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htTabViewOffset, 0);
        //读取padding属性
        int paddingLeft = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htTabLayoutPaddingLeft, 0);
        int paddingRight = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htTabLayoutPaddingRight, 0);
        int paddingTop = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htTabLayoutPaddingTop, 0);
        int paddingBottom = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htTabLayoutPaddingBottom, 0);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        //读取背景色
        Drawable background = typedArray.getDrawable(R.styleable.HTViewPagerWithIndicator_htTabLayoutBackground);
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(background);
            } else {
                setBackgroundDrawable(background);
            }
        }
        typedArray.recycle();
    }


    public SlidingTabStrip getTabStrip() {
        return mTabStrip;
    }

    /**
     * 是否允许SlidingTabLayout滑动
     * @param enableScroll true 允许滑动,默认值  false 不允许滑动
     */
    public void setEnableScroll(boolean enableScroll) {
        isEnableScroll = enableScroll;
    }


    /**
     * Set the custom layout to be inflated for the tab views.
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }


    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    /** ViewPager数据源改变时调用 */
    public void notifyDataSetChanged() {
        mTabStrip.removeAllViews();
        populateTabStrip();
    }


    /** TabView数据内容改变 */
    public void notifyTabViewsDataSetChanged() {
        if (mSlidingTabLayoutAdapter == null) return;
        int childCount = mTabStrip.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mSlidingTabLayoutAdapter.onTabViewBindData(mTabStrip.getChildAt(i), i);
        }
    }

    /**
     * 指定位置的TabView数据内容改变
     * @param position TabView的索引
     */
    public void notifyTabViewDataSetChanged(int position) {
        if (checkTabLayoutPosition(position)) return;
        if (mSlidingTabLayoutAdapter != null) {
            mSlidingTabLayoutAdapter.onTabViewBindData(mTabStrip.getChildAt(position), position);
        }
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();

        if (adapter instanceof HTSlidingTabLayoutAdapter) {
            mSlidingTabLayoutAdapter = (HTSlidingTabLayoutAdapter) adapter;
            mTabStrip.setSlidingTabLayoutAdapter(mSlidingTabLayoutAdapter);
        }
        final OnClickListener tabClickListener = new TabClickListener();

        mLastPosition = mViewPager.getCurrentItem();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            if (mSlidingTabLayoutAdapter == null) {
                TextView tabTitleView = null;

                if (mTabViewLayoutId != 0) {
                    // If there is a custom tab view layout id set, try and inflate it
                    tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                            false);
                    tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
                }

                if (tabView == null) {
                    tabView = createDefaultTabView(getContext());
                }

                if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                    tabTitleView = (TextView) tabView;
                }

                if (tabTitleView != null) {
                    tabTitleView.setText(adapter.getPageTitle(i));
                }
                mTabStrip.addView(tabView);
            } else {
                tabView = mSlidingTabLayoutAdapter.getTabView(i, mTabStrip);
                if (tabView == null) {
                    throw new NullPointerException("the tabView is null");
                }
                ViewGroup.LayoutParams lp = tabView.getLayoutParams();
                if (lp == null) {
                    lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                if (tabView.getParent() != null) {
                    ((ViewGroup) tabView.getParent()).removeView(tabView);
                }
                mSlidingTabLayoutAdapter.onTabViewBindData(tabView, i);
                mTabStrip.addView(tabView, lp);
            }
            tabView.setOnClickListener(tabClickListener);

            if (mSlidingTabLayoutAdapter == null) {
                tabView.setSelected(i == mLastPosition);
            } else {
                mSlidingTabLayoutAdapter.onTabViewStatusChanged(tabView, i, i == mLastPosition);
            }
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //为childView提供绘制内容宽度参考,因为是HorizontalScrollView,childView的widthMeasureSpec是0
        mTabStrip.setParentWidth(resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec) - getPaddingLeft() - getPaddingRight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            final ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnableScroll) return super.onTouchEvent(ev);
        else {
            return ev.getAction() == MotionEvent.ACTION_MOVE;
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        if (checkTabLayoutPosition(tabIndex)) return;
        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (targetScrollX <= 0) return;//此处添加判断,避免不必要滚动

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }
            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (checkTabLayoutPosition(position)) return;
            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mLastPosition != position) {
                if (checkTabLayoutPosition(position)) return;
                if (mSlidingTabLayoutAdapter == null) {
                    if (!checkTabLayoutPosition(position)) {
                        mTabStrip.getChildAt(position).setSelected(true);
                    }
                    if (!checkTabLayoutPosition(mLastPosition)) {
                        mTabStrip.getChildAt(mLastPosition).setSelected(false);
                    }
                } else {
                    if (!checkTabLayoutPosition(position)) {
                        mSlidingTabLayoutAdapter.onTabViewStatusChanged(mTabStrip.getChildAt(position), position, true);
                    }
                    if (!checkTabLayoutPosition(mLastPosition)) {
                        mSlidingTabLayoutAdapter.onTabViewStatusChanged(mTabStrip.getChildAt(mLastPosition), mLastPosition, false);
                    }
                }
                mLastPosition = position;
            }

        }

    }

    /** 确保索引位置在正确的范围之内 */
    private boolean checkTabLayoutPosition(int position) {
        int tabStripChildCount = mTabStrip.getChildCount();
        return (tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount);
    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

}
