package com.github.ar3s3ru.kubo.views.recyclers;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.models.Thread;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

public class CatalogRecycler extends RecyclerView.Adapter<CatalogRecycler.ViewHolder> {

    private final WeakReference<List<ThreadsList>> mThreadsList;

    private final List<Thread> mList = new ArrayList<>();
    private final String       mBoard;
    private       int          currentPage;

    private final ChangedPageRunnable mRunnable;
    private final Handler             mHandler = new Handler();

    public CatalogRecycler(@NonNull Listener listener,
                           @NonNull List<ThreadsList> list,
                           @NonNull String board) {
        // Listener callback, using WeakReference to avoid GC memory holdings
        mThreadsList = new WeakReference<>(list);
        // Setting up current page
        currentPage = 0;
        // Add first page
        mList.addAll(list.get(currentPage).threads);
        // Setting board string
        mBoard = board;
        // Setting up Runnable
        mRunnable = new ChangedPageRunnable(listener, this , currentPage, 0, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_catalog_list, parent, false)
        );
    }

    @SuppressWarnings("all")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Thread thread = mList.get(position);

        if (thread.comment != null) {
            holder.comment.setText(Html.fromHtml(thread.comment));
        }

        // TODO: change layout!
        holder.name.setText(thread.name);
        holder.number.setText(String.format("%d", thread.number));
        holder.images.setText(String.format("%d", thread.images));
        holder.replies.setText(String.format("%d", thread.replies));

        downloadImageForHolder(holder.thumbnail, mBoard, thread);

        if (position == getItemCount() - 1) {
            // Update page count and dataset!
            updateDataset(position);
        }
    }

    /**
     * Download Thread thumbnail image
     * @param imageView ImageView for thumbnail display
     * @param board Board path
     * @param thread Thread object
     */
    private static void downloadImageForHolder(@NonNull ImageView imageView,
                                               @NonNull String board,
                                               @NonNull Thread thread) {
        Glide.with(imageView.getContext())
                .load(getThumbnailURL(board, thread.properFilename, thread.fileExtension))
                .error(R.color.colorPrimaryDark)
                .placeholder(R.color.colorAccent)
                .into(imageView);
    }

    /**
     * Get image string URL for thumbnail downloading
     * @param board Board path
     * @param fileName Thumbnail filename
     * @param fileExtension Thumbnail file extension
     * @return
     */
    private static String getThumbnailURL(@NonNull String board, long fileName,
                                          @NonNull String fileExtension) {
        // TODO: consider generalizing behavior
        return "https://t.4cdn.org/" + board + "/" + fileName + fileExtension;
    }

    /**
     * Execute adapter dataset update
     * @param currentSize Current dataset list size
     */
    private void updateDataset(int currentSize) {
        final List<ThreadsList> wholeList = mThreadsList.get();

        if (wholeList != null) {
            if (++currentPage <= wholeList.size() - 1) {
                final ThreadsList newPage = wholeList.get(currentPage);
                mList.addAll(newPage.threads);

                // notifyItemRangeInserted(currentSize + 1, newPage.threads.size());
                mRunnable.setPositions(currentPage, currentSize + 1, newPage.threads.size());
                mHandler.post(mRunnable);
            }
        }
    }

    /**
     * Listener interface for page update dataset callback.
     */
    public interface Listener {
        /**
         * This method is invoked when dataset page is changed
         * @param pageNumber Current page number updated
         */
        void onChangedPage(int pageNumber);
    }

    /**
     * Runnable class for update contents delay.
     * (Source: http://stackoverflow.com/questions/26555428/recyclerview-notifyiteminserted-illegalstateexception)
     */
    private static class ChangedPageRunnable implements Runnable {

        private int pageNumber, fromPosition, toSize;
        private final WeakReference<Listener>        mListener;
        private final WeakReference<CatalogRecycler> mAdapter;

        ChangedPageRunnable(@NonNull Listener listener,
                            @NonNull CatalogRecycler adapter,
                            int pageNumber, int fromPosition, int toSize) {
            // Getting references
            mListener = new WeakReference<>(listener);
            mAdapter  = new WeakReference<>(adapter);

            setPositions(pageNumber, fromPosition, toSize);
        }

        /**
         * Set new adapter position, page number and newly added size
         * for notification
         * @param pageNumber New page number
         * @param fromPosition Old adapter position
         * @param toSize New items size
         */
        void setPositions(int pageNumber, int fromPosition, int toSize) {
            this.pageNumber   = pageNumber;
            this.fromPosition = fromPosition;
            this.toSize       = toSize;
        }

        @Override
        public void run() {
            final Listener        listener = mListener.get();
            final CatalogRecycler adapter  = mAdapter.get();

            if (listener != null && adapter != null) {
                listener.onChangedPage(pageNumber + 1);
                adapter.notifyItemRangeInserted(fromPosition, toSize);
                adapter.notifyDataSetChanged();
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.catalog_list_thread_thumbnail)    ImageView thumbnail;
        @BindView(R.id.catalog_list_header_name)         TextView  name;
        @BindView(R.id.catalog_list_thread_comment)      TextView  comment;
        @BindView(R.id.catalog_list_header_number)       TextView  number;
        @BindView(R.id.catalog_list_thread_images_text)  TextView  images;
        @BindView(R.id.catalog_list_thread_replies_text) TextView  replies;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
