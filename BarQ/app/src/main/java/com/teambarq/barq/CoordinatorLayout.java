package com.teambarq.barq;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.teambarq.barq.swipelib.AbsCoordinatorLayout;
import com.teambarq.barq.swipelib.SwipeLayout;

public class CoordinatorLayout extends AbsCoordinatorLayout {

    public interface OnServeListener {
        void onDismissed();
    }

    private View mBackgroundView;
    private OnServeListener mOnServeListener;

    private View mDelete;
    private SwipeLayout mForegroundView;

    public CoordinatorLayout(Context context) {
        super(context);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void doInitialViewsLocation() {
        mForegroundView = (SwipeLayout) findViewById(R.id.foregroundView);
        mDelete = findViewById(R.id.delete);
        mBackgroundView = findViewById(R.id.backgroundView);
        mForegroundView.anchor(-mDelete.getWidth(), 0, mBackgroundView.getRight());
    }

    public void setOnServeListener(OnServeListener listener) {
        mOnServeListener = listener;
    }

    @Override
    public void onTranslateChange(float global, int index, float relative) {
        if (global == 1) {
            mOnServeListener.onDismissed();
        }

        if (global > 0.5) {
            mDelete.setVisibility(View.GONE);
        } else {
            mDelete.setVisibility(View.VISIBLE);
        }
    }
}
