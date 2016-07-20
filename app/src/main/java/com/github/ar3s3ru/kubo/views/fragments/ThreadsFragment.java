package com.github.ar3s3ru.kubo.views.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;
import com.github.ar3s3ru.kubo.backend.net.KuboEvents;
import com.github.ar3s3ru.kubo.backend.net.KuboRESTService;
import com.github.ar3s3ru.kubo.utils.KuboStateListener;
import com.github.ar3s3ru.kubo.views.dialogs.ErrorDialog;
import com.github.ar3s3ru.kubo.views.recyclers.CatalogDirectRecycler;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;
import java.util.List;

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

public class ThreadsFragment extends Fragment implements CatalogDirectRecycler.OnClickListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    // For debug purposes
    public static final String TAG    = "ThreadsFragment";

    private static final String LIST   = "com.github.ar3s3ru.kubo.views.fragments.threads.list";
    private static final String LAYOUT = "com.github.ar3s3ru.kubo.views.fragments.threads.layout";
    private static final String PATH   = "com.github.ar3s3ru.kubo.views.fragments.threads.path";

    // ActionBar icons
    private static final int IC_GRID = R.drawable.ic_grid;
    private static final int IC_LIST = R.drawable.ic_list;

    // RecyclerView columns count
    private static final int GRID_COLUMNS = 2;
    private static final int LIST_COLUMNS = 1;

    // BroadcastReceiver intent filter (we could use just one instance at all)
    private static final IntentFilter mFilter = new IntentFilter(KuboEvents.CATALOG);

    /** Members variables */
    @BindView(R.id.fragment_threads_viewflipper)  ViewFlipper        mViewFlipper;
    @BindView(R.id.fragment_threads_recyclerview) RecyclerView       mRecycler;
    @BindView(R.id.fragment_threads_swiper)       SwipeRefreshLayout mSwiper;

    @Inject KuboSQLHelper mHelper;          // To handle the database

    private String mBoardPath;              // Board path to use for catalog requests

    private boolean isGridView  = false;    // Indicates LayoutManager view mode

    private List<ThreadsList> mList;        // Dataset reference

    private CatalogDirectRecycler mAdapter;
    private CatalogReceiver       mReceiver;
    private LocalBroadcastManager mBroadcastManager;

    public ThreadsFragment() {
        // Empty constructor...
    }

    /**
     * Return a new ThreadsFragment instance for the specified board
     * @param boardPath Board path
     * @return New ThreadsFragment instance
     */
    public static ThreadsFragment newInstance(@NonNull String boardPath) {
        ThreadsFragment threadsFragment = new ThreadsFragment();
        Bundle args = new Bundle();

        args.putString(PATH, boardPath);

        threadsFragment.setArguments(args);
        return threadsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For rendering menu
        setHasOptionsMenu(true);

        // Inject everything from Dagger
        ((KuboApp) getActivity().getApplication()).getAppComponent().inject(this);

        // Get required arguments
        mBoardPath = getArguments().getString(PATH);

        // Create new CatalogReceiver to handle getCatalog(path)
        mReceiver         = new CatalogReceiver(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save ThreadsList to avoid downloading and save LayoutManager state.
        // BEWARE: only if the arguments we want to save are not null
        if (mList != null) { outState.putParcelable(LIST, Parcels.wrap(mList)); }
        if (mRecycler != null) {
            outState.putParcelable(LAYOUT, mRecycler.getLayoutManager().onSaveInstanceState());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads, container, false);
        ButterKnife.bind(this, view);

        // Set up recycler view LayoutManager
        mRecycler.setLayoutManager(new GridLayoutManager(
                getContext(), isGridView ? GRID_COLUMNS : LIST_COLUMNS
        ));

        // Recover state
        if (savedInstanceState != null) {
            mList = Parcels.unwrap(savedInstanceState.getParcelable(LIST));
            mRecycler.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT));
        }

        // Set swipe callback
        mSwiper.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register Catalog receiver
        mBroadcastManager.registerReceiver(mReceiver, mFilter);

        // Send getCatalog() request
        if (mList == null) {
            mViewFlipper.setDisplayedChild(0);
            startRequestingCatalog();
        } else {
            mViewFlipper.setDisplayedChild(1);
            // mList is not null, but maybe there is no adapter...
            setUpRecyclerAdapter();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister Catalog receiver
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu
        inflater.inflate(R.menu.threads_fragment_menu, menu);

        // Retrieving search menu item
        final MenuItem   item       = menu.findItem(R.id.threads_menu_search);
        final SearchView searchView = (SearchView) item.getActionView();

        // Setting up SearchView
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.threads_menu_viewmode:
                // Change View Mode only if the Recycler has an adapter already
                // TODO: maybe it always has a layout manager?
                if (mRecycler.getAdapter() != null) {
                    onChangeViewMode(item);
                    return true;
                }
                // ...otherwise, no selection happened :-)
                return false;
            case R.id.threads_menu_search:
                // When selecting for search always return true if Recycler has an adapter
                return mRecycler.getAdapter() != null;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        // Swiped for refresh
        startRequestingCatalog();
    }

    /**
     * Adapter callback to notify a thread click, so propagate it to the Listener/Activity
     * @param threadNumber Clicked thread number
     */
    @Override
    public void onClick(int threadNumber) {
        // Thread clicked, callback to the activity to create a RepliesFragment
        ((Listener) getActivity()).onThreadClick(
                mBoardPath, threadNumber, mAdapter.isFollowing(threadNumber)
        );
    }

    /**
     * Callback for unfollowed thread state change
     * @param threadNumber Unfollowed thread number
     */
    @Override
    public void onUnfollowingThread(int threadNumber) {
        // Change database and adapter state
        KuboTableThread.setUnfollowingThread(mHelper, threadNumber);
        mAdapter.setUnfollowing(threadNumber);

        // Notifying Activity
        ((KuboStateListener) getActivity()).onChangeFollowingState();
    }

    /**
     * Callback for followed thread state change
     * @param position Followed thread position into the adapter
     * @param threadNumber Followed thread number
     */
    @Override
    public void onFollowingThread(int position, int threadNumber) {
        // Change database and adapter state
        KuboTableThread.setFollowingThread(mHelper, mAdapter.getThread(position), mBoardPath);
        mAdapter.setFollowing(threadNumber);

        // Notifying Activity
        ((KuboStateListener) getActivity()).onChangeFollowingState();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals("")) {
            // Restore original dataset
            mAdapter.onClearText();
        } else {
            // Perform filtering
            mAdapter.onQueryText(newText);
        }
        return true;
    }

    /**
     * Start a catalog request to the Retrofit API instance,
     * using the object's board path value
     */
    private void startRequestingCatalog() {
        // Waiting for progress...
        if (mViewFlipper.getDisplayedChild() == 1) { mViewFlipper.showPrevious(); }
        // Request Catalog
        KuboRESTService.getCatalog(getContext(), mBoardPath);
    }

    /**
     * Sets up the RecyclerAdapter for the RecyclerView (and updating the swipeview state
     * if needed)
     */
    private void setUpRecyclerAdapter() {
        // Adapter set (like activity resumed)
        if (mAdapter != null || mSwiper.isRefreshing()) {
            mAdapter.setList(mList);
            mSwiper.setRefreshing(false);
        } else {
            // Adapter not set (like first run or refresh issued)...
            // ...so, set up the adapter
            mAdapter = new CatalogDirectRecycler(this, mHelper, mList, mBoardPath);
        }

        // Set up Recycler
        mRecycler.setAdapter(mAdapter);
        // Show recycler
        if (mViewFlipper.getDisplayedChild() == 0) { mViewFlipper.showNext(); }
    }

    /**
     * Changes the RecyclerView's LayoutManager column count if the View mode button
     * into the ActionBar is clicked
     * @param item ViewMode menu button
     */
    private void onChangeViewMode(@NonNull MenuItem item) {
        if (isGridView) {
            item.setIcon(IC_GRID);
            // Handle list view
            mAdapter.setListViewType();
            ((GridLayoutManager) mRecycler.getLayoutManager()).setSpanCount(LIST_COLUMNS);
        } else {
            item.setIcon(IC_LIST);
            // Handle grid view
            mAdapter.setGridViewType();
            ((GridLayoutManager) mRecycler.getLayoutManager()).setSpanCount(GRID_COLUMNS);
        }

        // Change isGridView value
        isGridView = !isGridView;
    }

    /**
     * Handle successful HTTP call of getCatalog(path)
     * @param catalog Catalog result
     */
    private void handleCatalogSuccess(List<ThreadsList> catalog) {
        mList = catalog;        // New list received
        setUpRecyclerAdapter(); // Change adapter state
    }

    /**
     * Handle error on getCatalog(path) HTTP call displaying a dialog to the user
     * @param intent Action intent (for retrying)
     */
    private void handleCatalogError(@NonNull Intent intent) {
        // Error received, show a dialog
        ErrorDialog.newInstance(
                intent,
                intent.getStringExtra(KuboEvents.CATALOG_ERROR),
                intent.getIntExtra(KuboEvents.CATALOG_ERRCOD, 0)
        ).show(getActivity().getSupportFragmentManager(), ErrorDialog.TAG);
    }

    /**
     * Listener for ThreadsFragment callbacks to the activity.
     */
    public interface Listener {
        /**
         * Callback when thread clicking is performed
         * @param board Board name
         * @param threadNumber Thread number
         * @param followed true if thread is followed, false otherwise
         */
        void onThreadClick(@NonNull String board, int threadNumber, boolean followed);
    }

    /**
     * BroadcastReceiver for getCatalog() request.
     */
    static class CatalogReceiver extends BroadcastReceiver {

        private final WeakReference<ThreadsFragment> mFragment;

        CatalogReceiver(@NonNull ThreadsFragment threadsFragment) {
            mFragment = new WeakReference<>(threadsFragment);
        }

        @SuppressWarnings("unchecked")  // Annoia eh...
        @Override
        public void onReceive(Context context, Intent intent) {
            ThreadsFragment threadsFragment = mFragment.get();

            if (threadsFragment != null) {
                if (intent.getBooleanExtra(KuboEvents.CATALOG_STATUS, false)) {
                    // Success
                    threadsFragment.handleCatalogSuccess(
                            // Cast should work... :-/
                            (List<ThreadsList>) Parcels.unwrap(
                                    intent.getParcelableExtra(KuboEvents.CATALOG_RESULT)
                            )
                    );
                } else {
                    // Error, handle into the fragment
                    threadsFragment.handleCatalogError(intent);
                }
            }
        }
    }

}
