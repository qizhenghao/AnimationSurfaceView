package com.bruce.open.animationsurfaceview.Activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bruce.open.animationsurfaceview.R;
import com.bruce.open.animationsurfaceview.lib.AnimationSurfaceView;
import com.bruce.open.animationsurfaceview.lib.IAnimationStrategy;
import com.bruce.open.animationsurfaceview.strategies.ParabolaAnimationStrategy;
import com.bruce.open.animationsurfaceview.strategies.ScanAnimaitonStrategy;

public class DemoActivity extends Activity implements AnimationSurfaceView.OnStausChangedListener {

    private IAnimationStrategy iAnimationStrategy;
    private AnimationSurfaceView animationSurfaceView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

//        initScanAnimation();
//        initParabolaAnimation();
    }

    private void initParabolaAnimation() {
        animationSurfaceView = (AnimationSurfaceView) findViewById(R.id.animation_surfaceView);
        animationSurfaceView.setOnAnimationStausChangedListener(this);
        // 设置起始Y轴高度和终止X轴位移
        iAnimationStrategy = new ParabolaAnimationStrategy(animationSurfaceView, dp2px(320), dp2px(320));
        animationSurfaceView.setStrategy(iAnimationStrategy);
        animationSurfaceView.setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        animationSurfaceView.startAnimation();
    }

    private void initScanAnimation() {
        animationSurfaceView = (AnimationSurfaceView) findViewById(R.id.animation_surfaceView);
        iAnimationStrategy = new ScanAnimaitonStrategy(animationSurfaceView, dp2px(150), 2000);
        animationSurfaceView.setStrategy(iAnimationStrategy);
        animationSurfaceView.setOnAnimationStausChangedListener(this);
        animationSurfaceView.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan_icon));
        animationSurfaceView.startAnimation();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_demo_cancle_anim_btn:
                if (animationSurfaceView.isShow()) {
                    animationSurfaceView.endAnimation();
                }
                if (animationSurfaceView.isShow()) {
                    animationSurfaceView.endAnimation();
                }
                break;
            case R.id.activity_demo_start_anim_btn1:
                initScanAnimation();
                break;
            case R.id.activity_demo_start_anim_btn2:
               initParabolaAnimation();
                break;
        }
    }

    @Override
    public void onAnimationStart(AnimationSurfaceView view) {

    }

    @Override
    public void onAnimationEnd(AnimationSurfaceView view) {

    }

    private int dp2px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }
}
