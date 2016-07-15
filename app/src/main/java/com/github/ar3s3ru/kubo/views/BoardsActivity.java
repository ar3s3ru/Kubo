package com.github.ar3s3ru.kubo.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.views.custom.ListItemDivider;
import com.github.ar3s3ru.kubo.views.dialogs.BoardSelectedDialog;
import com.github.ar3s3ru.kubo.views.recyclers.BoardsListRecycler;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

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

public class BoardsActivity extends KuboActivity
        implements BoardSelectedDialog.Listener, OnMenuTabClickListener {

    @Inject Toast             mToast;
    @Inject KuboSQLHelper     mHelper;
    @Inject SharedPreferences mSharedPrefs;

    @BindView(R.id.activity_main_recyclerview_star_boards)
    RecyclerView mStarRecycler;

    @BindView(R.id.activity_main_recyclerview_unstar_boards)
    RecyclerView mUnstarRecycler;

    private BoardsListRecycler mStarAdapter, mUnstarAdapter;
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Perform injection to have all dependencies ready for use
        ((KuboApp) getApplication()).getAppComponent().inject(this);

        // Perform ButterKnife binding/injection
        ButterKnife.bind(this);

        // Setting up adapters
        mStarAdapter   = new BoardsListRecycler(true, mHelper, getSupportFragmentManager());
        mUnstarAdapter = new BoardsListRecycler(false, mHelper, getSupportFragmentManager());

        settingUpBottomBar(savedInstanceState); // Setting up BottomBar
        flagReadyToRecyclers();                 // Set up recyclers
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onMenuTabReSelected(@IdRes int menuItemId) {
        // On tab reselection, go up
        if (menuItemId == R.id.boards_activity_menu_starred) {
            mStarRecycler.smoothScrollToPosition(0);
        } else if (menuItemId == R.id.boards_activity_menu_unstarred) {
            mUnstarRecycler.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {
        // Change RecyclerViews visibility
        if (menuItemId == R.id.boards_activity_menu_starred) {
            switchVisibility(mUnstarRecycler, mStarRecycler);
        } else if (menuItemId == R.id.boards_activity_menu_unstarred) {
            switchVisibility(mStarRecycler, mUnstarRecycler);
        }
    }

    /**
     * Notifies that a board has been unstarred
     * @param position Starred board position
     */
    @Override
    public void onUnstarSelected(int id, int position) {
        mStarAdapter.removeItem(mHelper, id, position);
        mUnstarAdapter.updateCursor(mHelper);
    }

    /**
     * Notifies that a board has been starred
     * @param position Unstarred board position
     */
    @Override
    public void onStarSelected(int id, int position) {
        mUnstarAdapter.removeItem(mHelper, id, position);
        mStarAdapter.updateCursor(mHelper);
    }

    /**
     * Board selected, starts intent to ThreadsActivity
     * @param starred If board selected is starred or not
     * @param id Board id
     * @param position Board position into the adapter
     */
    @Override
    public void onGoToSelected(String title, boolean starred, int id, int position) {
        final String path =
                starred ? mStarAdapter.getItemPath(position) : mUnstarAdapter.getItemPath(position);

        if (path != null) {
            ContentsActivity.startContentsActivity(this, title, path, id);
        }
    }

    /**
     * Flags the application as ready for boards showing, so
     * sets the RecyclerView with the BoardsListRecycler adapter
     */
    private void flagReadyToRecyclers() {
        // Setting up Starred Recycler adapter
        settingUpRecyclerView(mStarRecycler, mStarAdapter);
        // Setting up Unstarred Recycler adapter
        settingUpRecyclerView(mUnstarRecycler, mUnstarAdapter);
    }

    /**
     * Sets up a RecyclerView with the BoardsListRecycler adapter and a certain cursor
     * @param recyclerView RecyclerView
     * @param adapter Board data adapter
     */
    private void settingUpRecyclerView(@NonNull RecyclerView recyclerView,
                                       @NonNull BoardsListRecycler adapter) {
        // Setting up recyclerView
        recyclerView.addItemDecoration(
                new ListItemDivider(this, R.dimen.listelement_margin,
                        R.dimen.generic_items_divider, R.color.dividerColor)
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Switch visibility between two RecyclerViews
     * @param visible Visible RecyclerView that must become invisible
     * @param invisible Invisible RecyclerView that must become visible
     */
    private void switchVisibility(@NonNull RecyclerView visible, @NonNull RecyclerView invisible) {
        visible.setVisibility(View.GONE);
        invisible.setVisibility(View.VISIBLE);
    }

    /**
     * Setter for BottomBar view object
     * @param savedInstanceState Activity savedInstanceState bundle
     */
    private void settingUpBottomBar(@Nullable Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach(this, savedInstanceState);

        mBottomBar.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        mBottomBar.setMaxFixedTabs(1);                      // Workaround for tab coloring
        mBottomBar.setItems(R.menu.boards_activity_menu);
        mBottomBar.setOnMenuTabClickListener(this);

        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorStarredTab));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorUnstarredTab));
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
