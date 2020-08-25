package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create By morningsun  on 2020-08-24
 */
public class ChartView extends View implements View.OnTouchListener, GestureDetector.OnGestureListener {
    //x轴
    private Paint xPaint;

    //x轴长度
    private float xWidth;

    //x轴宽度
    private float xHeight;

    //x轴文本画笔
    private Paint xLabelPaint;

    //x轴文本宽度
    private float xLabelWidth;

    //x轴文本高度
    private float xLabelHeight;

    //y轴
    private Paint yPaint;

    //y轴长度
    private float yWidth;

    //y轴宽度
    private float yHeight;

    //y轴文本画笔
    private Paint yLabelPaint;

    //y轴文本宽度
    private float yLabelWidth;

    //y轴文本高度
    private float yLabelHeight;

    //折线图起点 横坐标
    private float startPointX;

    //折线图起点 纵坐标
    private float startPointY;

    //折线
    private Paint linePaint;


    private List<Float> xList = new ArrayList<>();
    private List<Float> newXList = new ArrayList<>();
    private List<Float> yList = new ArrayList<>();
    private List<Float> newYList = new ArrayList<>();

    //背景颜色
    private int backColor;

    //线颜色
    private int lineColor;

    //轴线颜色
    private int xLineColor, yLineColor;

    //轴线显示标签数量
    private int xNum, yNum;

    //轴线粗细
    private float xStrokeWidth, yStrokeWidth;

    //标签颜色
    private int xLabelColor, yLabelColor;

    //标签字体大小
    private float xLabelTextSize, yLabelTextSize;

    //第一个画的点的下标
    private int startPoint = 0;

    //是否可以滑动
    private boolean canScroll;

    //是否显示显示气泡
    private boolean showPop = false;

    private int tapIndex;


