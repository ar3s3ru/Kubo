package com.github.ar3s3ru.kubo.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
import com.github.ar3s3ru.kubo.utils.KuboStateListener;
import com.github.ar3s3ru.kubo.views.fragments.RepliesFragment;
import com.github.ar3s3ru.kubo.views.fragments.ThreadsFragment;

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

public class ContentsActivity extends KuboActivity
        implements ThreadsFragment.Listener, KuboStateListener {

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

    @Inject KuboSQLHelper mHelper;

    private String mBoardPath, mBoardTitle;
    private int mThreadNumber;

    /** MaterialDrawer elements */
    // TODO: add here

    /**
     * Create a new Intent to start ContentsActivity
     * @param context UI context
     * @param path Board path to use
     * @param title Board title (if we have one)
     * @param threadNumber Thread number (if we need it, otherwise use -1)
     * @return New ContentsActivity Intent
     */
    public static Intent newContentsActivityIntent(@NonNull  Context context,
                                                   @NonNull  String path,
                                                   @Nullable String title,
                                                   int threadNumber) {
        // New intent
        return new Intent(context, ContentsActivity.class)
                // If an action is not set, when dealing with different PendingIntents for the same
                // activity, the intent drops its extras
                .setAction(Long.toString(System.currentTimeMillis()))
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
    static void startContentsActivity(@NonNull Context context,
                                             @NonNull String title,
                                             @NonNull String path) {
        // Default threadNumber value (-1)
        context.startActivity(newContentsActivityIntent(context, path, title, -1));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        // Bind views
        ButterKnife.bind(this);

        // Performs dependency injection
        ((KuboApp) getApplication()).getAppComponent().inject(this);

        // Set up state
        setUpContents(getIntent());

        // Construct fragments
        setUpFragments();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Set up contents again
        setUpContents(intent);

        // Retrieve the RepliesFragment instance (if we have one already)
        final RepliesFragment fragment = (RepliesFragment)
                getSupportFragmentManager().findFragmentByTag(REPLIES_TAG);

        if (fragment != null) {
            // We already have a RepliesFragment instance, update it
            updateRepliesFragment(fragment, true);
        } else {
            // We don't have a RepliesFragment instance, create a new one
            setUpRepliesFragment(mBoardPath, mThreadNumber, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up ActionBar
        if (mBoardTitle != null) {
            mToolbar.setTitle(
                    // From notification, mBoardTitle == mBoardPath
                    !mBoardTitle.equals(mBoardPath) ?
                            mBoardTitle :
                            KuboTableBoard.getTitleFromPath(mHelper, mBoardPath)
            );
        }

        setSupportActionBar(mToolbar);
    }

    @Override
    public void onBackPressed() {
        // Get fragments count into the backstack
        final int fragmentsCount = getSupportFragmentManager().getBackStackEntryCount();

        if (fragmentsCount <= 1) {
            // We have just one fragment, so exit
            super.onBackPressed();
        } else {
            // We have more than one fragment, so pop from the backstack
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onThreadClick(@NonNull String board, int threadNumber, boolean followed) {
        // TODO: change fragment or send repliesFragment an intent
        mThreadNumber = threadNumber;
        mBoardPath    = board;

        setUpRepliesFragment(board, threadNumber, followed);
    }

    @Override
    public void onChangeFollowingState() {
        // Notifying following threads received
        KuboTableThread.notifyFollowingThreadsChanged(this);
    }

    /**
     * Set up local state from the intent
     * @param intent Intent from which retrieve the status
     */
    private void setUpContents(@NonNull Intent intent) {
        mBoardPath    = intent.getStringExtra(PATH);
        mBoardTitle   = intent.getStringExtra(TITLE);
        mThreadNumber = intent.getIntExtra(NUMBER, -1);
    }

    /**
     * Set up the fragments according to the object state
     * @throws RuntimeException if board path is not set
     */
    private void setUpFragments() throws RuntimeException {
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
            // Create new RepliesFragment (executed from notification intent, so is followed)
            setUpRepliesFragment(mBoardPath, mThreadNumber, true);
        } else if (threadsFragment == null) {
            // Create new ThreadsFragment
            setUpThreadsFragment();
        }
    }

    /**
     * Sets up a new ThreadsFragment instance
     */
    private void setUpThreadsFragment() {
        final ThreadsFragment threadsFragment = ThreadsFragment.newInstance(mBoardPath);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_contents_fragment_container, threadsFragment, THREADS_TAG)
                .commitNow();
    }

    /**
     * Sets up a new RepliesFragment instance
     * @param board Board path
     * @param threadNumber Thread number
     * @param following Following state of the thread selected
     */
    private void setUpRepliesFragment(@NonNull String board,
                                      int threadNumber,
                                      boolean following) {
        final RepliesFragment repliesFragment =
                RepliesFragment.newInstance(board, threadNumber, following);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_contents_fragment_container, repliesFragment, REPLIES_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Updates RepliesFragment instance state
     * @param fragment RepliesFragment instance
     * @param followed true if the new thread is followed, false otherwise
     */
    private void updateRepliesFragment(@NonNull RepliesFragment fragment, boolean followed) {
        fragment.updateContents(mBoardPath, mThreadNumber, followed);
    }
}
