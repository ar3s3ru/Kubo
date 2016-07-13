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

public class BoardsListDivider extends RecyclerView.ItemDecoration {

    private static final int DIVIDER_HEIGHT = 1;

    private int   spaceLeft;
    private Paint mPaint;
    private Rect  mRect;

    // Using member variables to deal with onDraw allocations overhead
    private int childIdx, childCount;

    private View currentChild;
    private RecyclerView.LayoutParams currentParams;

    public BoardsListDivider(@NonNull Context context, @DimenRes int spaceRes,
                             @ColorRes int colorRes) {
        spaceLeft = context.getResources().getDimensionPixelSize(spaceRes);
        mPaint    = new Paint();
        mRect     = new Rect();

        mPaint.setColor(context.getResources().getColor(colorRes));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        mRect.left  = parent.getPaddingLeft() + spaceLeft;
        mRect.right = parent.getWidth() - parent.getPaddingRight();

        // How many child views we have?
        childCount = parent.getChildCount();

        for (childIdx = 0; childIdx < childCount; childIdx++) {
            // Get current child view and layout params
            currentChild  = parent.getChildAt(childIdx);
            currentParams = (RecyclerView.LayoutParams) currentChild.getLayoutParams();

            // Calculate top/bottom bounds
            mRect.top    = currentChild.getBottom() + currentParams.bottomMargin;
            mRect.bottom = mRect.top + DIVIDER_HEIGHT;

            c.drawRect(mRect, mPaint);
        }
    }
}
