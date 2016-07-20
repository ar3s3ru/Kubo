package com.github.ar3s3ru.kubo.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
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
    // Board path id
    private static final String PATH = "com.github.ar3s3ru.kubo.views.ContentsActivity.board_path";
    // Board title
    private static final String TITLE = "com.github.ar3s3ru.kubo.views.ContentsActivity.board_title";
    // Thread number
    private static final String NUMBER = "com.github.ar3s3ru.kubo.views.ContentsActivity.thread_number";

    /** Fragment tags */
    private static final String THREADS_TAG = "ThreadsFragment";
    private static final String REPLIES_TAG = "RepliesFragment";

    /** Members variables */
    @BindView(R.id.activity_contents_toolbar) Toolbar mToolbar;

    private Drawer mDrawer;

    private String mBoardPath, mBoardTitle;
    private int mThreadNumber;

    /** MaterialDrawer elements */
    // TODO: add here

    // TODO: Javadoc
    public static Intent newContentsActivityIntent(@NonNull Context context,
                                                   @NonNull String path,
                                                   String title, int threadNumber) {
        return new Intent(context, ContentsActivity.class)
                .putExtra(PATH, path)
                .putExtra(TITLE, title)
                .putExtra(NUMBER, threadNumber);
    }

    /**
     * Starts ContentsActivity with provided path, from defined context
     * @param context Starting context
     * @param title Board title
     * @param path Board path
     */
    public static void startContentsActivity(@NonNull Context context,
                                             @NonNull String title,
                                             @NonNull String path) {
        // Default threadNumber value (-1)
        context.startActivity(newContentsActivityIntent(context, path, title, -1));
    }

    /**
     * Starts ContentsActivity with provided path, from defined context, to show a certain thread
     * @param context Starting context
     * @param title Board title
     * @param path Board path
     * @param threadNumber Thread number
     */
    public static void startContentsActivityWithThread(@NonNull Context context,
                                                       @NonNull String title,
                                                       @NonNull String path,
                                                       int threadNumber) {
        // With threadNumber value
        context.startActivity(newContentsActivityIntent(context, path, title, threadNumber));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_contents);
        ButterKnife.bind(this);

        // Set up MaterialDrawer
        setUpNavigationDrawer();

        // Set up state
        setUpContents(getIntent());

        // Construct fragments
        setUpFragments();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");

        // Set up contents again
        setUpContents(intent);

        final RepliesFragment fragment = (RepliesFragment)
                getSupportFragmentManager().findFragmentByTag(REPLIES_TAG);

        if (fragment != null) {
            updateRepliesFragment(fragment);
        } else {
            setUpRepliesFragment(mBoardPath, mThreadNumber, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        // Set up ActionBar
        if (mBoardTitle != null) { mToolbar.setTitle(mBoardTitle); }
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        final int fragmentsCount = getSupportFragmentManager().getBackStackEntryCount();
        if (fragmentsCount <= 1) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onThreadClick(@NonNull String board, int threadNumber, boolean followed) {
        Log.e(TAG, "onThreadClick");
        // TODO: change fragment or send repliesFragment an intent
        mThreadNumber = threadNumber;
        mBoardPath    = board;

        setUpRepliesFragment(board, threadNumber, false);
    }

    @Override
    public void onChangeFollowingState() {
        // TODO: change NavigationDrawer contents
        // Notifying following threads received
        KuboTableThread.notifyFollowingThreadsChanged(this);
    }

    // TODO: Javadoc
    private void setUpNavigationDrawer() {
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withSliderBackgroundColor(ContextCompat.getColor(this, R.color.colorVeryDark))
                .build();
    }

    // TODO: Javadoc
    private void setUpContents(@NonNull Intent intent) {
        Log.e(TAG, "setUpContents");
        mBoardPath    = intent.getStringExtra(PATH);
        mBoardTitle   = intent.getStringExtra(TITLE);
        mThreadNumber = intent.getIntExtra(NUMBER, -1);
    }

    private void setUpFragments() throws RuntimeException {
        Log.e(TAG, "setUpFragments");
        // Checking required parameters
        if (mBoardPath == null) {
            throw new RuntimeException("Invalid intent used for ContentsActivity, " +
                    "must call static methods startContentsActivity() " +
                    "or startContentsActivityFromNotification()");
        }

        final RepliesFragment repliesFragment =
                (RepliesFragment) getSupportFragmentManager().findFragmentByTag(REPLIES_TAG);

        final ThreadsFragment threadsFragment =
                (ThreadsFragment) getSupportFragmentManager().findFragmentByTag(THREADS_TAG);

        if (mThreadNumber != -1 && repliesFragment == null) {
            // Create new RepliesFragment
            setUpRepliesFragment(mBoardPath, mThreadNumber, threadsFragment == null);
        } else if (threadsFragment == null) {
            // Create new ThreadsFragment
            setUpThreadsFragment();
        }
    }

    private void setUpThreadsFragment() {
        final ThreadsFragment threadsFragment = ThreadsFragment.newInstance(mBoardPath);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_contents_fragment_container, threadsFragment, THREADS_TAG)
                .commitNow();
    }

    private void setUpRepliesFragment(@NonNull String board, int threadNumber, boolean adding) {
        Log.e(TAG, "setUpRepliesFragment");
        final RepliesFragment repliesFragment =
                RepliesFragment.newInstance(board, threadNumber);

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (adding) {
            ft.add(R.id.activity_contents_fragment_container, repliesFragment, REPLIES_TAG);
        } else {
            ft.replace(R.id.activity_contents_fragment_container, repliesFragment, REPLIES_TAG);
        }

        ft.addToBackStack(null).commit();
    }

    private void updateRepliesFragment(@NonNull RepliesFragment fragment) {
        Log.e(TAG, "updateRepliesFragment");
        fragment.updateContents(mBoardPath, mThreadNumber);
    }
}
