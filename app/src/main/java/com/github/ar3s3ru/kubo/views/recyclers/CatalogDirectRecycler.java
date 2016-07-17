package com.github.ar3s3ru.kubo.views.recyclers;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
import com.github.ar3s3ru.kubo.backend.models.Thread;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

public class CatalogDirectRecycler extends RecyclerView.Adapter<CatalogDirectRecycler.ViewHolder>
    implements View.OnClickListener {

    public static final int VIEWTYPE_LIST = 0;
    public static final int VIEWTYPE_GRID = 1;

    private static final int BOOKMARK_KEY  = -1;

    private final TreeSet<Integer> mFollowed;                  // Followed threads
    private final List<Thread>     oList = new ArrayList<>();  // Original untouched list

    private static String mBoard;
    private final  WeakReference<OnClickListener> mListener;

    private int          mViewType = VIEWTYPE_LIST; // Current view type
    private List<Thread> mList     = oList;         // Original filterable list

    public CatalogDirectRecycler(@NonNull OnClickListener listener,
                                 @NonNull KuboSQLHelper helper,
                                 @NonNull List<ThreadsList> list,
                                 @NonNull String board) {
        // Add pages
        for (ThreadsList threadList : list) {
            oList.addAll(threadList.threads);
        }

        // Setting board string
        mBoard    = board;
        mFollowed = KuboTableThread.getFollowedThreadsSet(helper);
        mListener = new WeakReference<>(listener);

        // For ID usage
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).number;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_LIST:
                return new ListViewHolder(
                        mListener,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.adapter_catalog_list, parent, false)
                );
            case VIEWTYPE_GRID:
                return new GridViewHolder(
                        mListener,
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.adapter_catalog_grid, parent, false)
                );
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Thread thread = mList.get(position);
        // Set bookmark icon
        setBookmarkIcon(holder.bookmark, mFollowed.contains(thread.number));
        // Bind ViewHolder
        holder.onBindViewHolder(thread);
    }

    @Override
    public void onClick(View view) {
        final OnClickListener listener = mListener.get();

        if (listener != null) {
            listener.onClick(view.getId());
        }
    }

    /**
     * Sets bookmark icon depending on the bookmark status
     * @param imageView Bookmark ImageView object
     * @param bookmark True if needs to set bookmark, false if needs to remove it
     */
    private static void setBookmarkIcon(@NonNull ImageView imageView, boolean bookmark) {
        imageView.setImageDrawable(
                ContextCompat.getDrawable(
                        imageView.getContext(),
                        bookmark ? R.drawable.ic_bookmark_full : R.drawable.ic_bookmark_empty
                )
        );
        imageView.setTag(BOOKMARK_KEY, bookmark);
    }

    /**
     * Download Thread thumbnail image
     * @param imageView ImageView for thumbnail display
     * @param thread Thread object
     */
    private static void downloadImageForHolder(@NonNull ImageView imageView,
                                               @NonNull Thread thread) {
        Glide.with(imageView.getContext())
                .load(getThumbnailURL(thread.properFilename))
                .error(R.color.colorPrimaryDark)
                .placeholder(R.color.colorAccent)
                .into(imageView);
    }

    /**
     * Get image string URL for thumbnail downloading
     * @param fileName Thumbnail filename
     * @return Thumbnail URL string
     */
    private static String getThumbnailURL(long fileName) {
        return "https://t.4cdn.org/" + mBoard + "/" + fileName + "s.jpg";
    }

    /**
     * Set a new ThreadsList list as adapter dataset
     * @param list New threads list
     */
    public void setList(@NonNull List<ThreadsList> list) {

        // Clear old list
        oList.clear();

        // Add pages
        for (ThreadsList threadList : list) {
            oList.addAll(threadList.threads);
        }

        // Setting up filtered list
        mList = oList;
        // Notifying changes
        notifyDataSetChanged();
    }

    /**
     * Gets Thread object at a certain position into the adapter
     * @param position Thread position into the adapter
     * @return Position related Thread object
     */
    public Thread getThread(int position) {
        return mList.get(position);
    }

    /**
     * Sets the adapter view type to Grid
     */
    public void setGridViewType() {
        mViewType = VIEWTYPE_GRID;
        notifyDataSetChanged();
    }

    /**
     * Sets the adapter view type to List
     */
    public void setListViewType() {
        mViewType = VIEWTYPE_LIST;
        notifyDataSetChanged();
    }

    /**
     * Invoke this method when need to perform a search against thread comments
     * @param query Query to search against thread comments
     */
    public void onQueryText(@NonNull String query) {
        mList = new ArrayList<>();

        for (Thread th : oList) {
            // Filtering
            if (th.comment != null && th.comment.contains(query)) {
                mList.add(th);
            }
        }
        // Notifying new data
        notifyDataSetChanged();
    }

    /**
     * Invoke this method when need to restore original dataset
     */
    public void onClearText() {
        mList = oList;
        notifyDataSetChanged();
    }

    /**
     * Specifies if a certain thread into the adapter is flagged as followed
     * @param threadNumber Thread number
     * @return true if thread is followed, false otherwise
     */
    public boolean isFollowing(int threadNumber) {
        return mFollowed.contains(threadNumber);
    }

    /**
     * Set the thread as followed into the adapter (necessary for bookmark displaying)
     * @param threadNumber Thread number
     */
    public void setFollowing(int threadNumber) {
        mFollowed.add(threadNumber);
    }

    /**
     * Set the thread as followed into the adapter (necessary for bookmark displaying)
     * @param threadNumber Thread number
     */
    public void setUnfollowing(int threadNumber) {
        mFollowed.remove(threadNumber);
    }

    public interface OnClickListener {
        void onClick(int threadNumber);
        void onFollowingThread(int position, int threadNumber);
        void onUnfollowingThread(int threadNumber);
    }

    static abstract class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.catalog_layout)          ViewGroup layout;
        @BindView(R.id.catalog_thread_bookmark) ImageView bookmark;
        @BindView(R.id.catalog_thread_settings) ImageView settings;

        final WeakReference<OnClickListener> listener;

        ViewHolder(@NonNull WeakReference<OnClickListener> listener,
                   @NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.listener = listener;
            layout.setOnClickListener(this);
            bookmark.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final OnClickListener listener = this.listener.get();
            // Calling listener callback
            if (listener != null) {
                if (view.getId() == bookmark.getId()) {
                    // Clicked bookmark
                    handleBookmarkClicked(listener);
                } else {
                    listener.onClick((int) getItemId());
                }
            }
        }

        // TODO: Javadoc
        private void handleBookmarkClicked(@NonNull OnClickListener listener) {
            if ((boolean) bookmark.getTag(BOOKMARK_KEY)) {
                CatalogDirectRecycler.setBookmarkIcon(bookmark, false);
                listener.onUnfollowingThread((int) getItemId());
            } else {
                CatalogDirectRecycler.setBookmarkIcon(bookmark, true);
                listener.onFollowingThread(getAdapterPosition(), (int) getItemId());
            }
        }

        // TODO: Javadoc
        abstract void onBindViewHolder(@NonNull Thread thread);
    }

    /**
     * ViewHolder for list displaying.
     */
    static class ListViewHolder extends CatalogDirectRecycler.ViewHolder {

        @BindView(R.id.catalog_list_thread_thumbnail)    ImageView thumbnail;
        @BindView(R.id.catalog_list_header_name)         TextView  name;
        @BindView(R.id.catalog_list_thread_comment)      TextView  comment;
        @BindView(R.id.catalog_list_header_number)       TextView  number;
        @BindView(R.id.catalog_list_thread_images_text)  TextView  images;
        @BindView(R.id.catalog_list_thread_replies_text) TextView  replies;

        ListViewHolder(@NonNull WeakReference<OnClickListener> listener,
                       @NonNull View itemView) {
            super(listener, itemView);
        }

        /**
         * Binder routine for ListViewHolder
         * @param thread Thread object
         */
        @SuppressWarnings("all")
        @Override
        void onBindViewHolder(@NonNull Thread thread) {
            if (thread.comment != null) {
                comment.setText(Html.fromHtml(thread.comment));
            }

            name.setText(thread.name);
            number.setText(String.format("%d", thread.number));
            images.setText(String.format("%d", thread.images));
            replies.setText(String.format("%d", thread.replies));

            CatalogDirectRecycler.downloadImageForHolder(thumbnail, thread);
        }
    }

    /**
     * ViewHolder for grid displaying.
     */
    static class GridViewHolder extends CatalogDirectRecycler.ViewHolder {

        @BindView(R.id.catalog_grid_thread_thumbnail) ImageView thumbnail;
        @BindView(R.id.catalog_grid_thread_number)    TextView  number;
        @BindView(R.id.catalog_grid_thread_name)      TextView  name;

        GridViewHolder(@NonNull WeakReference<OnClickListener> listener,
                       @NonNull View itemView) {
            super(listener, itemView);
        }

        /**
         * Binder routine for GridViewHolder
         * @param thread Thread object
         */
        @SuppressWarnings("all")
        @Override
        void onBindViewHolder(@NonNull Thread thread) {
            number.setText(String.format("%d", thread.number));
            name.setText(thread.name);

            CatalogDirectRecycler.downloadImageForHolder(thumbnail, thread);
        }
    }
}