    private GestureDetector detector;
    private int offset = 0;

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChartView);
        backColor = ta.getColor(R.styleable.ChartView_backColor, Color.WHITE);
        lineColor = ta.getColor(R.styleable.ChartView_lineColor, Color.BLACK);
        xNum = ta.getInt(R.styleable.ChartView_xNum, 0);
        xLineColor = ta.getColor(R.styleable.ChartView_xLineColor, Color.BLACK);
        xLabelColor = ta.getColor(R.styleable.ChartView_xLabelColor, Color.BLACK);
        xStrokeWidth = ta.getFloat(R.styleable.ChartView_xStrokeWidth, 1);
        xLabelTextSize = ta.getFloat(R.styleable.ChartView_xLabelTextSize, 13);
        yNum = ta.getInt(R.styleable.ChartView_yNum, 0);
        yLineColor = ta.getColor(R.styleable.ChartView_yLineColor, Color.BLACK);
        yLabelColor = ta.getColor(R.styleable.ChartView_yLabelColor, Color.BLACK);
        yStrokeWidth = ta.getFloat(R.styleable.ChartView_yStrokeWidth, 1);
        yLabelTextSize = ta.getFloat(R.styleable.ChartView_yLabelTextSize, 13);
        canScroll = ta.getBoolean(R.styleable.ChartView_canScroll, false);
        ta.recycle();
        if (canScroll) {
            super.setOnTouchListener(this);
            super.setFocusable(true);
            super.setClickable(true);
            super.setLongClickable(true);
            detector = new GestureDetector(getContext(), this);
            detector.setIsLongpressEnabled(false);
        }

        initPain();
    }

    private void initPain() {
        xPaint = new Paint();
        xPaint.reset();
        xPaint.setAntiAlias(true);
        xPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        xPaint.setColor(xLineColor);
        xPaint.setStrokeWidth(xStrokeWidth);
        xPaint.setTextAlign(Paint.Align.CENTER);


        yPaint = new Paint();
        yPaint.reset();
        yPaint.setAntiAlias(true);
        yPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        yPaint.setColor(yLineColor);
        yPaint.setStrokeWidth(yStrokeWidth);
        yPaint.setTextAlign(Paint.Align.CENTER);


        xLabelPaint = new Paint();
        xLabelPaint.reset();
        xLabelPaint.setAntiAlias(true);
        xLabelPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        xLabelPaint.setColor(xLabelColor);
        xLabelPaint.setTextSize(xLabelTextSize);
        xLabelPaint.setStrokeWidth(xStrokeWidth);


        yLabelPaint = new Paint();
        yLabelPaint.reset();
        yLabelPaint.setAntiAlias(true);
        yLabelPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        yLabelPaint.setColor(yLabelColor);
        yLabelPaint.setTextSize(yLabelTextSize);
        yLabelPaint.setStrokeWidth(yStrokeWidth);


        linePaint = new Paint();
        linePaint.reset();
        linePaint.setAntiAlias(true);
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
    }


    public void setData(List<Float> xValue, List<Float> yValue) {
        xList.clear();
        yList.clear();
        newXList.clear();
        newYList.clear();

        xList.addAll(xValue);
        yList.addAll(yValue);

        //排除重复的纵坐标的值
        newYList = ifRepeat(yList);
        //y轴添加0点
        if (!newYList.contains(0f)) {
            newYList.add(0, 0f);
        }
        //y轴排序
        Collections.sort(newYList);

        startPoint = 0;
        showPop=false;
        requestLayout();
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        yLabelWidth = width / 15f;
        yHeight = height - xLabelHeight * 2 - xPaint.getStrokeWidth() * 2;
        yWidth = yPaint.getStrokeWidth();
        if (yNum != 0) {
            yLabelHeight = yHeight / yNum;
        } else {
            yLabelHeight = yHeight / newYList.size();
        }

        xLabelHeight = height / 10f;
        xHeight = xPaint.getStrokeWidth();
        xWidth = width - yPaint.getStrokeWidth() * 2 - yLabelWidth * 2;

        if (xNum != 0) {
            xLabelWidth = xWidth / xNum;
            if (canScroll) {
                newXList.addAll(xList);
            } else {
                if (!xList.isEmpty()) {
                    float a = 0 + xList.get(xList.size() - 1) / (xNum - 1);
                    for (int i = 0; i < xNum; i++) {
                        newXList.add(a * i);
                    }
                }
            }

        } else {
            xLabelWidth = xWidth / xList.size();
            newXList.addAll(xList);
        }
        startPointX = yLabelWidth + yWidth;
        startPointY = xLabelHeight + yHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景颜色
        setBackgroundColor(backColor);
        //绘制Y轴
        drawY(canvas);
        //绘制X轴
        drawX(canvas);
        //绘制线
        drawLine(canvas);
        //绘制圆点
        drawPoint(canvas);
        if (showPop) {
            drawPop(canvas);
        }
    }

    private void drawPop(Canvas canvas) {
        if (canScroll) {
            canvas.drawText("点击" + xList.get(startPoint + tapIndex), startPointX + tapIndex * xLabelWidth, startPointY - yList.get(startPoint + tapIndex) * yLabelHeight - 10f, xPaint);
        } else {
            canvas.drawText("点击" + xList.get(tapIndex), startPointX + tapIndex * xLabelWidth, startPointY - yList.get(tapIndex) * yLabelHeight - 10f, xPaint);
        }

    }

    private void drawPoint(Canvas canvas) {
        //绘制点
        for (int i = startPoint; i < xList.size(); i++) {
            int j = i - startPoint;
            if (xNum != 0 && !canScroll) {
                canvas.drawCircle(startPointX + (j * xLabelWidth / (newXList.size() - 1)), startPointY - (yList.get(i) * yLabelHeight), 5, linePaint);
            } else {
                canvas.drawCircle(startPointX + (j * xLabelWidth), startPointY - (yList.get(i) * yLabelHeight), 5, linePaint);
            }
        }
    }

    private void drawLine(Canvas canvas) {
        //绘制折线
        for (int i = startPoint; i < xList.size() - 1; i++) {
            int j = i - startPoint;
            if (xNum != 0 && !canScroll) {
                canvas.drawLine(startPointX + j * xLabelWidth / (newXList.size() - 1), startPointY - (yList.get(i) * yLabelHeight), startPointX + (j + 1) * xLabelWidth / (newXList.size() - 1), startPointY - (yList.get(i + 1) * yLabelHeight), linePaint);
            } else {
                canvas.drawLine(startPointX + j * xLabelWidth, startPointY - (yList.get(i) * yLabelHeight), startPointX + (j + 1) * xLabelWidth, startPointY - (yList.get(i + 1) * yLabelHeight), linePaint);
            }

        }
    }

    private void drawY(Canvas canvas) {
        //绘制纵坐标
        canvas.drawLine(startPointX, startPointY, startPointX, startPointY - yHeight, yPaint);

        //纵坐标内容
        for (int i = 0; i < newYList.size(); i++) {
            canvas.drawText(newYList.get(i).toString(), yLabelWidth / 2, startPointY - i * yLabelHeight, yPaint);
        }
    }

    private void drawX(Canvas canvas) {
        //绘制横坐标
        canvas.drawLine(startPointX, startPointY, startPointX + xWidth, startPointY, xPaint);

        //横坐标内容
        for (int i = startPoint; i < newXList.size(); i++) {
            int j = i - startPoint;
            Paint.FontMetrics fontMetrics = xPaint.getFontMetrics();
            // 计算文字高度
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            // 计算文字baseline
            float textBaseY = startPointY + xLabelHeight - (xLabelHeight - fontHeight) / 2 - fontMetrics.bottom;

            canvas.drawText(newXList.get(i).toString(), startPointX + j * xLabelWidth, textBaseY, xPaint);
        }
    }


    //排除数组中重复的数据并且返回新的数组
    public static List<Float> ifRepeat(List<Float> arr) {
        //实例化一个set集合
        Set set = new HashSet();
        //遍历数组并存入集合,如果元素已存在则不会重复存入
        for (int i = 0; i < arr.size(); i++) {
            set.add(arr.get(i));
        }
        //返回Set集合的数组形式
        return new ArrayList<>(set);
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        float distant = x - startPointX;
        int index = (int) (distant / xLabelWidth);
        if (xLabelWidth * index < distant) {
            if (distant - xLabelWidth * index < xLabelWidth * (index + 1) - distant) {
                tapIndex = index;
            } else {
                tapIndex = index + 1;
            }
        } else if (xLabelWidth * index == distant) {
            tapIndex = index;
        } else {
            if (distant - xLabelWidth * index < xLabelWidth * (index - 1) - distant) {
                tapIndex = index;
            } else {
                tapIndex = index - 1;
            }
        }
        //显示气泡
        showPop = true;
        invalidate();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        showPop = false;
        offset += v;
        if (Math.abs(offset) >= xLabelWidth) {
            if (offset > 0) {
                if (startPoint + xNum < xList.size() - 1) {
                    startPoint++;
                    invalidate();
                }
            } else {
                if (startPoint > 0) {
                    startPoint--;
                    invalidate();
                }
            }
            offset = 0;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return detector.onTouchEvent(motionEvent);
    }
}
