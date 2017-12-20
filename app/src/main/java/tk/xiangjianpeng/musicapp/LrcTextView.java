package tk.xiangjianpeng.musicapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangjianpeng
 *         自定义绘画歌词
 */

public class LrcTextView extends android.support.v7.widget.AppCompatTextView {
    private long mNextTime = 0; // 保存下一句开始的时间
    private int nextindex = 0;

    private int mOffsetY;   // y上的偏移
    private int mMaxScroll; // 最大滑动距离=一行歌词高度+歌词间距
    public Scroller mScroller;


    private float width;        //歌词视图宽度
    private float height;       //歌词视图高度
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 40;  //文本高度
    private float textSize = 18;        //文本大小
    private int index = 0;      //list集合下标

    int currentcolor=getResources().getColor(R.color.yellow);//当前行颜色
    int notcurrentcolor=getResources().getColor(R.color.yellow_0);//非当前行颜色

    private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

    public void setmLrcList(List<LrcContent> mLrcList) {
        this.mLrcList = mLrcList;
    }

    public LrcTextView(Context context) {
        super(context);
        init(context);
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);     //设置可对焦

        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        //滑动器初始化
        mScroller = new Scroller(context, new LinearInterpolator());
        //获取最大滚动的距离
        mMaxScroll = (int) textHeight;
    }


    /**
     * 绘画歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        if(mOffsetY<2)
            currentPaint.setColor(getResources().getColor(R.color.yellow_1));
        else if(mOffsetY<4)
            currentPaint.setColor(getResources().getColor(R.color.yellow_2));
        else if(mOffsetY<6)
            currentPaint.setColor(getResources().getColor(R.color.yellow_3));
        else if(mOffsetY<24)
            currentPaint.setColor(currentcolor);
        else if(mOffsetY<26)
            currentPaint.setColor(getResources().getColor(R.color.yellow_3));
        else if(mOffsetY<28)
            currentPaint.setColor(getResources().getColor(R.color.yellow_2));
        else
            currentPaint.setColor(getResources().getColor(R.color.yellow_1));
        notCurrentPaint.setColor(notcurrentcolor);

        currentPaint.setTextSize(32);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(32);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {
            setText("");
            canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2, (height - mOffsetY) / 2, currentPaint);

            float tempY = (height - mOffsetY) / 2;
            //画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
            tempY = (height - mOffsetY) / 2;
            //画出本句之后的句子
            for (int i = index + 1; i < mLrcList.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
        } catch (Exception e) {
            setText("歌词秀");
        }
        postInvalidateDelayed(200);
    }

    /**
     * 当view大小改变的时候调用的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }


    public void setIndex(int index) {
        if(Math.abs(index-this.index)==1)
            changeCurrent();
        this.index = index;
        //mOffsetY=0;
    }

    /**
     * 滑动界面代码开始
     */
    // 外部提供方法
    // 传入当前播放时间
    public synchronized void changeCurrent() {
        if (nextindex > index && nextindex < mLrcList.size()) {
            return;
        }
        nextindex=index+1;
        mScroller.abortAnimation();
        mScroller.startScroll(0, 0, 0, mMaxScroll, (int) (mLrcList.get(nextindex).getLrcTime() - mLrcList.get(nextindex-1).getLrcTime()));
        invalidate();
        return;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffsetY = mScroller.getCurrY();
            postInvalidate();
        }
        super.computeScroll();
    }
}

