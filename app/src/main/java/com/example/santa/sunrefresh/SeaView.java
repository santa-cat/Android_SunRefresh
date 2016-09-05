package com.example.santa.sunrefresh;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by santa on 16/6/14.
 */
public class SeaView extends View {
    private Resources mResource;
    private Paint mSeaPaint = new Paint();

    private float mScaleX;
    private float mScaleY;
    private int mWidth;
    private int mHeight;
    private static final int WIDTH = 240;
    private static final int HEIGHT = 180;

    private Path mSeaPath = new Path();
    private int mSeaPointY;
    private int mSeaWidth;
    private int mSpaceBetweenSea;
    private int mSpaceOffcet;
    private static final float SEA_SPINDRIFT_NUM = 10.0f;

    private float flowOffcet = 0;
    private float mPercent = 0.0f;
    private ValueAnimator mAnimator;
    private final static int ANIMOTION_DURATION_TIME = 2000;

    private FlowRunnable flowRunnable;
    private WhalePath mWhalePath = new WhalePath();

    public SeaView(Context context) {
        super(context);
        init();
    }

    public SeaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SeaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        mResource = this.getResources();
        mSeaPaint.setAntiAlias(true);
        mSeaPaint.setStyle(Paint.Style.FILL);

        this.flowRunnable = new FlowRunnable();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mScaleX = mWidth / WIDTH;
        mScaleY = mHeight / HEIGHT;

        calculateSeaPath(mPercent);

