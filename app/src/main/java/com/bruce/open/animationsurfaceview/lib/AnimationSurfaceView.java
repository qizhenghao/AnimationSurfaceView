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

    private static final String TAG = "AnimationSurfaceView";
    private static final long REFRESH_INTERVAL_TIME = 15l;//每间隔15ms刷一帧
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;                               //动画图标
    private IAnimationStrategy mIAnimationStrategy;       //动画执行算法策略
    private OnStausChangedListener mStausChangedListener; //动画状态改变监听事件

    private int marginLeft;
    private int marginTop;

    private boolean isSurfaceDestoryed = true;            //默认未创建，相当于Destory
    private Thread mThread;                               //动画刷新线程

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

    //初始化
    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);//设置画布背景透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mThread = new Thread(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceDestoryed = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceDestoryed = true;
        if (mIAnimationStrategy != null)//如果surfaceView创建后，没有执行setStrategy,就被销毁，会空指针异常
            mIAnimationStrategy.cancel();
    }

    //执行
    private void executeAnimationStrategy() {
        Canvas canvas = null;

        Paint tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        if (mStausChangedListener != null) {
            mStausChangedListener.onAnimationStart(this);
        }
        mIAnimationStrategy.start();
        while (mIAnimationStrategy.doing()) {
            try {
                mIAnimationStrategy.compute();

                canvas = mSurfaceHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);// 设置画布的背景为透明

                // 绘上新图区域
                float x = (float) mIAnimationStrategy.getX() + marginLeft;
                float y = (float) mIAnimationStrategy.getY() + marginTop;

                canvas.drawRect(x, y, x + mBitmap.getWidth(), y + mBitmap.getHeight(), tempPaint);
                canvas.drawBitmap(mBitmap, x, y, paint);

                mSurfaceHolder.unlockCanvasAndPost(canvas);
                Thread.sleep(REFRESH_INTERVAL_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // clear屏幕内容
        if (isSurfaceDestoryed == false) {// 如果直接按Home键回到桌面，这时候SurfaceView已经被销毁了，lockCanvas会返回为null。
            canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

        if (mStausChangedListener != null) {
            mStausChangedListener.onAnimationEnd(this);
        }
    }

    /**
     * 开始播放动画
     */
    public void startAnimation() {
        if (mThread.getState() == Thread.State.NEW) {
            mThread.start();
        } else if (mThread.getState() == Thread.State.TERMINATED) {
            mThread = new Thread(this);
            mThread.start();
        }
    }

    /**
     * 是否正在播放动画
     */
    public boolean isShow() {
        return mIAnimationStrategy.doing();
    }

    /**
     * 结束动画
     */
    public void endAnimation() {
        mIAnimationStrategy.cancel();
    }

    /**
     * 设置要播放动画的bitmap
     *
     * @param bitmap
     */
    public void setIcon(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    /**
     * 获取要播放动画的bitmap
     */
    public Bitmap getIcon() {
        return mBitmap;
    }

    /**
     * 设置margin left 像素
     *
     * @param marginLeftPx
     */
    public void setMarginLeft(int marginLeftPx) {
        this.marginLeft = marginLeftPx;
    }

    /**
     * 设置margin left 像素
     *
     * @param marginTopPx
     */
    public void setMarginTop(int marginTopPx) {
        this.marginTop = marginTopPx;
    }

    /**
     * 设置动画状态改变监听器
     */
    public void setOnAnimationStausChangedListener(OnStausChangedListener listener) {
        this.mStausChangedListener = listener;
    }

    @Override
    public void run() {
        executeAnimationStrategy();
    }

    public interface OnStausChangedListener {
        void onAnimationStart(AnimationSurfaceView view);

        void onAnimationEnd(AnimationSurfaceView view);
    }

    /**
     * 设置动画执行算法策略
     *
     * @param strategy
     */
    public void setStrategy(IAnimationStrategy strategy) {
        this.mIAnimationStrategy = strategy;
    }

}