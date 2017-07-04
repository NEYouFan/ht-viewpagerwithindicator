## HTViewPagerWithIndicator
`HTViewPagerWithIndicator`是一个基于[SlidingTabLayout](http://developer.android.com/intl/zh-cn/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html)和[ViewPager](https://developer.android.com/intl/zh-cn/reference/android/support/v4/view/ViewPager.html)的能够自定义滑动指示器视图的页面管理控件。

![image](https://github.com/NEYouFan/ht-viewpagerwithindicator/raw/master/imgs/Untitled1.gif)

## 特性
* 支持完全自定义`TabView`和滑动指示器视图
* 支持自定义`TabView`位置（头部和尾部）
* 支持`TabView`切换事件回调
* 支持`TabView`视图数据动态更新


## 用法
### 在XML布局文件中使用控件

```
<com.netease.hearttouch.htviewpagerwithindicator.HTViewPagerWithIndicator
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:htHorizontalBorderHeight="1dp"//底部分割线的高度，可选
    app:htHorizontalBorderColor="@color/colorAccent"//底部分割线的颜色，可选
    app:htTabLayoutBackground="#1f002211" //设置TabLayout的背景色，可选
    app:htVerticalDividerHeight="20dp" //tabView垂直分割线的高度，可选
    app:htIndicatorHeight="6dp"//滑动指示器的高度，可选
    app:htTabViewOffset="20dp" //TabView滑动时的偏移量，可选
    app:htTabLayoutGravity="bottom"//TabLayout的位置，默认是顶部，可选
    app:htIndicatorGravity="top" //指示器的位置，默认在TabLayout的底部，可选
    app:htHorizontalDividerGravity="top" //底部分割线的位置，默认在TabLayout的底部，可选
    app:htMaxVisibleViewCount="-1"//界面显示TabView的数量，默认自适应宽度，可选
    app:htTabLayoutPaddingTop="1dp"//设置TabLayout的padding值
   />
```
上面只列出部分的自定义属性，更多内容的详细说明请参考[使用文档](https://github.com/NEYouFan/ht-viewpagerwithindicator/blob/master/Guide.md)。
### 实现HTSlidingTabLayoutAdapter接口
若用户在创建`PagerAdapter`时，没有实现`HTSlidingTabLayoutAdapter`接口，则使用默认的`TabView`视图(仅文本显示)；否则，显示用户自定义的`TabView`视图样式，具体实现可以参考[使用文档](https://github.com/NEYouFan/ht-viewpagerwithindicator/blob/master/Guide.md)或者[demo](https://github.com/NEYouFan/ht-viewpagerwithindicator/tree/master/demo)。
## 集成
### Gradle

```
compile 'com.netease.hearttouch:ht-viewpagerwithindicator:0.0.1'
```

### Maven

``` 
<dependency>
    <groupId>com.netease.hearttouch</groupId>
    <artifactId>ht-viewpagerwithindicator</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 混淆
如果要使用混淆，在引用工程的 `proguard` 文件中，添加如下代码：

```
-keep class com.netease.hearttouch.htviewpagerwithindicator.** { *; }
-dontwarn com.netease.hearttouch.htviewpagerwithindicator.**
```

## 许可证
`HTRefreshRecyclerView` 使用 `MIT` 许可证，详情见 [LICENSE](https://github.com/NEYouFan/ht-viewpagerwithindicator/blob/master/LICENSE.txt) 文件。
