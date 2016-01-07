package com.marcusjacobsson.vault.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-08-11.
 */
public class CheckableLayout extends LinearLayout implements Checkable {
    private boolean mChecked;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public CheckableLayout(Context context) {
        super(context);
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }
}
