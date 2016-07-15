package com.github.ar3s3ru.kubo.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Copyright (C) 2016  Danilo Cianfrone
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

/**
 * Simple divider for RecyclerViews.
 */
public class ListItemDivider extends RecyclerView.ItemDecoration {
    // Using member variables to deal with onDraw allocations overhead
    private int spaceLeft, height;
    private int childIdx, childCount;

    private final Paint mPaint;
    private final Rect  mRect;

    private View currentChild;
    private RecyclerView.LayoutParams currentParams;

    public ListItemDivider(@NonNull Context context, @DimenRes int spaceRes,
                           @DimenRes int heightRes, @ColorRes int colorRes) {
        spaceLeft = context.getResources().getDimensionPixelSize(spaceRes);
        height    = context.getResources().getDimensionPixelSize(heightRes);

        mPaint    = new Paint();
        mRect     = new Rect();

        mPaint.setColor(context.getResources().getColor(colorRes));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        // Needs space for the divider
        outRect.bottom = height;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // Lateral paddings
        mRect.left  = parent.getPaddingLeft() + spaceLeft;
        mRect.right = parent.getWidth() - parent.getPaddingRight();

        // How many child views we have?
        // We don't want to draw the last divider, so we use childs - 1
        childCount = parent.getChildCount() - 1;

        for (childIdx = 0; childIdx < childCount; childIdx++) {
            // Get current child view and layout params
            currentChild  = parent.getChildAt(childIdx);
            currentParams = (RecyclerView.LayoutParams) currentChild.getLayoutParams();

            // Calculate top/bottom paddings
            mRect.top    = currentChild.getBottom() + currentParams.bottomMargin;
            mRect.bottom = mRect.top + height;

            // Drawing divider
            c.drawRect(mRect, mPaint);
        }
    }
}
