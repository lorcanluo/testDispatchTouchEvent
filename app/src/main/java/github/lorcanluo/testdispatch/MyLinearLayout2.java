package github.lorcanluo.testdispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author luocan
 * @version 1.0
 *          </p>
 *          Created on 15/12/3.
 */
public class MyLinearLayout2 extends LinearLayout{
    public MyLinearLayout2(Context context) {
        super(context);
    }

    public MyLinearLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("MyLinearLayout2", "onInterceptTouchEvent");

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("MyLinearLayout2", "dispatchTouchEvent");


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MyLinearLayout2", "onTouchEvent");

        return super.onTouchEvent(event);
    }
}
