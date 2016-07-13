package com.github.ar3s3ru.kubo.modules;

import android.support.annotation.NonNull;

import com.github.ar3s3ru.kubo.backend.controller.KuboAPInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
public class KuboNetModule {
    // 4chan REST API endpoint
    private String baseUrl;

    public KuboNetModule(@NonNull String url) {
        baseUrl = url;
    }

    /**
     * Provides a Gson instance for Retrofit builder creation
     * @param factory TypeAdapter factory to use for Parcelable reflection
     * @return Gson instance for Retrofit builder usage
     */
    @Provides
    static Gson provideGson(AutoParcelGsonTypeAdapterFactory factory) {
        return new GsonBuilder().registerTypeAdapterFactory(factory).create();
    }

    /**
     * Provide a Retrofit builder instance for REST API access.
     * @param gson Gson instance
     * @return Retrofit builder instance
     */
    @Provides
    @Singleton
    Retrofit provideAppRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Provide a 4chan REST API instance to use globally within some Service
     * @param retrofit Retrofit builder instance
     * @return 4chan REST API instance
     */
    @Provides
    @Singleton
    static KuboAPInterface provideApiInterface(Retrofit retrofit) {
        return retrofit.create(KuboAPInterface.class);
    }
}
