package com.pocketdigi.plib.util;

import android.graphics.Color;

/**
 * 处理颜色
 * Created by fhp on 15/2/10.
 */
public class ColorUtils {
    /**
     * 计算从一个颜色渐变到另一个颜色，中间点颜色
     * @param fromColor 当前色
     * @param destColor 目标色
     * @param stepTotal 渐变总步数
     * @param step 现在第几步
     * @return 当前步数的颜色
     */
    public static int calculateGradientColor(int fromColor,int destColor,int stepTotal,int step) {
        int ga=calculateGradient(Color.alpha(fromColor),Color.alpha(destColor),stepTotal,step);
        int gr=calculateGradient(Color.red(fromColor),Color.red(destColor),stepTotal,step);
        int gg=calculateGradient(Color.green(fromColor),Color.green(destColor),stepTotal,step);
        int gb=calculateGradient(Color.blue(fromColor),Color.blue(destColor),stepTotal,step);
        return Color.argb(ga, gr, gg, gb);
    }
    /**
     * Gradient = A + (B-A) / StepTotal * N
     * @param a
     * @param b
     * @param step
     * @return
     */
    private static int calculateGradient(int a,int b,int stepTotal,int step) {
        return a+(b-a)/ stepTotal *step;
    }
}
