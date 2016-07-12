package com.github.ar3s3ru.kubo.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

@Module
public class KuboAppModule {
    // The AppModule needs application context to access
    // application-level resources (such as DB, shared preferences, ...)
    private Context appContext;

    public KuboAppModule(@NonNull Context appContext) {
        this.appContext = appContext;
    }

    /**
     * Provides the application context to the other modules
     * @return Application context
     */
    @Provides
    Context provideAppContext() {
        return appContext;
    }

    /**
     * Provides the default shared preferences
     * @return Default application shared preferences
     */
    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    /**
     * Provides a Toast instance with application context to show
     * errors within Activities and Fragments and so on...
     * @return Toast instance for errors notifications
     */
    @Provides
    Toast provideNewErrorToast() {
        return Toast.makeText(appContext, "", Toast.LENGTH_LONG);
    }
}
