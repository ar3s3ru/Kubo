package com.github.ar3s3ru.kubo.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.controller.KuboEvents;
import com.github.ar3s3ru.kubo.backend.controller.KuboRESTService;
import com.github.ar3s3ru.kubo.backend.controller.events.BoardsEvent;
import com.github.ar3s3ru.kubo.backend.models.parcelable.ParcelableBoardsList;
import com.github.ar3s3ru.kubo.views.receivers.BoardsReceiver;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

public class MainActivity extends KuboActivity {

    @BindView(R.id.text1)  TextView text;
    @BindView(R.id.button) Button   button;

    private BoardsReceiver mBoardReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Perform ButterKnife binding/injection
        ButterKnife.bind(this);

        mBoardReceiver = new BoardsReceiver(text, button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastManager.registerReceiver(mBoardReceiver, new IntentFilter(KuboEvents.BOARDS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBroadcastManager.unregisterReceiver(mBoardReceiver);
    }

    @OnClick(R.id.button)
    void onClick() {
        KuboRESTService.getBoards(this);
        button.setText("Downloading...");
    }
}
