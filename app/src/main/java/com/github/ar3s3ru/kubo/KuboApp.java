package com.github.ar3s3ru.kubo;

import android.app.Application;
import android.content.Intent;

import com.github.ar3s3ru.kubo.backend.controller.KuboPushService;
import com.github.ar3s3ru.kubo.components.DaggerKuboAppComponent;
import com.github.ar3s3ru.kubo.components.DaggerKuboNetComponent;
import com.github.ar3s3ru.kubo.components.KuboAppComponent;
import com.github.ar3s3ru.kubo.components.KuboNetComponent;
import com.github.ar3s3ru.kubo.modules.KuboAppModule;
import com.github.ar3s3ru.kubo.modules.KuboDBModule;
import com.github.ar3s3ru.kubo.modules.KuboNetModule;

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
 * Application class for Kubo.
 */
public class KuboApp extends Application {

    /** Dagger Components */
    private KuboNetComponent mNetComponent;
    private KuboAppComponent mAppComponent;

    /** Dagger Modules */
    private final KuboNetModule mNetModule = new KuboNetModule("https://api.4chan.org");
    private final KuboAppModule mAppModule = new KuboAppModule(this);
    private final KuboDBModule  mDBModule  = new KuboDBModule();

    @SuppressWarnings("deprecation")    // Dagger sometimes flags as deprecated some
    @Override                           // modules...
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerKuboNetComponent.builder()
                .kuboNetModule(mNetModule)
                .kuboAppModule(mAppModule)
                .kuboDBModule(mDBModule)
                .build();

        mAppComponent = DaggerKuboAppComponent.builder()
                .kuboAppModule(mAppModule)
                .kuboDBModule(mDBModule)
                .build();

        // Start PushService
        startService(new Intent(this, KuboPushService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Stop PushService
        stopService(new Intent(this, KuboPushService.class));
    }

    /**
     * Getter for Dagger Net Component for IntentService injection
     * @return Dagger NetComponent instance
     */
    public KuboNetComponent getNetComponent() {
        return mNetComponent;
    }

    /**
     * Getter for Dagger App Component for UI components injection
     * @return Dagger AppComponent instance
     */
    public KuboAppComponent getAppComponent() {
        return mAppComponent;
    }
}
