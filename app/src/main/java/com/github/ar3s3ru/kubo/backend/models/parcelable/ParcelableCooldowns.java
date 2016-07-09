package com.github.ar3s3ru.kubo.backend.models.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.ar3s3ru.kubo.backend.models.Cooldowns;

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

public class ParcelableCooldowns implements Parcelable {

    private int threads;
    private int replies;
    private int images;
    private int repliesIntra;
    private int imagesIntra;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.threads);
        dest.writeInt(this.replies);
        dest.writeInt(this.images);
        dest.writeInt(this.repliesIntra);
        dest.writeInt(this.imagesIntra);
    }

    public ParcelableCooldowns(Cooldowns cooldowns) {
        threads      = cooldowns.getThreads();
        replies      = cooldowns.getReplies();
        images       = cooldowns.getImages();
        repliesIntra = cooldowns.getRepliesIntra();
        imagesIntra  = cooldowns.getImagesIntra();
    }

    protected ParcelableCooldowns(Parcel in) {
        this.threads      = in.readInt();
        this.replies      = in.readInt();
        this.images       = in.readInt();
        this.repliesIntra = in.readInt();
        this.imagesIntra  = in.readInt();
    }

    public static final Parcelable.Creator<ParcelableCooldowns> CREATOR =
            new Parcelable.Creator<ParcelableCooldowns>() {
                @Override
                public ParcelableCooldowns createFromParcel(Parcel source) {
                    return new ParcelableCooldowns(source);
                }

                @Override
                public ParcelableCooldowns[] newArray(int size) {
                    return new ParcelableCooldowns[size];
                }
            };

    /**
     * Getters
     */

    public int getThreads() {
        return threads;
    }

    public int getReplies() {
        return replies;
    }

    public int getImages() {
        return images;
    }

    public int getRepliesIntra() {
        return repliesIntra;
    }

    public int getImagesIntra() {
        return imagesIntra;
    }
}
