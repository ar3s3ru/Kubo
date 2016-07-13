package com.github.ar3s3ru.kubo.backend.models.autoparcel;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
abstract class Reply implements Parcelable {
    @SerializedName("no")
    abstract int number();

    @SerializedName("now")
    abstract String timeNow();

    @SerializedName("name")
    abstract String name();

    @SerializedName("com")
    abstract String comment();

    @SerializedName("filename")
    abstract String originalFilename();

    @SerializedName("ext")
    abstract String fileExtension();

    @SerializedName("w")
    abstract int width();

    @SerializedName("h")
    abstract int height();

    @SerializedName("tn_w")
    abstract int thumbWidth();

    @SerializedName("tn_h")
    abstract int thumbHeight();

    @SerializedName("tim")
    abstract long properFilename();

    @SerializedName("time")
    abstract int UNIXtime();

    @SerializedName("md5")
    abstract String MD5hash();

    @SerializedName("fsize")
    abstract int fileSize();

    @SerializedName("resto")
    abstract int replyTo();

    static Reply creator(int number, String timeNow, String name, String comment,
                         String originalFilename, String fileExtension,
                         int width, int height, int thumbWidth, int thumbHeight, long properFilename,
                         int UNIXtime, String MD5hash, int fileSize, int replyTo) {
        // Parcelable creator
        return new AutoParcelGson_Reply(number, timeNow, name, comment, originalFilename,
                fileExtension, width, height, thumbWidth, thumbHeight, properFilename, UNIXtime,
                MD5hash, fileSize, replyTo);
    }
}
