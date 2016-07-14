package com.github.ar3s3ru.kubo.views.recyclers;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;
import com.github.ar3s3ru.kubo.views.ContentsActivity;
import com.github.ar3s3ru.kubo.views.dialogs.BoardSelectedDialog;

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

    protected static final String TAG = "BoardsListRecycler";

    private FragmentManager mFragmentManager;
    private Cursor mCursor;
    private int    mCount;

    private boolean isStar;

    public BoardsListRecycler(boolean star, KuboSQLHelper helper, FragmentManager fm) {
        isStar = star;
        mFragmentManager = fm;

        // Updating cursor value
        updateCursor(helper);

        // Has stable ids
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        mCount = mCursor.getCount();
        return mCount > 0 ? mCount : 1;
    }

    @Override
    public long getItemId(int position) {
        return mCount > 0 ? KuboTableBoard.getId(mCursor, position) : -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_boardslist, parent, false),
                mFragmentManager, isStar
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCount > 0) {
            holder.setPath(KuboTableBoard.getBoard(mCursor, position));
            holder.title.setText(KuboTableBoard.getTitle(mCursor, position));
            holder.text.setText(Html.fromHtml(
                    KuboTableBoard.getMetaDescription(mCursor, position)
            ));

            holder.noElems.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.GONE);
            holder.text.setVisibility(View.GONE);
            holder.noElems.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update adapter's data with a new cursor
     * @param helper SQLite application helper instance
     */
    public void updateCursor(KuboSQLHelper helper) {
        // Close cursor only if really is there
        if (mCursor != null) {
            mCursor.close();
        }
        // Retrieve new cursor
        mCursor = isStar ? KuboTableBoard.getStarredBoards(helper) :    // Getting starred boards
                           KuboTableBoard.getUnstarredBoards(helper);   // Getting unstarred boards
        // Notify data updated
        notifyDataSetChanged();
    }

    /**
     * Stars/unstars a defined board, removes the board from the dataset and updates the cursor
     * @param helper SQLite application helper instance
     * @param id Board id
     * @param position Board dataset position
     */
    public void removeItem(KuboSQLHelper helper, int id, int position) {
        // Starring/unstarring
        if (isStar) {
            KuboTableBoard.unstarringBoard(helper, id);
        } else {
            KuboTableBoard.starringBoard(helper, id);
        }
        // Notify data removed
        notifyItemRemoved(position);
        // Update cursor
        updateCursor(helper);
    }

    /**
     * Gets Board path of item in position
     * @param position Board item position
     * @return Board path (if position is valid), null otherwise
     */
    public String getItemPath(int position) {
        if (position >= 0 && position < mCount) {
            return KuboTableBoard.getBoard(mCursor, position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.boardslist_title)           TextView title;
        @BindView(R.id.boardslist_description)     TextView text;
        @BindView(R.id.boardslist_text_noelements) TextView noElems;

        private FragmentManager mFragmentManager;
        private boolean mStarred;
        private String  mPath;

        ViewHolder(View itemView, FragmentManager fm, boolean starred) {
            super(itemView);

            // Binding views
            ButterKnife.bind(this, itemView);

            // Setting up private fields
            mFragmentManager = fm;
            mStarred         = starred;

            // Setting up itemView onLongClick listener
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int id = (int) getItemId(); // Gets the ItemId (casting is valid because we use int ids)

            if (id != -1) {
                // id is valid, create new Dialog
                BoardSelectedDialog
                        .newInstance(id, getAdapterPosition(), mStarred, title.getText().toString())
                        .show(mFragmentManager, BoardSelectedDialog.TAG);
                return true;
            }

            // If id is not valid, no longClick is performed
            return false;
        }

        @Override
        public void onClick(View view) {
            int id = (int) getItemId();

            if (id != -1 && mPath != null) {
                ContentsActivity.startContentsActivity(
                        view.getContext(), title.getText().toString(), mPath, id
                );
            }
        }

        public void setPath(@NonNull String path) {
            mPath = path;
        }
    }
}
