package com.github.ar3s3ru.kubo.views.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.models.parcelable.ParcelableBoardsList;

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

public class BoardsReceiver extends BroadcastReceiver {

    private WeakReference<TextView> mTextView;
    private WeakReference<Button>   mButton;

    public BoardsReceiver(@NonNull TextView text, @NonNull Button button) {
        mTextView = new WeakReference<>(text);
        mButton   = new WeakReference<>(button);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TextView text   = mTextView.get();
        Button   button = mButton.get();
        // Handle receive only if text/button still exists in the heap
        if (text != null && button != null) {
            // Status OK
            if (intent.getBooleanExtra(KuboEvents.BOARDS_STATUS, false)) {
                ParcelableBoardsList list = intent.getParcelableExtra(KuboEvents.BOARDS_ARG0);
                text.setText("Received " + list.getBoards().size() + " boards...");

                button.setText("Downloaded");
                button.setClickable(false);
            } else {
                // Status not OK
                String error = intent.getStringExtra(KuboEvents.BOARDS_ERR0);
                int errcod = intent.getIntExtra(KuboEvents.BOARDS_ERRCOD, 0);

                text.setText("Error (" + errcod + "): " + error);
                button.setText("Download");
            }
        }
    }
}
