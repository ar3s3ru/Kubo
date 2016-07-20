package com.github.ar3s3ru.kubo.views.fragments;

import android.app.NotificationManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.github.ar3s3ru.kubo.backend.models.RepliesList;
import com.github.ar3s3ru.kubo.backend.net.KuboEvents;
import com.github.ar3s3ru.kubo.backend.net.KuboRESTService;
import com.github.ar3s3ru.kubo.utils.KuboStateListener;
import com.github.ar3s3ru.kubo.views.custom.ListItemDivider;
import com.github.ar3s3ru.kubo.views.dialogs.ErrorDialog;
import com.github.ar3s3ru.kubo.views.recyclers.RepliesAdapter;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.NOTIFICATION_SERVICE;

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

public class RepliesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LIST   = "com.github.ar3s3ru.kubo.views.fragments.replies.list";
    private static final String LAYOUT = "com.github.ar3s3ru.kubo.views.fragments.replies.layout";

    private static final String TAG    = "RepliesFragment";
    private static final String BOARD  = "com.github.ar3s3ru.kubo.views.fragments.replies.board";
    private static final String NUMBER = "com.github.ar3s3ru.kubo.views.fragments.replies.number";
    private static final String FOLLOW = "com.github.ar3s3ru.kubo.views.fragments.replies.follow";

    private static final IntentFilter mFilter = new IntentFilter(KuboEvents.REPLIES);

    private LocalBroadcastManager mBroadcastManager;
    private RepliesReceiver       mReceiver;
    private ListItemDivider       mItemDivider;
    private RepliesAdapter        mAdapter;
    private RepliesList           mList;

    private boolean update = false, firstRun = true;

    private String  mBoard;
    private int     mThreadNumber;
    private boolean mFollowing;

    @BindView(R.id.fragment_replies_swiper)       SwipeRefreshLayout mSwiper;
    @BindView(R.id.fragment_replies_recyclerview) RecyclerView       mRecyclerView;
    @BindView(R.id.fragment_replies_viewflipper)  ViewFlipper        mViewFlipper;

    @Inject KuboSQLHelper mHelper;

    public RepliesFragment() {
        // Empty constructor...
    }

    /**
     * Return a new instance of RepliesFragment to show posts of the thread located into
     * /{board}/{threadNumber}
     * @param board Board path
     * @param threadNumber Thread number
     * @param follow If thread selected is followed (for options menu)
     * @return New RepliesFragment instance
     */
    public static RepliesFragment newInstance(@NonNull String board,
                                              int threadNumber,
                                              boolean follow) {

        RepliesFragment fragment = new RepliesFragment();
        Bundle args = new Bundle();

        // Set arguments
        args.putString(BOARD, board);
        args.putInt(NUMBER, threadNumber);
        args.putBoolean(FOLLOW, follow);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform dependency injection
        ((KuboApp) getActivity().getApplication()).getAppComponent().inject(this);

        // Set up initial state
        if (firstRun) {
            mBoard        = getArguments().getString(BOARD);
            mThreadNumber = getArguments().getInt(NUMBER, 0);
            mFollowing    = getArguments().getBoolean(FOLLOW, false);
        }

        // For menu inflating
        setHasOptionsMenu(true);

        // Create new receiver
        mReceiver = new RepliesReceiver(this);
        // Retrieve broadcast manager
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        // Create new item divider for the recycler view
        mItemDivider = new ListItemDivider(
                getContext(), R.dimen.listelement_margin,
                R.dimen.generic_items_divider, R.color.dividerColor
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save List if we have one
        if (mList != null) { outState.putParcelable(LIST, Parcels.wrap(mList)); }
        // Save LayoutManager state if mRecycler is shown
        if (mRecyclerView != null) {
            outState.putParcelable(LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replies, container, false);
        ButterKnife.bind(this, view);

        // Set up SwipeLayout listener
        mSwiper.setOnRefreshListener(this);

        // Set up RecyclerView LayoutManager and ItemDivider
        mRecyclerView.addItemDecoration(mItemDivider);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recover state
        if (savedInstanceState != null) {
            mList = Parcels.unwrap(savedInstanceState.getParcelable(LIST));
            mRecyclerView.getLayoutManager()
                    .onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register receiver
        mBroadcastManager.registerReceiver(mReceiver, mFilter);

        // Get new replies if we don't have a list saved, or it's fragment first run,
        // or we received update request from the Activity
        if (mList == null || update || firstRun) {
            if (firstRun) { firstRun = false; }     // Reset
            if (update)   { update   = false; }     // state
            // Always display loading child when creating a new view
            mViewFlipper.setDisplayedChild(0);
            // Start request
            KuboRESTService.getReplies(getContext(), mBoard, mThreadNumber);
        } else {
            // Display RecyclerView already
            mViewFlipper.setDisplayedChild(1);
            // We use mList as the adapter dataset
            setUpRecyclerAdapter(mList);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister receiver
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.replies_fragment_menu, menu);

        final MenuItem item = menu.findItem(R.id.replies_menu_follow);
        if (item != null) {
            item.setIcon(mFollowing ?
                    R.drawable.ic_bookmark_menu_full :
                    R.drawable.ic_bookmark_menu_empty);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.replies_menu_follow) {

            if (mFollowing) {
                item.setIcon(R.drawable.ic_bookmark_menu_empty);
                KuboTableThread.setUnfollowingThread(mHelper, mThreadNumber);
            } else {
                item.setIcon(R.drawable.ic_bookmark_menu_full);
                KuboTableThread.setFollowingThread(mHelper, mList.replies.get(0), mBoard);
            }

            // Notify changes to the activity
            ((KuboStateListener) getActivity()).onChangeFollowingState();
            // Change following state
            mFollowing = !mFollowing;

            return true;
        }

        return false;
    }

    @Override
    public void onRefresh() {
        KuboRESTService.getReplies(getContext(), mBoard, mThreadNumber);
    }

    /**
     * Sets up the recycler adapter with the specified replies list
     * @param replies New replies list
     */
    private void setUpRecyclerAdapter(@NonNull RepliesList replies) {
        // Update mList with the new replies
        mList = replies;
        // Create new adapter instance if we don't have one...
        if (mAdapter == null) {
            mAdapter = new RepliesAdapter(replies, mBoard);
        } else {
            // ...or swap dataset if we have one.
            mAdapter.swapList(replies);
            mAdapter.updateBoard(mBoard);
        }

        // Set adapter
        mRecyclerView.setAdapter(mAdapter);
        // Display recycler view
        if (mViewFlipper.getDisplayedChild() == 0) { mViewFlipper.showNext(); }
        // Stop refreshing
        if (mSwiper.isRefreshing()) { mSwiper.setRefreshing(false); }
    }

    /**
     * Handles an HTTP replies download success by setting up/refreshing the (existing) adapter
     * @param replies New downloaded replies list
     */
    private void handleRepliesSuccess(@NonNull RepliesList replies) {
        // Dismiss notifications
        ((NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE))
                .cancel(mThreadNumber);
        // Update (or set up) the adapter
        setUpRecyclerAdapter(replies);
    }

    /**
     * Handle an HTTP failure response with a new ErrorDialog for user notification
     * @param intent Intent used from the BroadcastReceiver
     */
    private void handleRepliesError(@NonNull Intent intent) {
        ErrorDialog.newInstance(
                intent,
                intent.getStringExtra(KuboEvents.REPLIES_ERROR),
                intent.getIntExtra(KuboEvents.REPLIES_ERRCOD, 0)
        ).show(getActivity().getSupportFragmentManager(), ErrorDialog.TAG);
    }

    /**
     * Update state contents of the RepliesFragment (which is not already destroyed)
     * @param board Board path
     * @param threadNumber Thread number
     */
    public void updateContents(@NonNull String board, int threadNumber, boolean following) {
        // Update new contents
        mBoard        = board;
        mThreadNumber = threadNumber;
        mFollowing    = following;
        update        = true;

        // If the fragment is visible, calls onResume which handles notification cancel
        // and adapter dataset update
        if (isVisible()) { onResume(); }
    }

    /**
     * BroadcastReceiver for getReplies() request.
     */
    static class RepliesReceiver extends BroadcastReceiver {

        private final WeakReference<RepliesFragment> mFragment;

        RepliesReceiver(@NonNull RepliesFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            RepliesFragment fragment = mFragment.get();

            if (fragment != null) {
                // Handle the result only if the thread is still active
                if (intent.getBooleanExtra(KuboEvents.REPLIES_STATUS, false)) {
                    // Success
                    fragment.handleRepliesSuccess(
                            (RepliesList) Parcels.unwrap(
                                    intent.getParcelableExtra(KuboEvents.REPLIES_RESULT)
                            )
                    );
                } else {
                    // Error, handle into the fragment
                    fragment.handleRepliesError(intent);
                }
            }
        }
    }
}
