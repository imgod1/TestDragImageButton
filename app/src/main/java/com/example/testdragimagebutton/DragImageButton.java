package com.example.testdragimagebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

/**
 * DragImageButton.java。
 *
 * @author gaokang
 * @date 2019/6/11 15:42
 * @update gaokang 2019/6/11 15:42
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class DragImageButton extends AppCompatImageButton {
    public DragImageButton(Context context, boolean canDrag, boolean canAttach) {
        super(context);
        this.canDrag = canDrag;
        this.canAttach = canAttach;
    }

    public DragImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private boolean canDrag = false;
    private boolean canAttach = false;

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragImageButton);
        canDrag = typedArray.getBoolean(R.styleable.DragImageButton_canDrag, false);
        canAttach = typedArray.getBoolean(R.styleable.DragImageButton_canAttach, false);
        typedArray.recycle();
    }

    private float lastRawX;
    private float lastRawY;
    private float parentWidth;
    private float parentHeight;

    private float mRootTopY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canDrag) {
            int action = event.getAction();
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            log("rawX:" + rawX);
            log("rawY:" + rawY);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastRawX = rawX;
                    lastRawY = rawY;
                    ViewGroup mViewGroup = (ViewGroup) getParent();
                    if (mViewGroup != null) {
                        int[] location = new int[2];
                        mViewGroup.getLocationInWindow(location);
                        //获取父布局的高度
                        parentHeight = mViewGroup.getMeasuredHeight();
                        parentWidth = mViewGroup.getMeasuredWidth();
                        //获取父布局顶点的坐标
                        mRootTopY = location[1];
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //获取 此次移动了多少距离
                    float movedX = rawX - lastRawX;
                    float movedY = rawY - lastRawY;
                    //获取view 左上角顶点坐标
                    float ownX = getX();
                    float ownY = getY();
                    //view最大 能移动到什么位置
                    float maxX = parentWidth - getWidth();
                    float maxY = parentHeight - getHeight();
                    //view 理论中应该移动到什么位置
                    float targetX = ownX + movedX;
                    float targetY = ownY + movedY;
                    //理论值和最大值 取一个最小的 并且大于等于0的数值 就是最后要移动的位置
                    targetX = Math.min(targetX, maxX);
                    targetY = Math.min(targetY, maxY);

                    setX(targetX < 0 ? 0 : targetX);
                    setY(targetY < 0 ? 0 : targetY);
                    lastRawX = rawX;
                    lastRawY = rawY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (canAttach) {
                        //应该向右吸顶
                        if (lastRawX > (parentWidth / 2)) {
                            animate()
                                    .setDuration(500)
                                    .setInterpolator(new BounceInterpolator())
                                    .x(parentWidth - getWidth() - 10)
                                    .start();
                        } else {
                            //向左
                            animate()
                                    .setDuration(500)
                                    .setInterpolator(new BounceInterpolator())
                                    .x(10)
                                    .start();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return canDrag ? canDrag : super.onTouchEvent(event);
    }


    private static final String TAG = "DragImageButton";

    public void log(String content) {
        Log.e(TAG, content);
    }
}
