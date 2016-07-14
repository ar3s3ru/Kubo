package com.github.ar3s3ru.kubo.backend.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

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
public class Thread extends Reply {
    @SerializedName("bumplimit")
    public int bumplimit;

    @SerializedName("imagelimit")
    public int imagelimit;

    @SerializedName("semantic_url")
    public String semanticUrl;

    @SerializedName("replies")
    public int replies;

    @SerializedName("images")
    public int images;

    @SerializedName("omitted_posts")
    public int omittedPosts;

    @SerializedName("omitted_images")
    public int omittedImages;

    @SerializedName("last_replies")
    public List<Reply> lastReplies;

    @Override
    public String toString() {
        return "Thread{" +
                "bumplimit=" + bumplimit +
                ", imagelimit=" + imagelimit +
                ", semanticUrl='" + semanticUrl + '\'' +
                ", replies=" + replies +
                ", images=" + images +
                ", omittedPosts=" + omittedPosts +
                ", omittedImages=" + omittedImages +
                ", lastReplies=" + lastReplies +
                "} " + super.toString();
    }
}
