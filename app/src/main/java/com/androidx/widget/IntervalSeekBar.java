package com.androidx.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 间距拖动
 */
public class IntervalSeekBar extends View {

    //圆圈颜色
    private int barColor = Color.WHITE;
    //圆圈半径
    private float barRadius = 20;
    //线条宽度
    private float lineWidth = 8;
    //左边进度
    private int leftProgress = 20;
    //右边进度
    private int rightProgress = 80;
    //水平间距
    private float marginHorizontal = 40;
    //垂直间距
    private float marginVertical = 40;
    //背景颜色
    private int seekBackgroundColor = Color.parseColor("#929292");
    //进度颜色
    private int seekProgressColor = Color.parseColor("#003AFD");


    //左边坐标
    private float[] leftCoordinate;
    //右边坐标
    private float[] rightCoordinate;
    //中心位置
    private int centerX, centerY;
    //宽度、高度
    private int width, height;
    private Paint paint;

    public IntervalSeekBar(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public IntervalSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public IntervalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.IntervalSeekBar);
            barColor = array.getColor(R.styleable.IntervalSeekBar_barColor,barColor);
            barRadius = array.getDimension(R.styleable.IntervalSeekBar_barRadius,barRadius);
            lineWidth = array.getDimension(R.styleable.IntervalSeekBar_lineWidth,lineWidth);
            leftProgress = array.getInt(R.styleable.IntervalSeekBar_leftProgress,leftProgress);
            rightProgress = array.getInt(R.styleable.IntervalSeekBar_rightProgress,rightProgress);
            marginHorizontal = array.getDimension(R.styleable.IntervalSeekBar_marginHorizontal,marginHorizontal);
            marginVertical = array.getDimension(R.styleable.IntervalSeekBar_marginVertical,marginVertical);
            seekBackgroundColor = array.getColor(R.styleable.IntervalSeekBar_seekBackgroundColor,seekBackgroundColor);
            seekProgressColor = array.getColor(R.styleable.IntervalSeekBar_seekProgressColor,seekProgressColor);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        int requiredWidth = width, requiredHeight = (int) (barRadius * 2 + marginVertical * 2);
        int measureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = measureSpecWidth;
        int measureHeight = measureSpecHeight;
        if ((widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) && heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = requiredHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = measureSpecHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = measureSpecWidth;
            measureHeight = requiredHeight;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawProgress(canvas, true);
        drawProgress(canvas, false);
        drawCircle(canvas, true);
        drawCircle(canvas, false);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(seekBackgroundColor);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float lineWidth = width - 2F * marginHorizontal;
        float lineStartX = marginHorizontal;
        float lineStopX = marginHorizontal + lineWidth;
        paint.setColor(seekBackgroundColor);
        canvas.drawLine(lineStartX, centerY, lineStopX, centerY, paint);
    }

