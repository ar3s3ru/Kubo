package com.github.ar3s3ru.kubo.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.utils.KuboUtilities;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

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

public class StartActivity extends KuboActivity {

    @Inject Toast mToast;
    @Inject SharedPreferences mSharedPrefs;

    private BoardsReceiver mBoardReceiver;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Perform injection to have all dependencies ready for use
        ((KuboApp) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (KuboUtilities.hasToDownloadBoards(mSharedPrefs)) {
            // Send getBoards() request
            manageDownloadingBranch();
        } else {
            // Wait 1s and send intent for BoardsActivity
            manageWaitingBranch();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isRegistered) {
            // Need to unregister BroadcastReceiver
            mBroadcastManager.unregisterReceiver(mBoardReceiver);
            isRegistered = false;
        }
    }

    /**
     * Register BroadcastReceiver and send a getBoards() request to the application
     * IntentService.
     */
    private void manageDownloadingBranch() {
        // Register receiver
        mBoardReceiver = new BoardsReceiver(this);
        mBroadcastManager.registerReceiver(mBoardReceiver, new IntentFilter(KuboEvents.BOARDS));
        isRegistered = true;

        // Request boards
        KuboRESTService.getBoards(this);
    }

    /**
     * Start a CountDownTimer lasting 1 second and send an intent
     * for BoardsActivity when finished.
     */
    private void manageWaitingBranch() {
        // TODO: check startups boards
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                // Self explainatory...
                goToBoardsActivity();
            }
        }.start();
    }

    /**
     * Sends an intent to BoardsActivity.
     */
    private void goToBoardsActivity() {
        startActivity(
            new Intent(this, BoardsActivity.class)
        );
    }

    /**
     * Disables boards download on startup (updating the shared preferences)
     * and send intent to BoardsActivity.
     */
    public void handleSuccessfullyDownload() {
        // Remind that we don't need to download on startup anymore
        KuboUtilities.disableStartupBoards(mSharedPrefs);
        goToBoardsActivity();
    }

    /**
     * Handles errors notifications through Toast instance
     * @param error Error description
     * @param errorCode Error HTTP/application code
     */
    public void handleErrorDownload(String error, int errorCode) {
        mToast.setText(errorCode + ": " + error);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * BroadcastReceiver for getBoards() request.
     */
    static class BoardsReceiver extends BroadcastReceiver {

        // Maintains a WeakReference to the MainActivity
        // to prevent blocking garbage collection
        private final WeakReference<StartActivity> mActivity;

        BoardsReceiver(@NonNull StartActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Gets MainActivity strong reference
            StartActivity activity = mActivity.get();

            // Handle receive only if the activity still exists
            if (activity != null) {
                // Status OK
                if (intent.getBooleanExtra(KuboEvents.BOARDS_STATUS, false)) {
                    // Notify download success
                    activity.handleSuccessfullyDownload();
                } else {
                    // Shows error within the activity
                    activity.handleErrorDownload(
                            intent.getStringExtra(KuboEvents.BOARDS_ERR),
                            intent.getIntExtra(KuboEvents.BOARDS_ERRCOD, 0)
                    );
                }
            }
        }
    }
}
