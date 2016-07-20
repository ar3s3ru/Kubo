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
        implements BoardSelectedDialog.Listener, OnMenuTabClickListener, BoardsListRecycler.Listener {

    @Inject Toast             mToast;
    @Inject KuboSQLHelper     mHelper;
    @Inject SharedPreferences mSharedPrefs;

    @BindView(R.id.activity_main_recyclerview_star_boards)
    RecyclerView mFavoriteRecycler;

    @BindView(R.id.activity_main_recyclerview_unstar_boards)
    RecyclerView mUnfavoriteRecycler;

    private BoardsListRecycler mFavoriteAdapter, mUnfavoriteAdapter;
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
        mFavoriteAdapter = new BoardsListRecycler(true, mHelper, this);
        mUnfavoriteAdapter = new BoardsListRecycler(false, mHelper, this);

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
        if (menuItemId == R.id.boards_activity_menu_favorites) {
            mFavoriteRecycler.smoothScrollToPosition(0);
        } else if (menuItemId == R.id.boards_activity_menu_unfavorites) {
            mUnfavoriteRecycler.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {
        // Change RecyclerViews visibility
        if (menuItemId == R.id.boards_activity_menu_favorites) {
            switchVisibility(mUnfavoriteRecycler, mFavoriteRecycler);
        } else if (menuItemId == R.id.boards_activity_menu_unfavorites) {
            switchVisibility(mFavoriteRecycler, mUnfavoriteRecycler);
        }
    }

    /**
     * Notifies that a board has been unfavorite
     * @param position Favorite board position
     */
    @Override
    public void onUnfavoriteSelected(int id, int position) {
        mFavoriteAdapter.removeItem(mHelper, id, position);
        mUnfavoriteAdapter.updateCursor(mHelper);
    }

    /**
     * Notifies that a board has been favorite
     * @param position Unfavorite board position
     */
    @Override
    public void onFavoriteSelected(int id, int position) {
        mUnfavoriteAdapter.removeItem(mHelper, id, position);
        mFavoriteAdapter.updateCursor(mHelper);
    }

    /**
     * Board selected, starts intent to ThreadsActivity
     * @param favorited If board selected is favorite or not
     * @param position Board position into the adapter
     */
    @Override
    public void onGoToSelected(String title, boolean favorited, int position) {
        final String path = favorited ?
                mFavoriteAdapter.getItemPath(position) :
                mUnfavoriteAdapter.getItemPath(position);
        // Start new ContentsActivity
        if (path != null) { onClick(title, path); }
    }

    @Override
    public void onClick(@NonNull String title, @NonNull String path) {
        ContentsActivity.startContentsActivity(this, title, path);
    }

    @Override
    public void onLongClick(int id, int position, boolean favorited, @NonNull String title) {
        BoardSelectedDialog
                .newInstance(id, position, favorited, title)
                .show(getSupportFragmentManager(), BoardSelectedDialog.TAG);
    }

    /**
     * Flags the application as ready for boards showing, so
     * sets the RecyclerView with the BoardsListRecycler adapter
     */
    private void flagReadyToRecyclers() {
        // Setting up Favorite Recycler adapter
        settingUpRecyclerView(mFavoriteRecycler, mFavoriteAdapter);
        // Setting up Unfavorite Recycler adapter
        settingUpRecyclerView(mUnfavoriteRecycler, mUnfavoriteAdapter);
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

        mBottomBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        mBottomBar.setMaxFixedTabs(1);                      // Workaround for tab coloring
        mBottomBar.setItems(R.menu.boards_activity_menu);
        mBottomBar.setOnMenuTabClickListener(this);

        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorFavoriteTab));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorUnfavoriteTab));
    }
}
