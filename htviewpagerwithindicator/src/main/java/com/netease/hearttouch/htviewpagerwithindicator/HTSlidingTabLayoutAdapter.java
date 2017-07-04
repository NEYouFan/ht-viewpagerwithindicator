/*
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package com.netease.hearttouch.htviewpagerwithindicator;

import android.view.View;
import android.view.ViewGroup;

/**
 * 在实现ViewPager的PagerAdapter时,实现该接口从而就可以自定义tabView
 */
public interface HTSlidingTabLayoutAdapter {

    /**
     * 获取指定位置上的TabView
     * @param position TabView位置索引
     * @return 自定义的TabView
     */
    View getTabView(int position, ViewGroup parent);

    /**
     * 获取指定位置的指示器宽度,单位px,默认和TabView等宽
     * @param position abView位置索引
     * @return 指示器的宽度
     */
    int getIndicatorWidth(int position);

    /**
     * TabView切换时,视图控件的状态变化监听回调
     * @param tabView    当前状态切换的abView
     * @param position   当前tabView的位置索引
     * @param isSelected 当前tabView的状态
     */
    void onTabViewStatusChanged(View tabView, int position, boolean isSelected);

    /**
     * 将指定位置的tabView进行数据绑定
     * @param tabView  即将绑定数据的tabView
     * @param position tabView的位置索引
     */
    void onTabViewBindData(View tabView, int position);

}
