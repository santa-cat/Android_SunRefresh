package com.example.santa.sunrefresh;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

/**
 * Created by santa on 16/6/19.
 */
public class SeaLayout extends RelativeLayout implements PullHeader {
    private Resources mResource;
    private float mScreenDensity;
    private SeaView mSeaView;
    private SunView mSunView;
    private boolean mRefreshStartFlag = false;
    private boolean mRefreshingFlag = false;
    private AnimatorSet mSunMoveAnimotion;

    //以下可以通过xml设置
    private int mDefaultSizeOfSun = 40;
    private int mDefaultColorOfSun;


    public SeaLayout(Context context) {
        super(context);
        init(context);
    }

    public SeaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SeaLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int getDpSize(int px) {
        return (int)(mScreenDensity * px);
    }

    public void init(Context context){
        mResource = this.getResources();
        final DisplayMetrics metrics = mResource.getDisplayMetrics();
        mScreenDensity = metrics.density;
        RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(getDpSize(mDefaultSizeOfSun), getDpSize(mDefaultSizeOfSun));
        l.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        l.addRule(RelativeLayout.CENTER_VERTICAL);

        mSunView = new SunView(context);
        mSunView.setLayoutParams(l);
        addView(mSunView);
        mSeaView = new SeaView(context);
        addView(mSeaView);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mResource.getColor(R.color.headerSkyColor));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(null != mSunView) {
            int right = getRight() - getPaddingRight();
            int left = right - mSunView.getMeasuredWidth();
            int top = mSeaView.getSeaOffcet();
            int buttom = top + mSunView.getMeasuredHeight();

            mSunView.layout(left, top, right, buttom);
        }
    }

    public void sunMoveAnimotionOnRefresh(){
        if(isAnimotionRunning()){
            return;
        }
        AnimatorSet UpAnim = new AnimatorSet();
        //后面的数字都是相对偏移量
        ObjectAnimator transX = ObjectAnimator.ofFloat(mSunView, "translationX", 0, -getWidth()/3);
        ObjectAnimator transY = ObjectAnimator.ofFloat(mSunView, "translationY", 0, -getHeight()/3);
        transY.setInterpolator(PathInterpolatorCompat.create(0.7f, 1f));

        UpAnim.playTogether(
            transX, transY,
            ObjectAnimator.ofFloat(mSunView, "scaleX", 1, 1.2f),
            ObjectAnimator.ofFloat(mSunView, "scaleY", 1, 1.2f)
        );
        UpAnim.setDuration(800);
        UpAnim.addListener(new DefaultAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                mRefreshingFlag = true;
            }
        });
        mSunMoveAnimotion = UpAnim;
        mSunMoveAnimotion.start();
        onUIRefreshing();

    }

    public boolean isAnimotionRunning() {
        return (null != mSunMoveAnimotion && mSunMoveAnimotion.isRunning());
    }

    public boolean isRefreshing() {
        return mRefreshingFlag;
    }

    //准备刷新
    public void onUIReadyToRefresh() {
        if(null != mSunView) {
            mSunView.startAnimotion();
        }
    }

    //放弃刷新
    public void onUICancleRefresh(){
        if(null != mSunView) {
            mSunView.stopAnimotion();
        }

        if (null != mSeaView) {
            mSeaView.stopAnimotion();
        }
    }

    //放手后开始刷新UI动画
    public void onUIRefreshing(){
        if(null != mSunView) {
            mSunView.startAnimotion();
        }

        if (null != mSeaView) {
            mSeaView.startAnimotion();
        }
    }

    //刷新完成UI动画
    public void onUIRefreshed(){
        if(null != mSunView) {
            mSunView.stopAnimotion();
        }

        if (null != mSeaView) {
            mSeaView.stopAnimotion();
        }

    }



    @Override
    public void onRefreshComplete() {
        if(isAnimotionRunning()){
            return;
        }
        if(!isRefreshing()) {
            return;
        } else {
            mRefreshingFlag = false;
        }
        onUIRefreshed();
        AnimatorSet UpAnim = new AnimatorSet();
        //后面的数字都是相对偏移量
        //ObjectAnimator transX = ObjectAnimator.ofFloat(mSunView, "translationX", -getWidth()/3, -getWidth()*2/3);
        ObjectAnimator transY = ObjectAnimator.ofFloat(mSunView, "translationY", -getHeight()/3, -getHeight()*2/3);
        //transY.setInterpolator(PathInterpolatorCompat.create(0.7f, 1f));
        UpAnim.playTogether(
                transY,
                ObjectAnimator.ofFloat(mSunView, "scaleX", 1.2f, 1f),
                ObjectAnimator.ofFloat(mSunView, "scaleY", 1.2f, 1f)
        );
        UpAnim.setDuration(500);


        AnimatorSet rebackAnim = new AnimatorSet();
        ObjectAnimator transBackX = ObjectAnimator.ofFloat(mSunView, "translationX", -getWidth()/3, 0);
        ObjectAnimator transBackY = ObjectAnimator.ofFloat(mSunView, "translationY", -getHeight()*2/3, 0);
        rebackAnim.playTogether(transBackX, transBackY );
        rebackAnim.setDuration(0);

        AnimatorSet animTogether = new AnimatorSet();
        animTogether.playSequentially(UpAnim, rebackAnim);
        mSunMoveAnimotion = animTogether;
        mSunMoveAnimotion.start();
    }

    public void onPullProgress(float percent, int status) {
        //如果正在刷新,不停止animotion
        if (!isRefreshing() && !isAnimotionRunning()) {
            if (status != PullRefreshLayout.STATE_RELEASE) {
//                mRefreshFinshFlag = false;
                mRefreshStartFlag = false;
                if (percent >= 0.9f) {
                    onUIReadyToRefresh();
                } else {
                    onUICancleRefresh();
                }
            } else {
                if (percent >= 0.9f) {
                    onUIReadyToRefresh();
                    mRefreshStartFlag = true;
                }
                if (percent == 0 && mRefreshStartFlag) {
                    sunMoveAnimotionOnRefresh();
                    mRefreshStartFlag = false;
                }
            }
        }
        if (null != mSeaView) {
            mSeaView.onPullProgress(percent);
        }
        return;
    }

    @Override
    public boolean isMoveWithContent() {
        return false;
    }

    @Override
    public boolean isCanRefresh() {
        return mRefreshStartFlag;
    }

    @Override
    public void onPullProgress(float percentUp, float percentDown, int status) {
        onPullProgress(percentDown, status);
    }
}
