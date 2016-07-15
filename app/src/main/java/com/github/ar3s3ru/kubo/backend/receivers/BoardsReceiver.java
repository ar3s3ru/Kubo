package com.github.ar3s3ru.kubo.backend.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.views.StartActivity;

import java.lang.ref.WeakReference;

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

/**
 * Local BroadcastReceiver for the getBoards() request.
 * Generally, this is done within MainActivity (hence the constructor param).
 */
public class BoardsReceiver extends BroadcastReceiver {

    // Maintains a WeakReference to the MainActivity
    // to prevent blocking garbage collection
    private final WeakReference<StartActivity> mActivity;

    public BoardsReceiver(@NonNull StartActivity activity) {
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
