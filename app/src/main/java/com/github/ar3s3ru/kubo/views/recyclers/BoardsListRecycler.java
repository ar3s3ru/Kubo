package com.github.ar3s3ru.kubo.views.recyclers;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;

import butterknife.BindView;
import butterknife.ButterKnife;

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

public class BoardsListRecycler extends RecyclerView.Adapter<BoardsListRecycler.ViewHolder> {

    private Cursor mCursor;
    private int    mCount;

    public BoardsListRecycler(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public int getItemCount() {
        mCount = mCursor.getCount();
        return mCount > 0 ? mCount : 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_boardslist, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCount > 0) {
            holder.title.setText(KuboTableBoard.getTitle(mCursor, position));
            holder.text.setText(Html.fromHtml(
                    KuboTableBoard.getMetaDescription(mCursor, position)
            ));
        } else {
            holder.title.setVisibility(View.GONE);
            holder.text.setVisibility(View.GONE);
            holder.noElems.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boardslist_title)           TextView title;
        @BindView(R.id.boardslist_description)     TextView text;
        @BindView(R.id.boardslist_text_noelements) TextView noElems;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
