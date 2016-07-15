package com.github.ar3s3ru.kubo.views.recyclers;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;

import java.lang.ref.WeakReference;

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

// TODO: implement two different viewtypes (http://stackoverflow.com/questions/25914003/recyclerview-and-handling-different-type-of-row-inflation)
public class BoardsListRecycler extends RecyclerView.Adapter<BoardsListRecycler.ViewHolder> {

    protected static final String TAG = "BoardsListRecycler";

    private final WeakReference<Listener> mListener;
    private       Cursor                  mCursor;
    private       int                     mCount;

    private boolean isStar;

    public BoardsListRecycler(boolean star, @NonNull KuboSQLHelper helper, @NonNull Listener listener) {
        isStar    = star;
        mListener = new WeakReference<Listener>(listener);

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
                mListener, isStar
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

    public interface Listener {
        void onLongClick(int id, int position, boolean starred, @NonNull String title);
        void onClick(@NonNull String title, @NonNull String path, int id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.boardslist_title)           TextView title;
        @BindView(R.id.boardslist_description)     TextView text;
        @BindView(R.id.boardslist_text_noelements) TextView noElems;

        private final WeakReference<Listener> mListener;
        private final boolean                 mStarred;

        // Board path
        private String mPath;

        ViewHolder(@NonNull View itemView,
                   @NonNull WeakReference<Listener> listener,
                   boolean starred) {

            super(itemView);

            // Binding views
            ButterKnife.bind(this, itemView);

            // Setting up private fields
            mListener = listener;
            mStarred  = starred;

            // Setting up itemView onLongClick listener
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            // Gets the ItemId (casting is valid because we use int ids)
            final int id = (int) getItemId();
            // Gets Listener instance
            final Listener listener = mListener.get();

            if (id != -1 && listener != null) {
                // id is valid, create new Dialog
                listener.onLongClick(id, getAdapterPosition(), mStarred, title.getText().toString());
                return true;
            }

            return false;   // If id is not valid, no longClick is performed
        }

        @Override
        public void onClick(View view) {
            final int id = (int) getItemId();
            final Listener listener = mListener.get();

            if (id != -1 && listener != null && mPath != null) {
                listener.onClick(title.getText().toString(), mPath, id);
            }
        }

        /**
         * Setter for Board path
         * @param path New Board path
         */
        public void setPath(@NonNull String path) {
            mPath = path;
        }
    }
}
