package com.github.ar3s3ru.kubo.backend.controller;

import com.github.ar3s3ru.kubo.backend.models.BoardsList;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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

/**
 * 4Chan REST API for Retrofit usage.
 */
public interface KuboAPInterface {
    /**
     * Get JSON representation of 4chan available boards
     * @return Call object with BoardsList JSON representation
     */
    @GET("boards.json")
    Call<BoardsList> getBoards();

    /**
     * Get JSON representation of 4chan alive threads (a.k.a catalog)
     * @param board Board path
     * @return Call object with Threads JSON representation
     */
    @GET("{board}/catalog.json")
    Call<List<ThreadsList>> getCatalog(@Path("board") String board);
}
