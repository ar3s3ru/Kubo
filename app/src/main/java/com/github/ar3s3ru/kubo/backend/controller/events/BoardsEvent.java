package com.github.ar3s3ru.kubo.backend.controller.events;

import android.support.annotation.NonNull;

import com.github.ar3s3ru.kubo.backend.models.BoardsList;

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

public class BoardsEvent {
    private final BoardsList mBoardsList;
    private final int        mHTTPCode;
    private final String     mErrorBody;

    private BoardsEvent(BoardsList list, int code, String error) {
        mBoardsList = list;
        mHTTPCode   = code;
        mErrorBody  = error;
    }

    public BoardsEvent(@NonNull BoardsList list) {
        this(list, 0, null);
    }

    public BoardsEvent(@NonNull String errorBody, int code) {
        this(null, code, errorBody);
    }

    public boolean isSuccessful() {
        return mErrorBody != null;
    }

    public BoardsList getBoardsList() {
        return mBoardsList;
    }

    public int getCode() {
        return mHTTPCode;
    }

    public String getError() {
        return mErrorBody;
    }
}
