package com.cipherthinkers.analogcountdownview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by avin on 05/06/16.
 */
public class AnalogCountdownView extends View {

    /////////////////////////////////////////
    //Fields
    /////////////////////////////////////////
    private TimerConfig mTimerConfig;
    private Paint mPaint;
    private Timer mTimer;
    private TimerTask mTimerTask;
    public final int DEFAULT_COLOR = Color.BLUE;
    private AnalogTimerUpdateListener mAnalogTimerUpdateListener;

    /////////////////////////////////////////
    //Constructors
    /////////////////////////////////////////
    public AnalogCountdownView(Context context) {
        super(context);
        init(null);
    }

    public AnalogCountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AnalogCountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnalogCountdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /*
    * Initializes the TimerConfig object used for all the calculations and initializing the fields
    * */
    private void init(AttributeSet attributeSet) {
        mTimerConfig = new TimerConfig();

        if(attributeSet == null){
            return;
        }

        TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.AnalogCountdownView);

        for (int i = 0; i < array.length(); i++) {
            int type = array.getIndex(i);

            if (type == R.styleable.AnalogCountdownView_start_angle) {
                int start_angle = array.getInt(R.styleable.AnalogCountdownView_start_angle, 270);
                mTimerConfig.setmStartAngle(start_angle);
            }

            if (type == R.styleable.AnalogCountdownView_total_value) {
                int total_value = array.getInt(R.styleable.AnalogCountdownView_total_value, 100);
                mTimerConfig.setmTotalValue(total_value);
            }

            if (type == R.styleable.AnalogCountdownView_initial_value) {
                int initial_value = array.getInt(R.styleable.AnalogCountdownView_initial_value, 0);
                mTimerConfig.setmInitialValue(initial_value);
            }

            if (type == R.styleable.AnalogCountdownView_increment) {
                boolean isIncreasing = array.getBoolean(R.styleable.AnalogCountdownView_increment, true);
                mTimerConfig.setIncreasing(isIncreasing);
            }

            if (type == R.styleable.AnalogCountdownView_interval_ms) {
                int interval = array.getInt(R.styleable.AnalogCountdownView_interval_ms, 1000);
                mTimerConfig.setmIntervalInMilliseconds(interval);
            }

            if (type == R.styleable.AnalogCountdownView_timer_icon) {
                int drawable_id = array.getResourceId(R.styleable.AnalogCountdownView_timer_icon, -1);
                mTimerConfig.setmDrawableId(drawable_id);
                if(mTimerConfig.getmIconWidthInPx() == 0){
                    mTimerConfig.setmIconWidthInPx(TimerConfig.DEFAULT_ICON_WIDTH_IN_PX);
                }
            }

            if (type == R.styleable.AnalogCountdownView_icon_size) {
                Float drawable_size = array.getDimension(R.styleable.AnalogCountdownView_icon_size, 140);
                mTimerConfig.setmIconWidthInPx(drawable_size.intValue());
            }

            if (type == R.styleable.AnalogCountdownView_progress_width) {
                Float progress_width = array.getDimension(R.styleable.AnalogCountdownView_progress_width, 3);
                mTimerConfig.setmProgressWidthInPx(progress_width.intValue());
            }

            if (type == R.styleable.AnalogCountdownView_start_delay_ms) {
                int start_delay = array.getInt(R.styleable.AnalogCountdownView_start_delay_ms, 0);
                mTimerConfig.setmStartDelay(start_delay);
            }

            if (type == R.styleable.AnalogCountdownView_progress_color) {
                int color = array.getColor(R.styleable.AnalogCountdownView_progress_color, DEFAULT_COLOR);
                mTimerConfig.setmProgressColor(color);
                setPaintColor(color);
            }
        }

