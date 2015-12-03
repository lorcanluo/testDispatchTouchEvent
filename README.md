# Touch事件的分发机制
网上很多用源码来分析touch事件机制的文章，但是由于View和ViewGroup事件分发和android系统事件分开有关系，所以看起来有点云里雾里的，下面自己写了一个例子来说嘛touch分发的原理，和我们工作中遇到此类问题应该怎么处理这类事件，首先说说ViewGroup和View下的三个相关的函数：

* View和ViewGroup下都实现了dispatchTouchEvent（），该方法用户touch事件的分发。
* View比ViewGroup多一个方法叫onTouchEvent（），该方法就是用于具体的touch事件处理。
* ViewGroup比View多一个方法叫onInterceptTouchEvent（），该方法用于拦截touch事件。

接下来我们先看看源码中的官方说明：

```
 /**
     * Pass the touch screen motion event down to the target view, or this
     * view if it is the target.
     *
     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
    }
```
上面写的很清楚，分发事件到对应的view，这是View源码中的类，实际ViewGroup中的更复杂，他多了一个功能就是还要往子View分发事件。


```
/**
     * Implement this method to handle touch screen motion events.
     * <p>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link #performClick()}. This will ensure consistent system behavior,
     * including:
     * <ul>
     * <li>obeying click sound preferences
     * <li>dispatching OnClickListener calls
     * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
     * accessibility features are enabled
     * </ul>
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    public boolean onTouchEvent(MotionEvent event) {
    }
```

上面这个方法就是我们经常用到的，具体对touch事件的处理。


```
  /**
     * Implement this method to intercept all touch screen motion events.  This
     * allows you to watch events as they are dispatched to your children, and
     * take ownership of the current gesture at any point.
     *
     * <p>Using this function takes some care, as it has a fairly complicated
     * interaction with {@link View#onTouchEvent(MotionEvent)
     * View.onTouchEvent(MotionEvent)}, and using it requires implementing
     * that method as well as this one in the correct way.  Events will be
     * received in the following order:
     *
     * <ol>
     * <li> You will receive the down event here.
     * <li> The down event will be handled either by a child of this view
     * group, or given to your own onTouchEvent() method to handle; this means
     * you should implement onTouchEvent() to return true, so you will
     * continue to see the rest of the gesture (instead of looking for
     * a parent view to handle it).  Also, by returning true from
     * onTouchEvent(), you will not receive any following
     * events in onInterceptTouchEvent() and all touch processing must
     * happen in onTouchEvent() like normal.
     * <li> For as long as you return false from this function, each following
     * event (up to and including the final up) will be delivered first here
     * and then to the target's onTouchEvent().
     * <li> If you return true from here, you will not receive any
     * following events: the target view will receive the same event but
     * with the action {@link MotionEvent#ACTION_CANCEL}, and all further
     * events will be delivered to your onTouchEvent() method and no longer
     * appear here.
     * </ol>
     *
     * @param ev The motion event being dispatched down the hierarchy.
     * @return Return true to steal motion events from the children and have
     * them dispatched to this ViewGroup through onTouchEvent().
     * The current target will receive an ACTION_CANCEL event, and no further
     * messages will be delivered here.
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
```

上面这个函数可以看看应该，这个函数是用来拦截touch事件的，默认返回的是false，如果返回true，当前的View的dispatchTouchEvent（）和onTouchEvent（）还会运行，但是子View的相关函数将不再运行。


## 测试工程

下面我用一个例子来说明这个问题，我建立了一个工程，自定义了三个MyLinearLayout，MyLinearLayout1，MyLinearLayout2类继承至LinearLayout,同样的代码如下，但是有三个：

```
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("MyLinearLayout", "onInterceptTouchEvent");

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("MyLinearLayout", "dispatchTouchEvent");


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MyLinearLayout", "onTouchEvent");

        return super.onTouchEvent(event);
    }
}
```

还写了一个MyTextView类，继承于TextView，代码如下：

```
public class MyTestView extends TextView {
    public MyTestView(Context context) {
        super(context);
    }

    public MyTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("MyTestView", "dispatchTouchEvent");

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MyTestView", "onTouchEvent");

        return super.onTouchEvent(event);
    }


}
```

我的布局代码代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="github.lorcanluo.testdispatch.MainActivity">

    <github.lorcanluo.testdispatch.MyLinearLayout
        android:layout_width="match_parent"
        android:layout_height="500dp"
        android:background="#ff0000"
        android:padding="20dp">

        <github.lorcanluo.testdispatch.MyLinearLayout1
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#00ff00"
            android:padding="30dp">

            <github.lorcanluo.testdispatch.MyLinearLayout2
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center"
                android:background="#0000ff">

                <github.lorcanluo.testdispatch.MyTestView
                    android:layout_width="100dp"
                    android:layout_height="100dp"
                    android:gravity="center"
                    android:background="#ffffff"
                    android:text="我就是小打杂" />

            </github.lorcanluo.testdispatch.MyLinearLayout2>

        </github.lorcanluo.testdispatch.MyLinearLayout1>


    </github.lorcanluo.testdispatch.MyLinearLayout>

</RelativeLayout>

```

布局出来的效果如图：

![phone](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/touch/dispatch_phone.png)

接下来我们来使用不同的操作，来输出日志，首先看一下什么都没改的日志输出如下：

![phone](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/touch/normal.png)



### MyTextView的onTouchEvent中返回true

```
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MyTestView", "onTouchEvent");

        return true;
    }
```

那么输出如下：

![phone](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/touch/mytextview_ontouch_return_true.png)

我们可以看到日志中，只有MyTextView的onTouchEvent()事件了，这表示事件已经被我们消耗了，父类不用再处理onTouchEvent（）事件了,如果这里你手动返回false的话，那么父类的onTouchEvent()事件还是会响应的。

### MyTextView的dispatchTouchEvent（）中返回true

```
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("MyTestView", "dispatchTouchEvent");

        return true;
    }

```

dispatchTouchEvent（）函数中返回true以后，表示事件已经被"消耗",那么所有相关的onTouchEvent（）将不再输出，所以我们得出的输出结果如下:

![phone](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/touch/view_dispatch_return_true.png)

如果父类的dispatchTouchEvent（）返回true之后，**本类和父view的onTouchEvent（）事件不再调用，子类的所有touch事件不再调用**，这和接下来的onInterceptTouchEvent（）还是有区别，需要细心分别。




### MyLinearLayout1的onInterceptTouchEvent（）中返回true

```
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("MyLinearLayout1", "onInterceptTouchEvent");


        return true;
    }

```


事件被拦截以后，子view的相关touch事件将不再调用，**但是本类和父类事件还是要调用的**，这里和上面dispatchTouchEvent（）还是有差别，需要仔细区分，我们的输出如下:

![phone](https://raw.githubusercontent.com/lorcanluo/myHugoProject/master/myImage/touch/layout1_intercept_return_true.png)


## 日常处理Touch冲突的常用办法
在日常工作中，我们还是有可能遇到touch事件冲突的问题的，那么有了上面的知识，我们可以通过以上函数处理的组合来处理事件冲突。

1. 如果我们想阻断子View对touch事件的处理，我们可以通过onInterceptTouchEvent（）方法来进行判断是否阻断
2. 如果我们想让父类不再处理onTouchEvent（）事件，我们可以通过在onTouchEvent（）中返回true来进行

但是还可能有更为复杂的情况，这就需要大家去动态的算法处理了。。。。


本文的例子放在了：https://github.com/lorcanluo/testDispatchTouchEvent
