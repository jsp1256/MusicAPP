package tk.xiangjianpeng.musicapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final int SCROLL_TIME = 500;
    private long mNextTime = 0l; // 保存下一句开始的时间

    private int mViewWidth; // view的宽度
    private int mLrcHeight; // lrc界面的高度
    private int mRows;      // 多少行
    private int mCurrentLine = 0; // 当前行
    private int mOffsetY;   // y上的偏移
    private int mMaxScroll; // 最大滑动距离=一行歌词高度+歌词间距
    public Scroller mScroller;



    private float width;        //歌词视图宽度
    private float height;       //歌词视图高度
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 25;  //文本高度
    private float textSize = 18;        //文本大小
    private int index = 0;      //list集合下标

    private List<LrcContent> mLrcList = new ArrayList<LrcContent>();

    public void setmLrcList(List<LrcContent> mLrcList) {
        this.mLrcList = mLrcList;
    }

    public LrcTextView(Context context) {
        super(context);
        init();
        //滑动弹窗代码
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        //TODO
        //滑动弹窗代码
        mScroller = new Scroller(context, new LinearInterpolator());
        inflateAttributes(attrs);
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mScroller = new Scroller(context, new LinearInterpolator());
        inflateAttributes(attrs);
    }

    private void init() {
        setFocusable(true);     //设置可对焦

        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
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

        currentPaint.setColor(Color.argb(210, 251, 248, 29));
        notCurrentPaint.setColor(Color.argb(140, 255, 255, 255));

        currentPaint.setTextSize(30);
        currentPaint.setTypeface(Typeface.SERIF);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        try {
            setText("");
            canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2, height / 2, currentPaint);

            float tempY = height / 2;
            //画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                //向上推移
                tempY = tempY - textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
            tempY = height / 2;
            //画出本句之后的句子
            for (int i = index + 1; i < mLrcList.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY, notCurrentPaint);
            }
        } catch (Exception e) {
            setText("歌词秀");
        }
    }

    /**
    * 当view大小改变的时候调用的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        //所属：滑动代码
        mViewWidth = getMeasuredWidth();
    }


    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 滑动界面代码开始
     */

    private void inflateAttributes(AttributeSet attrs) {

        //获取最大滚动的距离
        mMaxScroll = (int) textHeight;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    // 外部提供方法
    // 传入当前播放时间
    public synchronized void changeCurrent(long time) {
        // 如果当前时间小于下一句开始的时间
        // 直接return
        if (mNextTime > time) {
            return;
        }

        // 每次进来都遍历存放的时间
        for (int i = 0; i < mLrcList.size(); i++) {
            // 发现这个时间大于传进来的时间
            // 那么现在就应该显示这个时间前面的对应的那一行
            // 每次都重新显示，是不是要判断：现在正在显示就不刷新了
            if (mLrcList.get(i).getLrcTime() > time) {
                mNextTime = mLrcList.get(i).getLrcTime();
                mScroller.abortAnimation();
                mScroller.startScroll(0, 0, 0, mMaxScroll, (int)(mNextTime-time));
//              mNextTime = mTimes.get(i);
//              mCurrentLine = i <= 1 ? 0 : i - 1;
                postInvalidate();
                return;
            }
        }
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            mOffsetY = mScroller.getCurrY();
            if(mScroller.isFinished()) {
                mOffsetY = 0;
            }
            postInvalidate();
        }
    }
}

