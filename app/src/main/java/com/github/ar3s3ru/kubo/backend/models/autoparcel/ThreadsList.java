package com.github.ar3s3ru.kubo.backend.models.autoparcel;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import auto.parcelgson.AutoParcelGson;

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

@AutoParcelGson
public abstract class ThreadsList implements Parcelable {
    @SerializedName("page")
    abstract int page();

    @SerializedName("threads")
    abstract List<Thread> threads();

    static ThreadsList creator(int page, List<Thread> threads) {
        return new AutoParcelGson_ThreadsList(page, threads);
    }
}
