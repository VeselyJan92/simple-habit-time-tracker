package com.janvesely.activitytracker.ui.views;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Scroller;

public abstract class ScrollableChart extends View implements OnGestureListener, AnimatorUpdateListener {
    private int dataOffset;

    private GestureDetector detector;
    private int direction = 1;
    private int maxDataOffset = 10000;
    private ValueAnimator scrollAnimator;
    private ScrollController scrollController;
    private Scroller scroller;
    private int scrollerBucketSize = 1;

    public interface ScrollController {

        public final  class CC {
            public static void $default$onDataOffsetChanged(ScrollController scrollController, int i) {

            }
        }

        void onDataOffsetChanged(int i);
    }

    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public ScrollableChart(Context context) {
        super(context);
        init(context);
    }

    public ScrollableChart(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public int getDataOffset() {
        return this.dataOffset;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (!this.scroller.isFinished()) {
            this.scroller.computeScrollOffset();
            updateDataOffset();
            return;
        }
        this.scrollAnimator.cancel();
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        Scroller scroller2 = this.scroller;
        scroller2.fling(scroller2.getCurrX(), this.scroller.getCurrY(), (this.direction * ((int) f)) / 2, 0, 0, getMaxX(), 0, 0);
        invalidate();
        this.scrollAnimator.setDuration((long) this.scroller.getDuration());
        this.scrollAnimator.start();
        return false;
    }

    private int getMaxX() {
        return this.maxDataOffset * this.scrollerBucketSize;
    }

 /*   public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof BundleSavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        BundleSavedState bundleSavedState = (BundleSavedState) parcelable;
        int i = bundleSavedState.bundle.getInt("x");
        int i2 = bundleSavedState.bundle.getInt("y");
        this.direction = bundleSavedState.bundle.getInt("direction");
        this.dataOffset = bundleSavedState.bundle.getInt("dataOffset");
        this.maxDataOffset = bundleSavedState.bundle.getInt("maxDataOffset");
        this.scroller.startScroll(0, 0, i, i2, 0);
        this.scroller.computeScrollOffset();
        super.onRestoreInstanceState(bundleSavedState.getSuperState());
    }

    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putInt("x", this.scroller.getCurrX());
        bundle.putInt("y", this.scroller.getCurrY());
        bundle.putInt("dataOffset", this.dataOffset);
        bundle.putInt("direction", this.direction);
        bundle.putInt("maxDataOffset", this.maxDataOffset);
        return new BundleSavedState(onSaveInstanceState, bundle);
    }*/

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scrollerBucketSize == 0) {
            return false;
        }
        if (Math.abs(f) > Math.abs(f2)) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        float min = Math.min(((float) (-this.direction)) * f, (float) (getMaxX() - this.scroller.getCurrX()));
        Scroller scroller2 = this.scroller;
        scroller2.startScroll(scroller2.getCurrX(), this.scroller.getCurrY(), (int) min, (int) f2, 0);
        this.scroller.computeScrollOffset();
        updateDataOffset();
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.detector.onTouchEvent(motionEvent);
    }

    public void setScrollDirection(int i) {
        if (i == 1 || i == -1) {
            this.direction = i;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setMaxDataOffset(int i) {
        this.maxDataOffset = i;
        this.dataOffset = Math.min(this.dataOffset, i);
        this.scrollController.onDataOffsetChanged(this.dataOffset);
        postInvalidate();
    }

    public void setScrollController(ScrollController scrollController2) {
        this.scrollController = scrollController2;
    }

    public void setScrollerBucketSize(int i) {
        this.scrollerBucketSize = i;
    }

    private void init(Context context) {
        this.detector = new GestureDetector(context, this);
        this.scroller = new Scroller(context, null, true);
        this.scrollAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.scrollAnimator.addUpdateListener(this);
        this.scrollController = new ScrollController() {
            public /* synthetic */ void onDataOffsetChanged(int i) {
                CC.$default$onDataOffsetChanged(this, i);
            }
        };
    }

    private void updateDataOffset() {
        int min = Math.min(this.maxDataOffset, Math.max(0, this.scroller.getCurrX() / this.scrollerBucketSize));
        if (min != this.dataOffset) {
            this.dataOffset = min;
            this.scrollController.onDataOffsetChanged(this.dataOffset);
            postInvalidate();
        }
    }
}