        //setupAnimotion();
    }

    public int getSeaOffcet() {
        return  mSeaPointY + mSeaWidth / 4;
    }

    public void calculateSeaPath(float factor) {
        mSeaPointY = mHeight*2/5;
        //如果要流动,则需要将这个扩大
        mSeaWidth = (int)(mWidth/SEA_SPINDRIFT_NUM);
        mSpaceBetweenSea = mSeaWidth/2;
        mSpaceOffcet = mSpaceBetweenSea/2;

        mSeaPath.reset();
        mSeaPath.moveTo(0, mSeaPointY);

        int seaNum = (int)(SEA_SPINDRIFT_NUM + 2);
        for(int i = 0 ; i < seaNum; i++) {
            mSeaPath.quadTo(mSeaWidth / 2 + i * mSeaWidth, mSeaPointY + mSeaWidth / 2, mSeaWidth + i * mSeaWidth, mSeaPointY);
        }
        mSeaPath.lineTo(mSeaWidth*seaNum, mHeight);
        mSeaPath.lineTo(0, mHeight);
        mSeaPath.lineTo(0, mSeaPointY);
        mSeaPath.close();

//        factor = Math.min(1.0f, Math.abs(factor));
//        mSeaPath.offset(0, -mSeaWidth * factor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //这里画背景色,太阳不能显示,要在外层绘制
        //canvas.drawColor(0xFF7CCDCA);
        final int saveCount = canvas.save();
        drawSea(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void drawOneSea(Canvas canvas, int color, float offcetX, float offcetY){
        final int saveCount = canvas.save();
        canvas.translate(offcetX, offcetY);
        mSeaPaint.setColor(color);
//        mSeaPaint.setStrokeWidth(10);
        canvas.drawPath(mSeaPath, mSeaPaint);
        canvas.restoreToCount(saveCount);
    }

    public void drawOneWhale(Canvas canvas, int color, float offcetX, float offcetY){
        final int saveCount = canvas.save();
        canvas.translate(offcetX, offcetY);
        mWhalePath.draw(canvas, color);
        canvas.restoreToCount(saveCount);
    }

    public void drawSea(Canvas canvas) {


        drawOneSea(canvas, mResource.getColor(R.color.headerSeaColorTop), -flowOffcet, 0 + mSpaceBetweenSea * mPercent);
        mWhalePath.calcuPath(mWidth/3, mHeight/3);
        drawOneWhale(canvas, mResource.getColor(R.color.headerWhaleColor), mSeaWidth*3, (int)(mSpaceBetweenSea * (1.4f)) + mSeaPointY + mSpaceBetweenSea*mPercent*2f);
        drawOneSea(canvas, mResource.getColor(R.color.headerSeaColorBetwm),-mSpaceOffcet -flowOffcet, mSpaceBetweenSea*(mPercent*2f + 1));
        mWhalePath.calcuPath(mWidth/2, mHeight/2);
        drawOneWhale(canvas, mResource.getColor(R.color.headerWhaleColor), mSeaWidth/2, (int)(2*mSpaceBetweenSea * 1.15f) + mSeaPointY + mSpaceBetweenSea*mPercent*3);
        drawOneSea(canvas, mResource.getColor(R.color.headerSeaColorButtom), -2*mSpaceOffcet-flowOffcet, mSpaceBetweenSea*(mPercent*3 + 2));



//        canvas.translate(-flowOffcet, 0);
//        mSeaPaint.setColor(Color.BLUE);
////        mSeaPaint.setStrokeWidth(10);
//        canvas.drawPath(mSeaPath, mSeaPaint);
//
//        canvas.translate(0, mSpaceBetweenSea);
//        mSeaPaint.setColor(Color.GREEN);
////        mSeaPaint.setStrokeWidth(10);
//        canvas.drawPath(mSeaPath, mSeaPaint);
//
//        canvas.translate(0, mSpaceBetweenSea);
//        mSeaPaint.setColor(Color.CYAN);
////        mSeaPaint.setStrokeWidth(10);
//        canvas.drawPath(mSeaPath, mSeaPaint);
    }

    public void onPullProgress(float percent) {
        mPercent = percent;
        requestLayout();
    }

//    public void startAnimotion() {
//        if(!mAnimator.isStarted()) {
//            mAnimator.start();
//        }
//    }
//
//    public void stopAnimotion() {
//        if(mAnimator.isStarted()) {
//            mAnimator.cancel();
//        }
//    }
//    public void setupAnimotion() {
//        mAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, -mSeaWidth);
//        mAnimator.setDuration(ANIMOTION_DURATION_TIME);
//        mAnimator.setInterpolator(new LinearInterpolator());
//        //mAnimator.setEvaluator(new ArgbEvaluator());
//        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        mAnimator.setRepeatMode(ValueAnimator.RESTART);
//    }

    public void  startAnimotion() {
        flowRunnable.startAnimotion();
    }

    public void stopAnimotion(){
        flowRunnable.abortAnimation();
    }

    //不弄用animotion控制,animotion会移动整个view,包括鲸鱼,而且边上会没有掉.
    class FlowRunnable implements Runnable {

        boolean mIsFinished = true;

        public void abortAnimation() {
            this.mIsFinished = true;
        }

        public boolean isFinished() {
            return this.mIsFinished;
        }
        @Override
        public void run() {
            if(!isFinished()) {
                flowOffcet = flowOffcet < mSeaWidth ? flowOffcet + 1 : 0;
                postInvalidate();
                SeaView.this.post(this);
            }

        }

        public void startAnimotion() {
            if (!isFinished()){
                return;
            }
            this.mIsFinished = false;
            SeaView.this.post(this);
        }


    }


    public class WhalePath{
        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;
        private PointF mControl1 = new PointF(-15, -60);
        private PointF mControl2 = new PointF(80, -50);
        private PointF mControl3 = new PointF();
        private PointF mControl4 = new PointF();
        private PointF mStart = new PointF(2, 5);
        private PointF mEnd = new PointF(0, 0);
        private Path mPath = new Path();

        private Paint mPaint = new Paint();
        private Paint mEyePaint = new Paint();
        private float mScale = 5.0f;
        private Matrix mTransMatrix = new Matrix();

        public WhalePath(){

            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);

            mEyePaint.setAntiAlias(true);
            mEyePaint.setStyle(Paint.Style.FILL);
            mEyePaint.setColor(Color.BLACK);
            mEyePaint.setStrokeCap(Paint.Cap.ROUND);
            mEyePaint.setStrokeJoin(Paint.Join.ROUND);
        }

        public void calcuPath(float width, float height){
            mScale = Math.min(width/(WIDTH*2), height/HEIGHT);
            mTransMatrix.reset();
            mTransMatrix.setScale(mScale, mScale);
            mEyePaint.setStrokeWidth(2*mScale);

            mPath.reset();
            mEnd.set(75,-3);
            mPath.moveTo(mStart.x, mStart.y);
            mPath.lineTo(0 ,0);
            mPath.cubicTo(mControl1.x, mControl1.y, mControl2.x,mControl2.y, mEnd.x, mEnd.y);
            mPath.cubicTo(75, 2, 88, 2, 85, -13);
            mPath.quadTo(75, -20, 75, -25);
            mPath.quadTo(88 , -13, 100, -25);
            mPath.quadTo(102, -20, 90, -13);
            mPath.quadTo(90, 5, 85, 5);
            mPath.close();
            mPath.transform(mTransMatrix);

        }

        public void draw(Canvas canvas, int color) {
            mPaint.setColor(color);
            canvas.drawPath(mPath, mPaint);
            canvas.drawLine(8*mScale ,-5*mScale , 8*mScale, -8*mScale, mEyePaint);
        }

    }

}
