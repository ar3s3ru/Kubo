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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;
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

    private static final String TAG    = "ThreadsFragment";
    private static final String LIST   = "com.github.ar3s3ru.kubo.views.fragments.threads.list";
    private static final String LAYOUT = "com.github.ar3s3ru.kubo.views.fragments.threads.layout";
    private static final String PATH   = "com.github.ar3s3ru.kubo.views.fragments.threads.path";
    private static final String TITLE  = "com.github.ar3s3ru.kubo.views.fragments.threads.title";

    private static final int IC_GRID = R.drawable.ic_grid;
    private static final int IC_LIST = R.drawable.ic_list;

    private static final int GRID_COLUMNS = 2;
    private static final int LIST_COLUMNS = 1;

    private static final IntentFilter mFilter = new IntentFilter(KuboEvents.CATALOG);

    /** Members variables */
    @BindView(R.id.fragment_threads_viewflipper)  ViewFlipper        mViewFlipper;
    @BindView(R.id.fragment_threads_recyclerview) RecyclerView       mRecycler;
    @BindView(R.id.fragment_threads_swiper)       SwipeRefreshLayout mSwiper;

    @Inject Toast mToast;
    @Inject KuboSQLHelper mHelper;

    private String  mBoardTitle;
    private String  mBoardPath;

    private boolean isGridView  = false;

    private List<ThreadsList> mList;

    private CatalogDirectRecycler mAdapter;
    private CatalogReceiver       mReceiver;
    private GridLayoutManager     mLayoutManager;
    private LocalBroadcastManager mBroadcastManager;

    public ThreadsFragment() {
        // Empty constructor...
    }

    public static ThreadsFragment newInstance(@NonNull String boardTitle,
                                              @NonNull String boardPath) {
        ThreadsFragment threadsFragment = new ThreadsFragment();
        Bundle args = new Bundle();

        args.putString(PATH, boardPath);
        args.putString(TITLE, boardTitle);

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
        mBoardPath  = getArguments().getString(PATH);
        mBoardTitle = getArguments().getString(TITLE);

        // Create new layout manager
        mLayoutManager = new GridLayoutManager(getContext(), LIST_COLUMNS);

        // Create new CatalogReceiver to handle getCatalog(path)
        mReceiver = new CatalogReceiver(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save ThreadsList to avoid downloading
        outState.putParcelable(LIST, Parcels.wrap(mList));
        outState.putParcelable(LAYOUT, mLayoutManager.onSaveInstanceState());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads, container, false);
        ButterKnife.bind(this, view);

        // Recover state
        if (savedInstanceState != null) {
            mList = Parcels.unwrap(savedInstanceState.getParcelable(LIST));
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT));
        }

        // Set swipe callback
        mSwiper.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up ActionBar
        setUpActionBar(((AppCompatActivity) getActivity()).getSupportActionBar());

        // Always displaying first child when creating the view
        mViewFlipper.setDisplayedChild(0);
        // Register Catalog receiver
        mBroadcastManager.registerReceiver(mReceiver, mFilter);

        // Send getCatalog() request
        if (mList == null) { startRequestingCatalog(false); }
        // mList is not null, but maybe there is no adapter...
        else { setUpRecyclerView(); }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister Catalog receiver
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                onChangeViewMode(item);
                return true;
            case R.id.threads_menu_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        startRequestingCatalog(true);
    }

    @Override
    public void onClick(int threadNumber) {
        ((Listener) getActivity()).onThreadClick(
                mBoardPath, threadNumber, mAdapter.isFollowing(threadNumber)
        );
    }

    @Override
    public void onUnfollowingThread(int threadNumber) {
        KuboTableThread.setUnfollowingThread(mHelper, threadNumber);
        mAdapter.setUnfollowing(threadNumber);

        // Notifying Activity
        ((Listener) getActivity()).onChangeFollowingState();
    }

    @Override
    public void onFollowingThread(int position, int threadNumber) {
        KuboTableThread.setFollowingThread(mHelper, mAdapter.getThread(position), mBoardPath);
        mAdapter.setFollowing(threadNumber);

        // Notifying Activity
        ((Listener) getActivity()).onChangeFollowingState();
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
     * Sets up ActionBar title
     * @param actionBar ActionBar support xv7
     */
    private void setUpActionBar(@Nullable ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setTitle(mBoardTitle);
        }
    }

    // TODO: Javadoc
    private void startRequestingCatalog(boolean flip) {
        // Waiting for progress...
        if (flip) {
            mViewFlipper.showPrevious();
        }

        // Request Catalog
        KuboRESTService.getCatalog(getContext(), mBoardPath);
    }

    // TODO: Javadoc
    private void setUpRecyclerView() {
        if (mAdapter != null || mSwiper.isRefreshing()) {
            mAdapter.setList(mList);
            mSwiper.setRefreshing(false);
        } else {
            // Set up adapter
            mAdapter = new CatalogDirectRecycler(this, mHelper, mList, mBoardPath);
        }

        // Set up Recycler
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), isGridView ? GRID_COLUMNS : LIST_COLUMNS));
        // Show recycler
        mViewFlipper.showNext();
    }

    // TODO: Javadoc
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
        mList = catalog;
        setUpRecyclerView();
    }

    /**
     * Handle error on getCatalog(path) HTTP call
     * @param error Error string encoded
     * @param errorCode Error HTTP code
     */
    private void handleCatalogError(String error, int errorCode) {
        mToast.setText(errorCode + ": " + error);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    // TODO: Javadoc
    public void setUpContents(@NonNull String title, @NonNull String path) {
        mBoardTitle = title;
        mBoardPath  = path;
    }

    // TODO: Javadoc
    public void updateContents(String title, String path) {
        setUpContents(title, path);
        startRequestingCatalog(true);
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

        /**
         * Callback executed when changing a thread's follow state
         */
        void onChangeFollowingState();
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
                    threadsFragment.handleCatalogError(
                            intent.getStringExtra(KuboEvents.BOARDS_ERR),
                            intent.getIntExtra(KuboEvents.BOARDS_ERRCOD, 0)
                    );
                }
            }
        }
    }

}
