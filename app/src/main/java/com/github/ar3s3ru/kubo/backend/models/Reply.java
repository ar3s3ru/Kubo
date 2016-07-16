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

    @SerializedName("tim")
    public long properFilename;

    @SerializedName("time")
    public long UNIXtime;

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
                ", properFilename=" + properFilename +
                ", UNIXtime=" + UNIXtime +
                ", replyTo=" + replyTo +
                '}';
    }
}
