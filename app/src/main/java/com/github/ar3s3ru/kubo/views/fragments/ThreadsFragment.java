package com.github.ar3s3ru.kubo.views.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;
import com.github.ar3s3ru.kubo.backend.receivers.CatalogReceiver;
import com.github.ar3s3ru.kubo.views.ContentsActivity;
import com.github.ar3s3ru.kubo.views.recyclers.CatalogRecycler;

import java.util.ArrayList;
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

public class ThreadsFragment extends Fragment {

    private static final String TAG = "ThreadsFragment";

    /** Members variables */
    @BindView(R.id.fragment_threads_viewflipper)  ViewFlipper  mViewFlipper;
    @BindView(R.id.fragment_threads_recyclerview) RecyclerView mRecycler;

    @Inject Toast mToast;

    private String  mBoardTitle;
    private String  mBoardPath;
    private int     mBoardPrimaryKey;
    private boolean mLoaded = false;

    private CatalogRecycler       mAdapter;
    private CatalogReceiver       mReceiver;
    private LocalBroadcastManager mBroadcastManager;

    public ThreadsFragment() {
        // Empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject everything from Dagger
        ((KuboApp) getActivity().getApplication()).getAppComponent().inject(this);

        // Create new CatalogReceiver to handle getCatalog(path)
        mReceiver = new CatalogReceiver(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up ActionBar
        setUpActionBar(((ContentsActivity) getActivity()).getSupportActionBar());

        if (!mLoaded) {
            // Send getCatalog() request
            startRequestingCatalog(false);
        } else if (mAdapter != null) {
            // Sets up the RecyclerView (adapter is already created)
            setUpRecyclerView();
        }

        // Register Catalog receiver
        mBroadcastManager.registerReceiver(
                mReceiver,
                new IntentFilter(KuboEvents.CATALOG)
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister Catalog receiver
        mBroadcastManager.unregisterReceiver(mReceiver);
    }

    /**
     * Sets up ActionBar title
     * @param actionBar ActionBar support v7
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

    private void setUpRecyclerView() {
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // TODO: Javadoc
    public void setUpContents(@NonNull String title, @NonNull String path, int primaryKey) {
        mBoardTitle      = title;
        mBoardPath       = path;
        mBoardPrimaryKey = primaryKey;
    }

    // TODO: Javadoc
    public void updateContents(String title, String path, int primaryKey) {
        setUpContents(title, path, primaryKey);
        startRequestingCatalog(true);
    }

    /**
     * Handle successful HTTP call of getCatalog(path)
     * @param catalog Catalog result
     */
    public void handleCatalogSuccess(List<ThreadsList> catalog) {
        mLoaded  = true;             // Loaded
        mAdapter = new CatalogRecycler(catalog);

        setUpRecyclerView();
        mViewFlipper.showNext();    // Shows recycler
    }

    /**
     * Handle error on getCatalog(path) HTTP call
     * @param error Error string encoded
     * @param errorCode Error HTTP code
     */
    public void handleCatalogError(String error, int errorCode) {
        mToast.setText(errorCode + ": " + error);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }
}
