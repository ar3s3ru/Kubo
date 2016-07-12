package com.github.ar3s3ru.kubo.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
 * RecyclerView Item divider for BoardsListRecycler adapter.
 */
public class BoardsListDivider extends RecyclerView.ItemDecoration {

    // Divider drawable shape
    private Drawable mDivider;

    // Using member variables to deal with onDraw allocations overhead
    private int paddingLeft, paddingRight, paddingTop, paddingBottom;
    private int childIdx, childCount;

    private View currentChild;
    private RecyclerView.LayoutParams currentParams;
    // ---------------------------------------------------------------

    /**
     * Creates a new BoardListDivider with the drawable resId
     */
    public BoardsListDivider(@NonNull Context context, @DrawableRes int resId) {
        // Get divider drawable shape
        mDivider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        paddingLeft  = parent.getPaddingLeft();
        paddingRight = parent.getWidth() - parent.getPaddingRight();

        // How many child views we have?
        childCount = parent.getChildCount();

        for (childIdx = 0; childIdx < childCount; childIdx++) {
            // Get current child view and layout params
            currentChild  = parent.getChildAt(childIdx);
            currentParams = (RecyclerView.LayoutParams) currentChild.getLayoutParams();

            // Calculate top/bottom bounds
            paddingTop    = currentChild.getBottom() + currentParams.bottomMargin;
            paddingBottom = paddingTop + mDivider.getIntrinsicHeight();

            mDivider.setBounds(paddingLeft, paddingTop, paddingRight, paddingBottom);
            mDivider.draw(c);
        }
    }
}
