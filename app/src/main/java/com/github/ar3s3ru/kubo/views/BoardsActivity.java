package com.github.ar3s3ru.kubo.views;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;
import com.github.ar3s3ru.kubo.backend.receivers.BoardsReceiver;
import com.github.ar3s3ru.kubo.utils.KuboUtilities;
import com.github.ar3s3ru.kubo.views.custom.BoardsListDivider;
import com.github.ar3s3ru.kubo.views.recyclers.BoardsListRecycler;

import javax.inject.Inject;

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

public class BoardsActivity extends KuboActivity {

    private BoardsReceiver mBoardReceiver;
    private boolean isRegistered = false;

    @Inject Toast             mToast;
    @Inject KuboSQLHelper     mHelper;
    @Inject SharedPreferences mSharedPrefs;

    @BindView(R.id.activity_main_recyclerview_star_boards)
    RecyclerView mStarRecycler;

    @BindView(R.id.activity_main_recyclerview_unstar_boards)
    RecyclerView mUnstarRecycler;

    @BindView(R.id.activity_main_starred_header)
    TextView starredHeader;

    @BindView(R.id.activity_main_unstarred_header)
    TextView unstarredHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Perform injection to have all dependencies ready for use
        ((KuboApp) getApplication()).getAppComponent().inject(this);

        // Perform ButterKnife binding/injection
        ButterKnife.bind(this);

        // Initial views setup
        mStarRecycler.setVisibility(View.GONE);
        mUnstarRecycler.setVisibility(View.GONE);
        starredHeader.setVisibility(View.GONE);
        unstarredHeader.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (KuboUtilities.hasToDownloadBoards(mSharedPrefs)) {
            // Register receiver
            mBoardReceiver = new BoardsReceiver(this);
            mBroadcastManager.registerReceiver(mBoardReceiver, new IntentFilter(KuboEvents.BOARDS));
            isRegistered = true;
            // Request boards
            KuboRESTService.getBoards(this);
        } else {
            // Recyclers can be visible now
            flagReadyToRecyclers();

            // Headers visible
            starredHeader.setVisibility(View.VISIBLE);
            unstarredHeader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isRegistered) {
            mBroadcastManager.unregisterReceiver(mBoardReceiver);
            isRegistered = false;
        }
    }

    /**
     * Disables boards download on startup (updating the shared preferences)
     */
    public void disableStartupDownload() {
        // Remind that we don't need to download on startup anymore
        KuboUtilities.disableStartupBoards(mSharedPrefs);
    }

    /**
     * Flags the application as ready for boards showing, so
     * sets the RecyclerView with the BoardsListRecycler adapter
     */
    public void flagReadyToRecyclers() {
        // Setting up Starred Recycler adapter
        settingUpRecyclerView(mStarRecycler, KuboTableBoard.getStarredBoards(mHelper));
        // Setting up Unstarred Recycler adapter
        settingUpRecyclerView(mUnstarRecycler, KuboTableBoard.getUnstarredBoards(mHelper));
    }

    /**
     * Sets up a RecyclerView with the BoardsListRecycler adapter and a certain cursor
     * @param recyclerView RecyclerView
     * @param cursor Data cursor
     */
    private void settingUpRecyclerView(@NonNull RecyclerView recyclerView, @NonNull Cursor cursor) {
        // Setting up recyclerView
        recyclerView.addItemDecoration(new BoardsListDivider(this, R.drawable.list_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BoardsListRecycler(cursor));

        // Recycler can be visible again
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Handles errors notifications through Toast instance
     * @param error Error description
     * @param errorCode Error HTTP/application code
     */
    public void showToastError(String error, int errorCode) {
        mToast.setText(errorCode + ": " + error);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }
}
