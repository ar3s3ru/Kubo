package com.github.ar3s3ru.kubo.views.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import com.github.ar3s3ru.kubo.views.recyclers.CatalogRecycler;

import org.parceler.Parcels;

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

public class ThreadsFragment extends Fragment implements CatalogRecycler.Listener {

    private static final String TAG  = "ThreadsFragment";
    private static final String LIST = "com.github.ar3s3ru.kubo.views.fragments.threadsfragment.list";

    /** Members variables */
    @BindView(R.id.fragment_threads_viewflipper)  ViewFlipper  mViewFlipper;
    @BindView(R.id.fragment_threads_recyclerview) RecyclerView mRecycler;

    @Inject Toast mToast;

    private String  mBoardTitle;
    private String  mBoardPath;
    private int     mBoardPrimaryKey;

    private List<ThreadsList> mList;

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

        // Recover state
        mList = savedInstanceState == null ? null :
                (List<ThreadsList>) Parcels.unwrap(savedInstanceState.getParcelable(LIST));

        // Create new CatalogReceiver to handle getCatalog(path)
        mReceiver = new CatalogReceiver(this);
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save ThreadsList to avoid downloading
        outState.putParcelable(LIST, Parcels.wrap(mList));
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
        setUpActionBar(((AppCompatActivity) getActivity()).getSupportActionBar());

        if (mList == null) {
            // Send getCatalog() request
            startRequestingCatalog(false);
        } else {
            // Sets up the RecyclerView
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

    @Override
    public void onChangedPage(int pageNumber) {
        mToast.setText("Page " + pageNumber);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
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
        // Set up adapter
        mAdapter = new CatalogRecycler(this, mList, mBoardPath);
        // Set up Recycler
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // Show recycler
        mViewFlipper.showNext();
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
        mList = catalog;
        setUpRecyclerView();
    }

    /**
     * Handle error on getCatalog(path) HTTP call
     * @param error Error string encoded
     * @param errorCode Error HTTP code
     */
    public void handleCatalogError(String error, int errorCode) {
        mToast.setText(errorCode + ": " + error);
        mToast.setGravity(Gravity.BOTTOM, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }
}
