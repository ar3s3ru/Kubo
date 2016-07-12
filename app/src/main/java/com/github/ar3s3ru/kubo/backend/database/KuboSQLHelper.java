package com.github.ar3s3ru.kubo.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;

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
 * SQLite application helper.
 * Handles creation and upgrading, using Table classes.
 */
public class KuboSQLHelper extends SQLiteOpenHelper {
    // Database versioning informations
    private static final String DB_NAME = "kubo.db";
    private static final int    DB_VERS = 1;

    // Put all the table creators here
    private static final String[] DB_CREATORS = {
            KuboTableBoard.DB_CREATE
    };

    // Put all the table destructors here
    private static final String[] DB_DROPS = {
            KuboTableBoard.DB_DROP
    };

    public KuboSQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String DB_DROP : DB_DROPS) {
            // TODO: consider using some migration mechanism
            // Destruct all previous tables
            db.execSQL(DB_DROP);
        }

        // Recreate tables
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String DB_CREATE : DB_CREATORS) {
            // Construct all tables
            db.execSQL(DB_CREATE);
        }
    }
}
