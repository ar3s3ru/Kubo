package com.github.ar3s3ru.kubo.utils;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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
        SharedPreferences.Editor mEditor = sharedPrefs.edit();

        mEditor.putBoolean(KuboSharedPrefsKeys.KEY_DOWNLOAD_BOARDS, false);
        mEditor.apply();
    }
}
