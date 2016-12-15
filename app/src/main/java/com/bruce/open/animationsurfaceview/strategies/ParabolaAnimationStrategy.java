package com.bruce.open.animationsurfaceview.strategies;

import android.util.Log;

import com.bruce.open.animationsurfaceview.lib.AnimationSurfaceView;
import com.bruce.open.animationsurfaceview.lib.IAnimationStrategy;

/**
 * @author zhenghao.qi
 * @version 2015年11月10日10:40:03
 */
public class ParabolaAnimationStrategy implements IAnimationStrategy {
    /**
     * 重力加速度值。
     */
    private static final float GRAVITY = 400.78033f;
    /**
     * 与X轴碰撞后，重力势能损失掉的百分比。
     */
    private static final float WASTAGE = 0.3f;
    /**
     * 起始下降高度。
     */
    private int height;
    /**
     * 起始点到终点的X轴位移。
     */
    private int width;
    /**
     * 水平位移速度。
     */
    private double velocity;
    /**
     * X Y坐标。
     */
    private double x, y;
    /**
     * 动画开始时间。
     */
    private long startTime;
    /**
     * 首阶段下载的时间。 单位：毫秒。
     */
    private double t1;
    /**
     * 第二阶段上升与下载的时间。 单位：毫秒。
     */
    private double t2;
    /**
     * 动画正在进行时值为true，反之为false。
     */
    private boolean doing;

    private AnimationSurfaceView animationSurfaceView;

    public ParabolaAnimationStrategy(AnimationSurfaceView animationSurfaceView, int h, int w) {
        this.animationSurfaceView = animationSurfaceView;
        setParams(h, w);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        doing = true;
    }

    /**
     * 设置起始下落的高度及水平位移宽度；以此计算水平初速度、计算小球下落的第一阶段及第二阶段上升耗时。
     */
    private void setParams(int h, int w) {
        height = h;
        width = w;

        t1 = Math.sqrt(2 * height * 1.0d / GRAVITY);
        t2 = Math.sqrt((1 - WASTAGE) * 2 * height * 1.0d / GRAVITY);
        velocity = width * 1.0d / (t1 + 2 * t2);
        Log.d("Bruce1", "t1=" + t1 + " t2=" + t2);
    }

    /**
     * 根据当前时间计算小球的X/Y坐标。
     */
    public void compute() {
        double used = (System.currentTimeMillis() - startTime) * 1.0d / 1000;
        x = velocity * used;
        if (0 <= used && used < t1) {
            y = height - 0.5d * GRAVITY * used * used;
        } else if (t1 <= used && used < (t1 + t2)) {
            double tmp = t1 + t2 - used;
            y = (1 - WASTAGE) * height - 0.5d * GRAVITY * tmp * tmp;
        } else if ((t1 + t2) <= used && used < (t1 + 2 * t2)) {
            double tmp = used - t1 - t2;
            y = (1 - WASTAGE) * height - 0.5d * GRAVITY * tmp * tmp;
        } else {
            Log.d("Bruce1", "used:" + used + " set doing false");
            x = velocity * (t1 + 2 * t2);
            y = 0;
            doing = false;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return getMirrorY(animationSurfaceView.getHeight(), animationSurfaceView.getIcon().getHeight());
    }

    /**
     * 反转Y轴正方向。适应手机的真实坐标系。
     */
    public double getMirrorY(int parentHeight, int bitHeight) {
        int half = parentHeight >> 1;
        double tmp = half + (half - y);
        tmp -= bitHeight;
        return tmp;
    }

    public boolean doing() {
        return doing;
    }

    public void cancel() {
        doing = false;
    }
}