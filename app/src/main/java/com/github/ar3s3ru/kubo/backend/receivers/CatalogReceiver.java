package com.github.ar3s3ru.kubo.backend.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;
import com.github.ar3s3ru.kubo.views.fragments.ThreadsFragment;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;
import java.util.List;

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

public class CatalogReceiver extends BroadcastReceiver {

    private final WeakReference<ThreadsFragment> mFragment;

    public CatalogReceiver(@NonNull ThreadsFragment threadsFragment) {
        mFragment = new WeakReference<>(threadsFragment);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ThreadsFragment threadsFragment = mFragment.get();

        if (threadsFragment != null) {
            if (intent.getBooleanExtra(KuboEvents.CATALOG_STATUS, false)) {
                // Success
                threadsFragment.handleCatalogSuccess(
                        // Cast should work... :-/
                        (List<ThreadsList>) Parcels.unwrap(intent.getParcelableExtra(KuboEvents.CATALOG_RESULT))
                );
            } else {
                // Shows error within the activity
                threadsFragment.handleCatalogError(
                        intent.getStringExtra(KuboEvents.BOARDS_ERR),
                        intent.getIntExtra(KuboEvents.BOARDS_ERRCOD, 0)
                );
            }
        }
    }
}
