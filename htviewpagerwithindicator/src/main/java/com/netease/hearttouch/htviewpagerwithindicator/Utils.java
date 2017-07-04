package com.netease.hearttouch.htviewpagerwithindicator;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 一个简单的工具类
 */
public class Utils {
    public static int convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (px / metrics.density + 0.5f);
    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }
}
