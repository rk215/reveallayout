package com.hendraanggrian.circularreveal.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.hendraanggrian.circularreveal.animation.SupportAnimator;
import com.hendraanggrian.circularreveal.animation.ViewAnimationUtils;
import com.hendraanggrian.circularrevealactivitydialog.R;

/**
 * Created by victorleonardo on 11/29/15.
 */
public class RevealRelativeLayout extends RelativeLayout {

    private boolean isDialog;
    private SupportAnimator ANIMATOR;

    // from java
    private int REVEAL_X;
    private int REVEAL_Y;
    // from xml
    private int REVEAL_EXIT_DURATION;
    private int REVEAL_OPEN_DURATION;
    private final int REVEAL_DURATION_DEFAULT = 500;

    public RevealRelativeLayout(Context context) {
        super(context);
    }

    public RevealRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        animateOpen(context);
    }

    public RevealRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        animateOpen(context);
    }

    public void setLocation(int REVEAL_X, int REVEAL_Y) {
        this.REVEAL_X = REVEAL_X;
        this.REVEAL_Y = REVEAL_Y;
    }

    public void isDialog() {
        isDialog = true;
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.CircularReveal);
        REVEAL_EXIT_DURATION = typedArray.getInteger(R.styleable.CircularReveal_reveal_exit_duration, REVEAL_DURATION_DEFAULT);
        REVEAL_OPEN_DURATION = typedArray.getInteger(R.styleable.CircularReveal_reveal_open_duration, REVEAL_DURATION_DEFAULT);
        typedArray.recycle();
    }

    private void animateOpen(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);

                    if (isDialog) {

                        Display display = null;
                        if (context instanceof Activity)
                            display = ((Activity) context).getWindowManager().getDefaultDisplay();
                        else if (context instanceof ContextWrapper)
                            display = ((Activity) ((ContextWrapper) context).getBaseContext()).getWindowManager().getDefaultDisplay();

                        Point size = new Point();
                        display.getSize(size);
                        double xScalePoint = size.x / REVEAL_X;
                        double yScalePoint = size.y / REVEAL_Y;

                        // animate from custom coordinate
                        REVEAL_X = new Double((getLeft() + getRight()) / xScalePoint).intValue();
                        REVEAL_Y = new Double((getTop() + getBottom()) / yScalePoint).intValue();
                    }

                    // get the final radius for the clipping circle
                    int finalRadius = Math.max(getWidth(), getHeight());

                    ANIMATOR = ViewAnimationUtils.createCircularReveal(RevealRelativeLayout.this, REVEAL_X, REVEAL_Y, 0, finalRadius);
                    ANIMATOR.setInterpolator(new AccelerateDecelerateInterpolator());
                    ANIMATOR.setDuration(REVEAL_OPEN_DURATION);
                    ANIMATOR.start();
                }
            });
    }

    public void animateExit(final Activity mActivity) {
        if (!ANIMATOR.isRunning())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ANIMATOR = ANIMATOR.reverse();
                ANIMATOR.setInterpolator(new AccelerateDecelerateInterpolator());
                ANIMATOR.setDuration(REVEAL_EXIT_DURATION);
                ANIMATOR.start();
                ANIMATOR.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        setVisibility(View.GONE);
                        mActivity.finish();
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
            }
    }

    public void animateExit(final Dialog mDialog) {
        if (!ANIMATOR.isRunning())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ANIMATOR = ANIMATOR.reverse();
                ANIMATOR.setInterpolator(new AccelerateDecelerateInterpolator());
                ANIMATOR.setDuration(REVEAL_EXIT_DURATION);
                ANIMATOR.start();
                ANIMATOR.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        setVisibility(View.GONE);
                        mDialog.dismiss();
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
            }
    }
}