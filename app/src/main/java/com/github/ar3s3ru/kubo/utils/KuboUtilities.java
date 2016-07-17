package com.github.ar3s3ru.kubo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;

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
 * General utility routines that can be used across all the application.
 * As such, SharedPreferences relevant routines are declared here.
 */
public class KuboUtilities {
    /**
     * Converts an integer into a boolean, C-way
     * @param value integer "boolean" value
     * @return Java boolean value
     */
    public static boolean convertFromInt(int value) {
        return value != 0;
    }

    /**
     * Notify the application PushService and other components listening that following threads
     * has been updated, therefore all the actual queries needs to be re-done
     * @param context Application context to send local broadcast message
     */
    // TODO: move it
    public static void notifyFollowingThreadsChanged(@NonNull Context context) {
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(KuboEvents.FOLLOWING_UPDATE));
    }

    /**
     * Specify if the boards have to be downloaded (ex. first usage)
     * @param sharedPrefs Default shared preferences
     * @return true if must download boards, false otherwise
     */
    public static boolean hasToDownloadBoards(@NonNull SharedPreferences sharedPrefs) {
        return sharedPrefs.getBoolean(KuboSharedPrefsKeys.KEY_DOWNLOAD_BOARDS, true);
    }

    /**
     * Disable startup boards download
     * @param sharedPrefs Default shared preferences
     */
    public static void disableStartupBoards(@NonNull SharedPreferences sharedPrefs) {
        final SharedPreferences.Editor mEditor = sharedPrefs.edit();

        mEditor.putBoolean(KuboSharedPrefsKeys.KEY_DOWNLOAD_BOARDS, false);
        mEditor.apply();
    }
}
