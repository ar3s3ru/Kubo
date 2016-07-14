package com.github.ar3s3ru.kubo.backend.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

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

@Parcel
public class Reply {
    @SerializedName("no")
    public int number;

    @SerializedName("now")
    public String timeNow;

    @SerializedName("name")
    public String name;

    @SerializedName("com")
    public String comment;

    @SerializedName("filename")
    public String originalFilename;

    @SerializedName("ext")
    public String fileExtension;

    @SerializedName("w")
    public int width;

    @SerializedName("h")
    public int height;

    @SerializedName("tn_w")
    public int thumbWidth;

    @SerializedName("tn_h")
    public int thumbHeight;

    @SerializedName("tim")
    public long properFilename;

    @SerializedName("time")
    public long UNIXtime;

    @SerializedName("md5")
    public String MD5hash;

    @SerializedName("fsize")
    public int fileSize;

    @SerializedName("resto")
    public int replyTo;

    @Override
    public String toString() {
        return "Reply{" +
                "number=" + number +
                ", timeNow='" + timeNow + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", thumbWidth=" + thumbWidth +
                ", thumbHeight=" + thumbHeight +
                ", properFilename=" + properFilename +
                ", UNIXtime=" + UNIXtime +
                ", MD5hash='" + MD5hash + '\'' +
                ", fileSize=" + fileSize +
                ", replyTo=" + replyTo +
                '}';
    }
}
