package com.bruce.open.animationsurfaceview.lib;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author zhenghao.qi
 * @version 1.0
 * @time 2015年11月09日15:24:15
 */
public class AnimationSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * 每30ms刷一帧。
     */
    private static final long SLEEP_DURATION = 10l;
    private SurfaceHolder holder;
    /**
     * 动画图标。
     */
    private Bitmap bitmap;
    private IAnimationStrategy iAnimationStrategy;
    private OnAnimationStausChangedListener listener;

    private int marginLeft;
    private int marginTop;

    /**
     * 默认未创建，相当于Destory。
     */
    private boolean surfaceDestoryed = true;
    private Thread thread;

    public AnimationSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AnimationSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimationSurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
        thread = new Thread(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDestoryed = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDestoryed = true;
        iAnimationStrategy.cancel();
    }

    private void handleThread() {
        Canvas canvas = null;

        Paint pTmp = new Paint();
        pTmp.setAntiAlias(true);
        pTmp.setColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        // 设置抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        if (listener != null) {
            listener.onAnimationStart(this);
        }
        iAnimationStrategy.start();
        while (iAnimationStrategy.doing()) {
            try {
                iAnimationStrategy.compute();
                canvas = holder.lockCanvas();
                // 设置画布的背景为透明。
                canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
                // 绘上新图区域
                float x = (float) iAnimationStrategy.getX() + marginLeft;
                float y = (float) iAnimationStrategy.getY() + marginTop;
                canvas.drawRect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight(), pTmp);
                canvas.drawBitmap(bitmap, x, y, paint);
                holder.unlockCanvasAndPost(canvas);
                Thread.sleep(SLEEP_DURATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 清除屏幕内容
        // 直接按"Home"回桌面，SurfaceView被销毁了，lockCanvas返回为null。
        if (surfaceDestoryed == false) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
            holder.unlockCanvasAndPost(canvas);
        }

        if (listener != null) {
            listener.onAnimationEnd(this);
        }
    }

    /**
     * 开始播放动画
     */
    public void startAnimation() {
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        } else if (thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * 是否正在播放动画
     */
    public boolean isShow() {
        return iAnimationStrategy.doing();
    }

    /**
     * 结束动画
     */
    public void endAnimation() {
        iAnimationStrategy.cancel();
    }

    /**
     * 设置要播放动画的bitmap
     *
     * @param bitmap
     */
    public void setIcon(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    /**
     * 获取要播放动画的bitmap
     *
     */
    public Bitmap getIcon() {
        return bitmap;
    }

    /**
     * 设置margin left 像素
     * @param marginLeftPx
     */
    public void setMarginLeft(int marginLeftPx) {
        this.marginLeft = marginLeftPx;
    }

    /**
     * 设置margin left 像素
     * @param marginTopPx
     */
    public void setMarginTop(int marginTopPx) {
        this.marginTop = marginTopPx;
    }

    /**
     * 设置动画状态改变监听器
     */
    public void setOnAnimationStausChangedListener(OnAnimationStausChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        handleThread();
    }

    public interface OnAnimationStausChangedListener {
        public void onAnimationStart(AnimationSurfaceView view);

        public void onAnimationEnd(AnimationSurfaceView view);
    }

    /**
     * 设置动画执行算法策略
     *
     * @param strategy
     */
    public void setStrategy(IAnimationStrategy strategy) {
        this.iAnimationStrategy = strategy;
    }

}