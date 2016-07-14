package com.github.ar3s3ru.kubo.views.recyclers;

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

public class CatalogRecycler extends RecyclerView.Adapter<CatalogRecycler.ViewHolder> {

    private List<Thread> mList = new ArrayList<>();
    private String mBoard;

    public CatalogRecycler(List<ThreadsList> list, String board) {
        // Populate mList...
        for (ThreadsList tList : list) {
            // ...adding all Thread elements within it
            mList.addAll(tList.threads);
        }
        mBoard = board;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_catalog, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Thread thread = mList.get(position);

        holder.title.setText(thread.semanticUrl);
        if (thread.comment != null) {
            holder.comment.setText(Html.fromHtml(thread.comment));
        }

        holder.thumbnail.setMinimumWidth(thread.thumbWidth);
        holder.thumbnail.setMinimumHeight(thread.thumbHeight);

        Glide.with(holder.thumbnail.getContext())
                .load("http://t.4cdn.org/" + mBoard + "/" + thread.properFilename + "s" + thread.fileExtension)
                .into(holder.thumbnail);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.catalog_thread_thumbnail) ImageView thumbnail;
        @BindView(R.id.catalog_thread_title)     TextView  title;
        @BindView(R.id.catalog_thread_comment)   TextView  comment;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