        array.recycle();
    }

    private Paint getPaint(){
        if(mPaint == null){
            mPaint = new Paint();
            mPaint.setColor(mTimerConfig.getmProgressColor());
            mPaint.setStrokeWidth(mTimerConfig.getmProgressWidthInPx());
            mPaint.setAntiAlias(true);
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setStyle(Paint.Style.STROKE);
        }

        return mPaint;
    }

    private void setPaintColor(int color){
        if(mPaint != null){
            mPaint.setColor(color);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        if (canvas == null) {
            canvas = new Canvas();
        }
        super.onDraw(canvas);
        canvas.drawArc(mTimerConfig.getmRectF(), mTimerConfig.getmStartAngle(), mTimerConfig.getSweepAngle(),
                false, getPaint());
        Bitmap bitmap = mTimerConfig.getmBitmap();
        if(bitmap != null) {
            canvas.drawBitmap(bitmap, null, mTimerConfig.getmIconRectF(), null);
        }
    }

    /**
     * Resets the current value based on which the timer is running
     * Note : It doesn't stops the timer
     *
     * If you want to stop/start the timer as well you can call #resetAndStop or #resetAndStart respectively
     * */
    public void reset(){
        mTimerConfig.reset();
    }

    /**
     * Resets the current value based on which the timer is running
     * And stops the timer
     * */
    public void resetAndStop(){
        mAnalogTimerUpdateListener = null;
        mTimerConfig.reset();
        stop();
        invalidate();
    }

    /**
     * Resets the current value based on which the timer is running
     * And restarts the timer
     * */
    public void resetAndStart(){
        resetAndStop();
        start();
    }

    /**
     * Resets the current value based on which the timer is running
     * And restarts the timer
     *
     * @param analogTimerUpdateListener - Listener to be associated with the view to get updates from the view
     * */
    public void resetAndStart(AnalogTimerUpdateListener analogTimerUpdateListener){
        resetAndStop();
        start(analogTimerUpdateListener);
    }

    /**
     * Starts the timer
     *
     * @param analogTimerUpdateListener - Listener to be associated with the view to get updates from the view
     * */
    public void start(AnalogTimerUpdateListener analogTimerUpdateListener){
        mAnalogTimerUpdateListener = null;
        stop();
        mAnalogTimerUpdateListener = analogTimerUpdateListener;
        if(mTimer == null){
            mTimer = new Timer();
        }

        if(mTimerTask == null){
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mTimerConfig.moveToNextValue();
                    AnalogCountdownView.this.postInvalidate();
                    if(mTimerConfig.isExpired()){
                        mTimer.cancel();
                        if(mAnalogTimerUpdateListener != null){
                            AnalogCountdownView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAnalogTimerUpdateListener.onTimerExpired();
                                }
                            });
                        }
                        return;
                    }
                    if(mAnalogTimerUpdateListener != null){
                        AnalogCountdownView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                mAnalogTimerUpdateListener.onTimerUpdated(mTimerConfig.getmCurrentValue(),
                                        mTimerConfig.getmTotalValue());
                            }
                        });
                    }
                }
            };
        }
        if(mAnalogTimerUpdateListener != null){
            AnalogCountdownView.this.post(new Runnable() {
                @Override
                public void run() {
                    mAnalogTimerUpdateListener.onTimerStarted();
                }
            });
        }
        mTimer.schedule(mTimerTask, mTimerConfig.getmStartDelay(), mTimerConfig.getmIntervalInMilliseconds());
    }

    /**
     * Starts the timer
     * */
    public void start(){
        start(null);
    }

    /**
     * Stops the timer
     * */
    public void stop(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }

        if(mTimerTask != null){
            mTimerTask = null;
        }
    }

    private class TimerConfig{
        public static final int DEFAULT_ICON_WIDTH_IN_PX = 140;
        private int mStartAngle = 270;
        private int mSweepAngle = 0;
        private int mTotalValue = 100;
        private int mCurrentValue = 0;
        private int mProgressWidthInDp = 3;
        private int mProgressWidthInPx = 0;
        private int mViewWidthInPx = 0;
        private Paint mPaint;
        private RectF mRectF, mIconRectF;
        private int mDrawableId = -1;
        private Bitmap mBitmap;
        private int mIconX, mIconY;
        private int mIconWidthInPx = 0;
        private int mInitialValue = 0;
        private boolean isIncreasing = true;
        private long mIntervalInMilliseconds = 1000;
        private long mStartDelay = 0;
        private int mProgressColor = DEFAULT_COLOR;

        public int getmProgressColor() {
            return mProgressColor;
        }

        public void setmProgressColor(int mProgressColor) {
            this.mProgressColor = mProgressColor;
        }

        public int getmStartAngle() {
            return mStartAngle;
        }

        public void setmStartAngle(int mStartAngle) {
            this.mStartAngle = mStartAngle;
        }

        public int getmSweepAngle() {
            return mSweepAngle;
        }

        public void setmSweepAngle(int mSweepAngle) {
            this.mSweepAngle = mSweepAngle;
        }

        public int getmTotalValue() {
            return mTotalValue;
        }

        public void setmTotalValue(int mTotalValue) {
            this.mTotalValue = mTotalValue;
        }

        public int getmCurrentValue() {
            return mCurrentValue;
        }

        public void setmCurrentValue(int mCurrentValue) {
            this.mCurrentValue = mCurrentValue;
        }

        public int getmProgressWidthInDp() {
            return mProgressWidthInDp;
        }

        public void setmProgressWidthInDp(int mProgressWidthInDp) {
            this.mProgressWidthInDp = mProgressWidthInDp;
        }

        public int getmProgressWidthInPx() {
            if(mProgressWidthInPx == 0){
                mProgressWidthInPx = (int) ((mProgressWidthInDp * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
            }

            return mProgressWidthInPx;
        }

        public void setmProgressWidthInPx(int mProgressWidthInPx) {
            this.mProgressWidthInPx = mProgressWidthInPx;
        }

        public int getmViewWidthInPx() {
            if(mViewWidthInPx == 0){
                mViewWidthInPx = getRight() - getLeft();
            }

            return mViewWidthInPx;
        }

        public void setmViewWidthInPx(int mViewWidthInPx) {
            this.mViewWidthInPx = mViewWidthInPx;
        }

        public Paint getmPaint() {
            return mPaint;
        }

        public void setmPaint(Paint mPaint) {
            this.mPaint = mPaint;
        }

        public RectF getmRectF() {
            if(mRectF == null){
                mRectF = new RectF();

                int curveWidthInPx = getmProgressWidthInPx();

                int widthInPx = getmViewWidthInPx();
                mRectF.set(curveWidthInPx / 2 + mIconWidthInPx/4, // left
                        curveWidthInPx / 2 + mIconWidthInPx/4, // top
                        widthInPx - curveWidthInPx/2 - mIconWidthInPx/4, // right
                        widthInPx - curveWidthInPx/2 - mIconWidthInPx/4); // bottom
            }

            return mRectF;
        }

        public void setmRectF(RectF mRectF) {
            this.mRectF = mRectF;
        }

        public RectF getmIconRectF() {
            if(mIconRectF == null){
                mIconRectF = new RectF();
            }
            calculateX();
            calculateY();
            mIconRectF.set(mIconX - mIconWidthInPx/2, //Left
                    mIconY - mIconWidthInPx/2, //Top
                    mIconX + mIconWidthInPx/2, //Right
                    mIconY + mIconWidthInPx/2); // Bottom
            return mIconRectF;
        }

        public void setmIconRectF(RectF mIconRectF) {
            this.mIconRectF = mIconRectF;
        }

        public int getmDrawableId() {
            return mDrawableId;
        }

        public void setmDrawableId(int mDrawableId) {
            this.mDrawableId = mDrawableId;
        }

        public Bitmap getmBitmap() {
            if(mBitmap == null && mDrawableId != -1) {
                mBitmap = BitmapFactory.decodeResource(getContext().getResources(), mDrawableId);
            }

            return mBitmap;
        }

        public void setmBitmap(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }

        public int getmIconX() {
            return mIconX;
        }

        public void setmIconX(int mIconX) {
            this.mIconX = mIconX;
        }

        public int getmIconY() {
            return mIconY;
        }

        public void setmIconY(int mIconY) {
            this.mIconY = mIconY;
        }

        public int getmIconWidthInPx() {
            return mIconWidthInPx;
        }

        public void setmIconWidthInPx(int mIconWidthInPx) {
            this.mIconWidthInPx = mIconWidthInPx;
        }

        public int getmInitialValue() {
            return mInitialValue;
        }

        public void setmInitialValue(int mInitialValue) {
            this.mInitialValue = mInitialValue;
            this.mCurrentValue = mInitialValue;
        }

        public boolean isIncreasing() {
            return isIncreasing;
        }

        public void setIncreasing(boolean increasing) {
            isIncreasing = increasing;
        }

        public long getmIntervalInMilliseconds() {
            return mIntervalInMilliseconds;
        }

        public void setmIntervalInMilliseconds(long mIntervalInMilliseconds) {
            this.mIntervalInMilliseconds = mIntervalInMilliseconds;
        }

        private int adjustAngleForStartAngle(){
            int adjustAngle = 0;
            if(mStartAngle > 270){
                adjustAngle = mStartAngle - 270;
            }else if(mStartAngle < 270){
                adjustAngle = 90 + mStartAngle;
            }

            return adjustAngle;
        }

        private int calculateX(){
            int sweepAngle = getSweepAngle() + adjustAngleForStartAngle();
            int radius = (mViewWidthInPx/2) - mIconWidthInPx/4;
            mIconX = (int) (mIconWidthInPx/4 + radius * (1 + Math.sin(Math.toRadians(sweepAngle))));

            return mIconX;
        }

        private int calculateY(){
            int sweepAngle = getSweepAngle() + adjustAngleForStartAngle();
            int radius = (mViewWidthInPx/2) - mIconWidthInPx/4;
            mIconY = (int) (mIconWidthInPx/4 + radius * (1 - Math.cos(Math.toRadians(sweepAngle))));

            return mIconY;
        }

        private int getSweepAngle(){
            mSweepAngle = 360*mCurrentValue/mTotalValue;
            return mSweepAngle;
        }

        public void reset(){
            mCurrentValue = mInitialValue;
        }

        private void moveToNextValue(){
            mCurrentValue = mCurrentValue + ((isIncreasing)?1:(-1));
        }

        private boolean isExpired(){
            return (isIncreasing && mCurrentValue == mTotalValue) ||
                    (!isIncreasing && mCurrentValue == 0);
        }

        public long getmStartDelay() {
            return mStartDelay;
        }

        public void setmStartDelay(long mStartDelay) {
            this.mStartDelay = mStartDelay;
        }
    }


    /**
     * A listenr that can be used to get callbacks for the following three scenarios:
     * 1. Timer started
     * 2. Timer updated
     * 3. Timer stopped
     * */
    public interface AnalogTimerUpdateListener{
        void onTimerUpdated(int currentValue, int totalValue);
        void onTimerStarted();
        void onTimerExpired();
    }

}
