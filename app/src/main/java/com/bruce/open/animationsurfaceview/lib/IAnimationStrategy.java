package com.bruce.open.animationsurfaceview.lib;

/**
 * Created by qizhenghao on 15-11-9.
 */
public interface IAnimationStrategy {

    void compute();

    boolean doing();

    void start();

    double getX();

    double getY();

    void cancel();
}
