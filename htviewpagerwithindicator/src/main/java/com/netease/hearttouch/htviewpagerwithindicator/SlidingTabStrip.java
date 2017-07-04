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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

class SlidingTabStrip extends LinearLayout {

    //    private static final int DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 0;
//    private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;
    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 8;
    private static final int DEFAULT_SELECTED_INDICATOR_COLOR = 0x00FFFFFF;

    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final int DEFAULT_DIVIDER_COLOR = 0x00FFFFFF;
//    private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;

    private final Paint mBottomBorderPaint;
    private final Paint mSelectedIndicatorPaint;
    private final Paint mDividerPaint;

    private int mSelectedPosition;
    private float mSelectionOffset;

    private SlidingTabLayout.TabColorizer mCustomTabColorizer;
    private final SimpleTabColorizer mDefaultTabColorizer;

    /************** 自定义属性 ******************/
    private int mHorizontalBorderHeight;
    private int mSelectedIndicatorHeight;
    private int mHorizontalBorderColor;
    private int mVerticalDividerHeight;
    private HTSlidingTabLayoutAdapter mSlidingTabLayoutAdapter;
    private boolean mIndicatorShow = true;

    private int mMaxVisibleViewCount = -1;
    private int mParentWidth = -1;

    /** Divider分割线的对齐方式,0-top,1-bottom(默认) */
    private int mHorizontalDividerGravity;
    /** Indicator指示器的对齐方式,0-top,1-bottom(默认) */
    private int mIndicatorGravity;

    SlidingTabStrip(Context context) {
        this(context, null);
    }

    SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, null);
        setWillNotDraw(false);

        //解析一些xml属性
        readStyleParameters(attrs);

        mDefaultTabColorizer = new SimpleTabColorizer();
        mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);
        mDefaultTabColorizer.setDividerColors(DEFAULT_DIVIDER_COLOR);

        mBottomBorderPaint = new Paint();
        mBottomBorderPaint.setColor(mHorizontalBorderColor);

        mSelectedIndicatorPaint = new Paint();

        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth(Utils.convertDpToPixel(DEFAULT_DIVIDER_THICKNESS_DIPS));
    }

    private void readStyleParameters(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HTViewPagerWithIndicator);
        mHorizontalBorderHeight = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htHorizontalBorderHeight, 0);
        mSelectedIndicatorHeight = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htIndicatorHeight,
                Utils.convertDpToPixel(SELECTED_INDICATOR_THICKNESS_DIPS));
        mVerticalDividerHeight = typedArray.getDimensionPixelOffset(R.styleable.HTViewPagerWithIndicator_htVerticalDividerHeight,
                0);
        mHorizontalBorderColor = typedArray.getColor(R.styleable.HTViewPagerWithIndicator_htHorizontalBorderColor,
                DEFAULT_DIVIDER_COLOR);
        mMaxVisibleViewCount = typedArray.getInteger(R.styleable.HTViewPagerWithIndicator_htMaxVisibleViewCount, -1);
        mHorizontalDividerGravity = typedArray.getInt(R.styleable.HTViewPagerWithIndicator_htHorizontalDividerGravity, 1);
        mIndicatorGravity = typedArray.getInt(R.styleable.HTViewPagerWithIndicator_htIndicatorGravity, 1);
        typedArray.recycle();
    }

    void setCustomTabColorizer(SlidingTabLayout.TabColorizer customTabColorizer) {
        mCustomTabColorizer = customTabColorizer;
        invalidate();
    }

    void setSelectedIndicatorColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setIndicatorColors(colors);
        invalidate();
    }

    void setDividerColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setDividerColors(colors);
        invalidate();
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }


    public void setParentWidth(int parentWidth) {
        mParentWidth = parentWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMaxVisibleViewCount <= -1 || mParentWidth < 0) {
            setGravity(Gravity.CENTER);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // 初始化界面时,mMaxVisibleViewCount标示允许可见的tabView的最大数量
        //mMaxVisibleViewCount=-1,tabView宽度为原始宽度;mMaxVisibleViewCount=0,平分屏幕宽度;mMaxVisibleViewCount>0,屏幕宽度N等份;
        final int childCount = getChildCount();
        int measureWidth = 0;//测量的总宽度
        int measureHeight = 0;//测量的最大高度
        if (childCount > 0) {
            if (mMaxVisibleViewCount == 0) {
                mMaxVisibleViewCount = childCount;
            } else if (mMaxVisibleViewCount > 0) {
                mMaxVisibleViewCount = Math.min(mMaxVisibleViewCount, childCount);
            }
            int childWith = mParentWidth / mMaxVisibleViewCount;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                if (child.getVisibility() == GONE) {
                    continue;
                }
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWith, MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
                measureWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                measureHeight = Math.max(measureHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            }
        }
        setMeasuredDimension(resolveSize(measureWidth, widthMeasureSpec), resolveSize(measureHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();

        //不能超过控件的高度值
        mVerticalDividerHeight = Math.min(mVerticalDividerHeight, height);
        mHorizontalBorderHeight = Math.min(mHorizontalBorderHeight, height);
        mSelectedIndicatorHeight = Math.min(mSelectedIndicatorHeight, height);

        final SlidingTabLayout.TabColorizer tabColorizer = mCustomTabColorizer != null
                ? mCustomTabColorizer
                : mDefaultTabColorizer;

        // Thick colored underline below the current selection
        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            if (selectedTitle == null) return;
            float left = selectedTitle.getLeft();
            float right = selectedTitle.getRight();
            int color = tabColorizer.getIndicatorColor(mSelectedPosition);

            if (mSlidingTabLayoutAdapter != null) {
                int width = selectedTitle.getWidth();
                int indicatorWidth = mSlidingTabLayoutAdapter.getIndicatorWidth(mSelectedPosition);
                if (indicatorWidth > 0 && indicatorWidth < width) {
                    left += (width - indicatorWidth) / 2.0f;
                    right -= (width - indicatorWidth) / 2.0f;
                }
            }

            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                int nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }

                // Draw the selection partway between the tabs
                View nextTitle = getChildAt(mSelectedPosition + 1);

                float nextLeft = nextTitle.getLeft();
                float nextRight = nextTitle.getRight();

                if (mSlidingTabLayoutAdapter != null) {
                    int nextWidth = nextTitle.getWidth();
                    int nextIndicatorWidth = mSlidingTabLayoutAdapter.getIndicatorWidth(mSelectedPosition + 1);
                    if (nextIndicatorWidth > 0 && nextIndicatorWidth < nextWidth) {
                        nextLeft += (nextWidth - nextIndicatorWidth) / 2.0f;
                        nextRight -= (nextWidth - nextIndicatorWidth) / 2.0f;
                    }
                }
                left = mSelectionOffset * nextLeft + (1.0f - mSelectionOffset) * left;
                right = mSelectionOffset * nextRight + (1.0f - mSelectionOffset) * right;
            }

            mSelectedIndicatorPaint.setColor(color);
            if (mIndicatorShow && mSelectedIndicatorHeight > 0) {//支持隐藏指示器视图
                if (mIndicatorGravity == 1) {
                    canvas.drawRect(left, height - mSelectedIndicatorHeight, right,
                            height, mSelectedIndicatorPaint);
                } else {
                    canvas.drawRect(left, 0, right,
                            mSelectedIndicatorHeight, mSelectedIndicatorPaint);
                }
            }

        }

        // Thin underline along the entire bottom edge
        if (mHorizontalDividerGravity == 1) {
            canvas.drawRect(-getLeft(), height - mHorizontalBorderHeight, getWidth() + getRight(), height, mBottomBorderPaint);
        } else {
            canvas.drawRect(-getLeft(), 0, getWidth() + getRight(), mHorizontalBorderHeight, mBottomBorderPaint);
        }

        if (mVerticalDividerHeight > 0) {
            // Vertical separators between the titles
            int separatorTop = (height - mVerticalDividerHeight) / 2;
            for (int i = 0; i < childCount - 1; i++) {
                View child = getChildAt(i);
                mDividerPaint.setColor(tabColorizer.getDividerColor(i));
                canvas.drawLine(child.getRight(), separatorTop, child.getRight(),
                        separatorTop + mVerticalDividerHeight, mDividerPaint);
            }
        }
    }


    public void setIndicatorViewShow(boolean indicatorShow) {
        mIndicatorShow = indicatorShow;
    }

    /**
     * Set the alpha value of the {@code color} to be the given {@code alpha} value.
     */
    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }


    public void setSlidingTabLayoutAdapter(HTSlidingTabLayoutAdapter slidingTabLayoutAdapter) {
        mSlidingTabLayoutAdapter = slidingTabLayoutAdapter;
    }

    private static class SimpleTabColorizer implements SlidingTabLayout.TabColorizer {
        private int[] mIndicatorColors;
        private int[] mDividerColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        @Override
        public final int getDividerColor(int position) {
            return mDividerColors[position % mDividerColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }

        void setDividerColors(int... colors) {
            mDividerColors = colors;
        }
    }
}