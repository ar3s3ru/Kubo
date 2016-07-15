package com.github.ar3s3ru.kubo.views.recyclers;

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

public class CatalogDirectRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_LIST = 0;
    private static final int VIEWTYPE_GRID = 1;

    private final List<Thread> mList = new ArrayList<>();
    private final String       mBoard;

    private int mViewType = VIEWTYPE_LIST;

    public CatalogDirectRecycler(@NonNull List<ThreadsList> list,
                                 @NonNull String board) {
        // Add pages
        for (ThreadsList threadList : list) {
            mList.addAll(threadList.threads);
        }

        // Setting board string
        mBoard = board;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_LIST:
                return new ListViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.adapter_catalog_list, parent, false)
                );
            case VIEWTYPE_GRID:
                return new GridViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.adapter_catalog_grid, parent, false)
                );
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Thread thread = mList.get(position);

        if (holder instanceof ListViewHolder) {
            onBindListViewHolder((ListViewHolder) holder, thread, mBoard);
        } else if (holder instanceof GridViewHolder) {
            onBindGridViewHolder((GridViewHolder) holder, thread, mBoard);
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

    @SuppressWarnings("all")
    private static void onBindListViewHolder(@NonNull ListViewHolder holder,
                                             @NonNull Thread thread,
                                             @NonNull String board) {
        if (thread.comment != null) {
            holder.comment.setText(Html.fromHtml(thread.comment));
        }

        // TODO: change layout!
        holder.name.setText(thread.name);
        holder.number.setText(String.format("%d", thread.number));
        holder.images.setText(String.format("%d", thread.images));
        holder.replies.setText(String.format("%d", thread.replies));

        downloadImageForHolder(holder.thumbnail, board, thread);
    }

    @SuppressWarnings("all")
    private static void onBindGridViewHolder(@NonNull GridViewHolder holder,
                                             @NonNull Thread thread,
                                             @NonNull String board) {
        downloadImageForHolder(holder.thumbnail, board, thread);
    }

    public void setGridViewType() {
        mViewType = VIEWTYPE_GRID;
        notifyDataSetChanged();
    }

    public void setListViewType() {
        mViewType = VIEWTYPE_LIST;
        notifyDataSetChanged();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.catalog_list_thread_thumbnail)    ImageView thumbnail;
        @BindView(R.id.catalog_list_header_name)         TextView  name;
        @BindView(R.id.catalog_list_thread_comment)      TextView  comment;
        @BindView(R.id.catalog_list_header_number)       TextView  number;
        @BindView(R.id.catalog_list_thread_images_text)  TextView  images;
        @BindView(R.id.catalog_list_thread_replies_text) TextView  replies;

        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.catalog_grid_thread_thumbnail) ImageView thumbnail;
        @BindView(R.id.catalog_grid_thread_bookmark)  ImageView bookmark;
        @BindView(R.id.catalog_grid_thread_settings)  ImageView settings;

        GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

