## HTViewPagerWithIndicator使用文档
`HTViewPagerWithIndicator`是一个基于[SlidingTabLayout](http://developer.android.com/intl/zh-cn/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html)和[ViewPager](https://developer.android.com/intl/zh-cn/reference/android/support/v4/view/ViewPager.html)的能够自定义滑动指示器视图的页面管理控件。根据[README](https://github.com/NEYouFan/ht-viewpagerwithindicator/blob/master/README.md)文档，基本了解了`HTViewPagerWithIndicator`的特点以及快速使用的方法。
### 1. 自定义控件属性设置
* `htHorizontalBorderHeight`：`SlidingTabLayout`底部横向分割线的高度，可选，默认`0`；
* `htHorizontalBorderColor`：`SlidingTabLayout`底部横向分割线的颜色，可选；
* `htIndicatorHeight`：滑动指示器的高度，可选，默认`8dp`；
* `htVerticalDividerHeight`：`TabView`间分割线的高度，可选，默认是`0`，宽度默认`1dp`；
* `htTabViewOffset`：`TabView`滑动时的偏移量，可选，默认`0`；
* `htMaxVisibleViewCount`：控件可见`TabView`的数量`n`，可选。`n`默认值是`-1`，表示`TabView`自适应宽度；若`n=0`，则全部的`TabView`平分`SlidingTabLayout`的宽度；若N大于`0`，则`TabView`的宽度为`SlidingTabLayout`宽度的`1/n`，多出的部分将不可见。
* `htTabLayoutPaddingTop`：设置`SlidingTabLayout`的`PaddingTop`值。`htTabLayoutPaddingLeft`、`htTabLayoutPaddingBottom`、`htTabLayoutPaddingRight`类似；
* `htTabLayoutBackground`：设置`SlidingTabLayout`的背景（注意不要再getTabView返回的view中设置背景色，会覆盖指示器）；
* `htTabLayoutGravity`：设置`SlidingTabLayout `的对齐位置,`top`(默认)或者`bottom`；
* `htIndicatorGravity`：设置`Indicator`指示器的对齐位置,`top`或者`bottom`(默认)；
* `htHorizontalDividerGravity`：底部横向分割线的对齐位置,`top`或者`bottom`(默认)。

### 2. `TabView`视图的数据源接口
 用户在创建`ViewPager`的`PagerAdapter`时，通过实现`HTSlidingTabLayoutAdapter`接口，可以实现自定义`TabView`样式。其中包括`TabView`的视图、`TabView`的数据绑定、滑动指示器的宽度、以及`TabView`切换回调等。
#####  `HTSlidingTabLayoutAdapter`接口方法

* `View getTabView(int position, ViewGroup parent)`
>获取指定位置上的`TabView`视图。`position`为`TabView`的索引，`parent`为父容器。如果要显示指示器，不要再返回的view中设置背景色，会覆盖指示器。

* `int getIndicatorWidth(int position)`
>获取指定位置的指示器宽度，单位`px`，默认和`TabView`等宽。`position`为`TabView`的索引。

* `void onTabViewBindData(View tabView, int position)`
>将指定位置的`tabView`进行数据绑定。`tabView`是当前的`TabView`视图，`position`为`TabView`的索引。

* `void onTabViewStatusChanged(View tabView, int position, boolean isSelected)`
>当`TabView`初始化或者切换时,监听控件的状态变化进行回调。`tabView`是当前状态发生变化的`TabView`视图，`position`为该`TabView`的索引，`isSelected`标示状态。

### 3.控件的常用方法
`HTViewPagerWithIndicator`是继承`ViewPager`的，因此`ViewPager`的接口方法详细说明参考[ViewPager使用](https://developer.android.com/intl/zh-cn/reference/android/support/v4/view/ViewPager.html)。
##### 其他常用方法

* `setEnableViewPagerSlide(boolean enableSlide)`：是否允许`ViewPager`页面横向滑动，默认允许；
* `setEnableTabViewScroll(boolean enableScroll)`：是否允许`TabView`横向滑动，默认允许；
* `setSelectedIndicatorColors(int... colors)`：设置滑动指示器的颜色，默认是透明色。参数可以是一组颜色，将以循环的形式显示；
* `setDividerColors(int... colors)`：设置纵向分割线的颜色，默认透明色。参数可以是一组颜色，将以循环的形式显示；
* `setCustomTabColorizer(SlidingTabLayout.TabColorizer tabColorizer)`：设置自定义的`TabView`的滑动指示器和纵向分割线的颜色；
* `setIndicatorViewShow(boolean indicatorShow)`：设置是否显示滑动指示器，默认显示；
* `void notifyTabViewsDataSetChanged()`：通知全部的`TabView`重新绑定数据；
* `notifyTabViewDataSetChanged(int position)`：通知指定位置的`TabView`数据重新绑定