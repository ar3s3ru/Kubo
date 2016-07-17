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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.backend.models.RepliesList;
import com.github.ar3s3ru.kubo.views.custom.ListItemDivider;
import com.github.ar3s3ru.kubo.views.recyclers.RepliesAdapter;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;

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

public class RepliesFragment extends Fragment {

    private static final String TAG   = "RepliesFragment";
    private static final String BOARD = "com.github.ar3s3ru.kubo.views.fragments.replies.board";
    private static final String THNUM = "com.github.ar3s3ru.kubo.views.fragments.replies.number";

    private static final IntentFilter mFilter = new IntentFilter(KuboEvents.REPLIES);

    private LocalBroadcastManager mBroadcastManager;
    private RepliesReceiver       mReceiver;
    private ListItemDivider       mItemDivider;
    private RepliesAdapter        mAdapter;

    private String mBoard;
    private int    mThreadNumber;

    @BindView(R.id.fragment_replies_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.fragment_replies_viewflipper)  ViewFlipper  mViewFlipper;

    public RepliesFragment() {
        // Empty constructor...
    }

    public static RepliesFragment newInstance(@NonNull String board, int threadNumber) {
        RepliesFragment fragment = new RepliesFragment();
        Bundle args = new Bundle();

        args.putString(BOARD, board);
        args.putInt(THNUM, threadNumber);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform dependency injection
        ((KuboApp) getActivity().getApplication()).getAppComponent().inject(this);

        // Get required arguments
        mBoard        = getArguments().getString(BOARD);
        mThreadNumber = getArguments().getInt(THNUM, 0);

        // Create new receiver
        mReceiver = new RepliesReceiver(this);
        // Retrieve broadcast manager
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        // Create new item divider for the recycler view
        mItemDivider = new ListItemDivider(
                getContext(), R.dimen.listelement_margin,
                R.dimen.generic_items_divider, R.color.dividerColor, true   // Ignore first element
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replies, container, false);
        ButterKnife.bind(this, view);

        // Always display loading child when creating a new view
        mViewFlipper.setDisplayedChild(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register receiver
        mBroadcastManager.registerReceiver(mReceiver, mFilter);
        // Start request
        KuboRESTService.getReplies(getContext(), mBoard, mThreadNumber);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister receiver
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    private void setUpRecyclerView(@NonNull RepliesList replies) {

        Log.e(TAG, replies.toString());

        if (mAdapter == null) {
            mAdapter = new RepliesAdapter(replies);
        } else {
            mAdapter.swapList(replies);
        }

        mRecyclerView.addItemDecoration(mItemDivider);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        // Display recycler view
        mViewFlipper.showNext();
    }

    private void handleRepliesSuccess(@NonNull RepliesList replies) {
        setUpRecyclerView(replies);
    }

    private void handleRepliesError(@NonNull String error, int errorCode) {
        Log.e(TAG, "Error (" + errorCode + "): " + error);
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
                    fragment.handleRepliesError(
                            intent.getStringExtra(KuboEvents.REPLIES_ERROR),
                            intent.getIntExtra(KuboEvents.REPLIES_ERRCOD, 0)
                    );
                }
            }
        }
    }
}
