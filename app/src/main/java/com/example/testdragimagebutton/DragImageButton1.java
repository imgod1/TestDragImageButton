package com.example.testdragimagebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

/**
 * @author Andy
 * @date 2019/6/3 16:39
 * Desc:
 */
public class DragImageButton1 extends AppCompatImageButton {

    private float mLastRawX;
    private float mLastRawY;
    private final String TAG = "AttachButton";
    private boolean isDrug = false;
    private int mRootMeasuredWidth = 0;
    private int mRootMeasuredHeight = 0;
    private int mRootTopY = 0;
    private boolean isAttach;
    private boolean isDrag;
    private Context mContext;

    public DragImageButton1(Context context) {
        this(context, null);
    }

    public DragImageButton1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragImageButton1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(context, attrs);
    }


    /**
     * 初始化自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedAttay = context.obtainStyledAttributes(attrs, R.styleable.DragImageButton);
        isAttach = mTypedAttay.getBoolean(R.styleable.DragImageButton_canAttach, true);
        isDrag = mTypedAttay.getBoolean(R.styleable.DragImageButton_canDrag, true);
        mTypedAttay.recycle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断是否需要滑动
        if (isDrag) {
            //当前手指的坐标
            float mRawX = ev.getRawX();
            float mRawY = ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://手指按下
                    isDrug = false;
                    //记录按下的位置
                    mLastRawX = mRawX;
                    mLastRawY = mRawY;
                    ViewGroup mViewGroup = (ViewGroup) getParent();
                    if (mViewGroup != null) {
                        int[] location = new int[2];
                        mViewGroup.getLocationInWindow(location);
                        //获取父布局的高度
                        mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                        mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                        //获取父布局顶点的坐标
                        mRootTopY = location[1];
                    }
                    break;
                //手指滑动
                case MotionEvent.ACTION_MOVE:
                    if (mRawX >= 0 && mRawX <= mRootMeasuredWidth && mRawY >= mRootTopY && mRawY <= (mRootMeasuredHeight + mRootTopY)) {
                        //手指X轴滑动距离
                        float differenceValueX = mRawX - mLastRawX;
                        //手指Y轴滑动距离
                        float differenceValueY = mRawY - mLastRawY;
                        //判断是否为拖动操作
                        if (!isDrug) {
                            if (Math.sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) < 2) {
                                isDrug = false;
                            } else {
                                isDrug = true;
                            }
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownX = getX();
                        //获取手指按下的距离与控件本身Y轴的距离
                        float ownY = getY();
                        //理论中X轴拖动的距离
                        float endX = ownX + differenceValueX;
                        //理论中Y轴拖动的距离
                        float endY = ownY + differenceValueY;
                        //X轴可以拖动的最大距离
                        float maxX = mRootMeasuredWidth - getWidth();
                        //Y轴可以拖动的最大距离
                        float maxY = mRootMeasuredHeight - getHeight();
                        //X轴边界限制
                        endX = endX < 0 ? 0 : endX > maxX ? maxX : endX;
                        //Y轴边界限制
                        endY = endY < 0 ? 0 : endY > maxY ? maxY : endY;
                        //开始移动
                        setX(endX);
                        setY(endY);
                        //记录位置
                        mLastRawX = mRawX;
                        mLastRawY = mRawY;
                    }

                    break;
                //手指离开
                case MotionEvent.ACTION_UP:
                    //根据自定义属性判断是否需要贴边
                    if (isAttach) {
                        //判断是否为点击事件
                        if (isDrug) {
                            float center = mRootMeasuredWidth / 2;
                            //自动贴边
                            if (mLastRawX <= center) {
                                //向左贴边
                                this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(10f)
                                        .start();
                            } else {
                                //向右贴边
                                this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(mRootMeasuredWidth - getWidth() - 10f)
                                        .start();
                            }
                        }
                    }
                    break;
                default:
            }
        }
        //是否拦截事件
        return isDrug ? isDrug : super.onTouchEvent(ev);
    }
}
