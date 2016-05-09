package com.teambarq.barq;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by jojortz on 5/9/2016.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
    private boolean checked = false;
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean _checked) {
        checked = _checked;
        refreshDrawableState();
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

}