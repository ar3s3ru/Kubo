package com.github.ar3s3ru.kubo.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.github.ar3s3ru.kubo.KuboApp;
import com.squareup.otto.Bus;

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

public class KuboActivity extends AppCompatActivity {

    @Inject protected Bus activityBus;
    protected LocalBroadcastManager mBroadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform injection to have all dependencies ready for use
        ((KuboApp) getApplication()).getAppComponent().inject(this);

        // Perform EventBus registration
        activityBus.register(this);

        // Get LocalBroadcastManager instance
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister EventBus
        activityBus.unregister(this);
    }
}
