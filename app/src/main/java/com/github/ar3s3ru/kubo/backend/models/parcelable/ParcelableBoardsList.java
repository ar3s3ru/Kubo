package com.github.ar3s3ru.kubo.backend.models.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.ar3s3ru.kubo.backend.models.Board;
import com.github.ar3s3ru.kubo.backend.models.BoardsList;

import java.util.ArrayList;
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

public class ParcelableBoardsList implements Parcelable {

    private List<ParcelableBoard> boards = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.boards);
    }

    public ParcelableBoardsList(BoardsList list) {
        for (Board b : list.getBoards()) {
            boards.add(new ParcelableBoard(b));
        }
    }

    protected ParcelableBoardsList(Parcel in) {
        this.boards = in.createTypedArrayList(ParcelableBoard.CREATOR);
    }

    public static final Parcelable.Creator<ParcelableBoardsList> CREATOR =
            new Parcelable.Creator<ParcelableBoardsList>() {
                @Override
                public ParcelableBoardsList createFromParcel(Parcel source) {
                    return new ParcelableBoardsList(source);
                }

                @Override
                public ParcelableBoardsList[] newArray(int size) {
                    return new ParcelableBoardsList[size];
                }
            };

    /**
     * Getter
     */

    public List<ParcelableBoard> getBoards() {
        return boards;
    }
}
