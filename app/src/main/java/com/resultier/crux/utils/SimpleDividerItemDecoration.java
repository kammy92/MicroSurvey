package com.resultier.crux.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.resultier.crux.R;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int margin_top;
    private int margin_bottom;
    private int margin_left;
    private int margin_right;
    private Drawable mDivider;
    
    public SimpleDividerItemDecoration (Context context, @IntRange(from = 0) int margin_top, @IntRange(from = 0) int margin_bottom, @IntRange(from = 0) int margin_right, @IntRange(from = 0) int margin_left) {
        mDivider = context.getResources ().getDrawable (R.drawable.line_divider);
        this.margin_top = margin_top;
        this.margin_bottom = margin_bottom;
        this.margin_left = margin_left;
        this.margin_right = margin_right;
    }
    
    @Override
    public void onDrawOver (Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft () + (int)(margin_left * 0.5f);
        int right = parent.getWidth () - parent.getPaddingRight () - (int)(margin_right * 0.5f);
        
        int childCount = parent.getChildCount ();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt (i);
            
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams ();
            
            int top = child.getBottom () + params.bottomMargin + margin_bottom / 2;
            int bottom = top + mDivider.getIntrinsicHeight ();
            
            mDivider.setBounds (left, top, right, bottom);
            mDivider.draw (c);
        }
        
    }
}