    /**
     * 绘制进度
     *
     * @param canvas
     * @param left
     */
    private void drawProgress(Canvas canvas, boolean left) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(seekProgressColor);
        paint.setStrokeWidth(lineWidth);
        float lineWidth = width - 2 * marginHorizontal;
        float lineLeftStartX = marginHorizontal;
        float lineLeftStopX = marginHorizontal + lineWidth * leftProgress / 100;
        if (left) {
            paint.setColor(seekBackgroundColor);
            canvas.drawLine(lineLeftStartX, centerY, lineLeftStopX, centerY, paint);
        }
        float lineRightStartX = marginHorizontal + lineWidth * leftProgress / 100;
        float lineSRightStopX = marginHorizontal + lineWidth * rightProgress / 100;
        if (!left) {
            paint.setColor(seekProgressColor);
            canvas.drawLine(lineRightStartX, centerY, lineSRightStopX, centerY, paint);
        }
    }

    /**
     * 绘制圆圈
     *
     * @param canvas
     * @param left
     */
    private void drawCircle(Canvas canvas, boolean left) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(barColor);
        paint.setStrokeWidth(lineWidth);
        float lineWidth = width - 2 * marginHorizontal;
        float lineLeftStopX = marginHorizontal + lineWidth * leftProgress / 100;
        if (left) {
            Shader shader = new RadialGradient(lineLeftStopX, centerY, barRadius, new int[]{Color.WHITE, Color.GRAY}, new float[]{0.9f, 1.0f}, Shader.TileMode.MIRROR);
            paint.setShader(shader);
            canvas.drawCircle(lineLeftStopX, centerY, barRadius, paint);
            leftCoordinate = new float[]{lineLeftStopX, centerY};
        }
        float lineRightStopX = marginHorizontal + lineWidth * rightProgress / 100;
        if (!left) {
            Shader shader = new RadialGradient(lineRightStopX, centerY, barRadius, new int[]{Color.WHITE, Color.GRAY}, new float[]{0.9f, 1.0f}, Shader.TileMode.MIRROR);
            paint.setShader(shader);
            canvas.drawCircle(lineRightStopX, centerY, barRadius, paint);
            rightCoordinate = new float[]{lineRightStopX, centerY};
        }
    }

    /**
     * 是否左边
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isLeft(float x, float y) {
        if (x <= leftCoordinate[0] + barRadius
                && y >= leftCoordinate[1] - barRadius * 2
                && y <= leftCoordinate[1] + barRadius * 2) {
            return true;
        }
        return false;
    }

    /**
     * 是否右边
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isRight(float x, float y) {
        if (x >= rightCoordinate[0] - barRadius * 2
                && y >= rightCoordinate[1] - barRadius * 2
                && y <= rightCoordinate[1] + barRadius * 2) {
            return true;
        }
        return false;
    }

    private boolean isLeft;
    private boolean isRight;
    private float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isLeft = isLeft(downX, downY);
                isRight = isRight(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLeft) {
                    float leftMoveX = event.getX() - leftCoordinate[0];
                    if (leftMoveX != 0) {
                        leftProgress = (int) ((leftCoordinate[0] + leftMoveX) * 100F / width);
                    }
                    if (leftProgress > 100) {
                        leftProgress = 100;
                    }
                    if (leftProgress < 0) {
                        leftProgress = 0;
                    }
                }
                if (isRight) {
                    int rightMoveX = (int) (event.getX() - rightCoordinate[0]);
                    if (rightMoveX != 0) {
                        rightProgress = (int) ((rightCoordinate[0] + rightMoveX) * 100 / width);
                    }
                    if (rightProgress > 100) {
                        rightProgress = 100;
                    }
                    if (rightProgress < 0) {
                        rightProgress = 0;
                    }
                }
                int spaceProgress = (int) (barRadius * 3 * 100f / (width - 2f * marginHorizontal));
                if (leftProgress < rightProgress - spaceProgress) {
                    invalidate();
                    if (onSeekBarChangeListener != null) {
                        onSeekBarChangeListener.onProgressChanged(this, leftProgress, rightProgress);
                    }
                }
                break;
        }
        return true;
    }

    private OnSeekBarChangeListener onSeekBarChangeListener;

    /**
     * 设置监听
     *
     * @param onSeekBarChangeListener
     */
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(IntervalSeekBar seekBar, int leftProgress, int rightProgress);
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        invalidate();
    }

    public float getBarRadius() {
        return barRadius;
    }

    public void setBarRadius(float barRadius) {
        this.barRadius = barRadius;
        invalidate();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public int getLeftProgress() {
        return leftProgress;
    }

    public void setLeftProgress(int leftProgress) {
        this.leftProgress = leftProgress;
        invalidate();
    }

    public int getRightProgress() {
        return rightProgress;
    }

    public void setRightProgress(int rightProgress) {
        this.rightProgress = rightProgress;
        invalidate();
    }

    public float getMarginHorizontal() {
        return marginHorizontal;
    }

    public void setMarginHorizontal(float marginHorizontal) {
        this.marginHorizontal = marginHorizontal;
        invalidate();
    }

    public float getMarginVertical() {
        return marginVertical;
    }

    public void setMarginVertical(float marginVertical) {
        this.marginVertical = marginVertical;
        invalidate();
    }

    public int getSeekBackgroundColor() {
        return seekBackgroundColor;
    }

    public void setSeekBackgroundColor(int seekBackgroundColor) {
        this.seekBackgroundColor = seekBackgroundColor;
        invalidate();
    }

    public int getSeekProgressColor() {
        return seekProgressColor;
    }

    public void setSeekProgressColor(int seekProgressColor) {
        this.seekProgressColor = seekProgressColor;
        invalidate();
    }
}

