package com.github.ar3s3ru.kubo.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.views.fragments.RepliesFragment;
import com.github.ar3s3ru.kubo.views.fragments.ThreadsFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

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

public class ContentsActivity extends KuboActivity implements ThreadsFragment.Listener {
    // Activity TAG
    private static final String TAG = "ContentsActivity";
    // Board primary key
    private static final String BOARD_PK = "com.github.ar3s3ru.kubo.views.ContentsActivity.board_pk";
    // Board path id
    private static final String BOARD_ID = "com.github.ar3s3ru.kubo.views.ContentsActivity.board_id";
    // Board title
    private static final String BOARD_TL = "com.github.ar3s3ru.kubo.views.ContentsActivity.board_tl";

    /** Members variables */

    @BindView(R.id.activity_contents_toolbar) Toolbar mToolbar;

    private Drawer  mDrawer;
    private ThreadsFragment threadsFragment;
    private RepliesFragment repliesFragment;

    /** MaterialDrawer elements */
    // TODO: add here

    /**
     * Starts ContentsActivity with provided path and id, from defined context
     * @param context Starting context
     * @param path Board path
     * @param id Board id
     */
    public static void startContentsActivity(@NonNull Context context, @NonNull String title,
                                             @NonNull String path, int id) {
        context.startActivity(
                new Intent(context, ContentsActivity.class)
                        .putExtra(BOARD_PK, id)
                        .putExtra(BOARD_ID, path)
                        .putExtra(BOARD_TL, title)
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        ButterKnife.bind(this);

        // Set up ActionBar
        setSupportActionBar(mToolbar);

        // Set up MaterialDrawer
        setUpNavigationDrawer();

        // Get showed fragments
        threadsFragment = (ThreadsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_contents_fragment_master);

        repliesFragment = (RepliesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_contents_fragment_detail);

        // Freshly started activity, sets up member variables accordingly
        setUpContents(
                getIntent().getStringExtra(BOARD_TL),
                getIntent().getStringExtra(BOARD_ID),
                getIntent().getIntExtra(BOARD_PK, -1)
        );
    }

    @Override
    public void onThreadClick(@NonNull String board, int threadNumber) {
        // TODO: change fragment or send repliesFragment an intent
    }

    // TODO: Javadoc
    private void setUpContents(String title, String path, int primaryKey) {
        threadsFragment.setUpContents(title, path, primaryKey);
    }

    // TODO: Javadoc
    private void setUpNavigationDrawer() {
        mDrawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(mToolbar)
                    .withSliderBackgroundColor(
                            getResources().getColor(R.color.colorVeryDark)
                    )
                    .build();
    }
}
