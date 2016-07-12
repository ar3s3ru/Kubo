package com.github.ar3s3ru.kubo.backend.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.models.Board;
import com.github.ar3s3ru.kubo.utils.KuboUtilities;

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
 * Board table helper class.
 * Contains all the relevant representation for table creation/destruction,
 * cursor creation routines, getters, setters...
 */
public class KuboTableBoard {
    private static final String TABLE_NAME  = "board";  // Table name
    private static final String LIMIT_QUERY = "10";     // Pagination

    private static final String KEY_BOARD = "board";
    private static final String KEY_TITLE = "title";
    private static final String KEY_STARD = "starred";
    private static final String KEY_DESCR = "descr";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_ANON  = "anon";

    private static final String KEY_MIN_WIDTH  = "min_width";
    private static final String KEY_MIN_HEIGHT = "min_height";
    private static final String KEY_MAX_FSIZE  = "max_fsize";

    private static final String DB_BOARD = KEY_BOARD + " text not null unique";
    private static final String DB_TITLE = KEY_TITLE + " text not null unique";
    private static final String DB_STARD = KEY_STARD + " integer not null";
    private static final String DB_DESCR = KEY_DESCR + " text not null";
    private static final String DB_FLAGS = KEY_FLAGS + " integer not null";
    private static final String DB_ANON  = KEY_ANON  + " integer not null";

    private static final String DB_MIN_WIDTH  = KEY_MIN_WIDTH  + " integer not null";
    private static final String DB_MIN_HEIGHT = KEY_MIN_HEIGHT + " integer not null";
    private static final String DB_MAX_FSIZE  = KEY_MAX_FSIZE  + " integer not null";

    /**
     * SQL statements for table creation/destruction
     */
    public static final String DB_DROP   = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String DB_CREATE = "create table " + TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + DB_BOARD + ", " + DB_TITLE + ", " + DB_DESCR + ", "
            + DB_FLAGS + ", " + DB_ANON  + ", " + DB_STARD + ", "
            + DB_MIN_WIDTH  + ", " + DB_MIN_HEIGHT + ", " + DB_MAX_FSIZE + ");";

    /**
     * Writes starring status for a Board row identified by a certain id
     * @param helper SQLite application helper instance
     * @param id Board row primary key
     * @param value Boolean (yeah...) value to write
     * @return Number of rows affected by the change (should be == 1 if successful)
     */
    private static int writeStarring(KuboSQLHelper helper, long id, int value) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_STARD, value);

        return helper.getWritableDatabase().update(TABLE_NAME, cv, "_id=" + id, null);
    }

    /**
     * Insert a new Board row into the database
     * @param helper SQLite application helper instance
     * @param board Board JSON representation to write
     * @return Row primary key if successful, throws SQLException if it fails
     */
    public static long insertBoard(KuboSQLHelper helper, Board board) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_BOARD, board.getBoard());
        cv.put(KEY_TITLE, board.getTitle());
        cv.put(KEY_STARD, 0);
        cv.put(KEY_DESCR, board.getMetaDescription());
        cv.put(KEY_FLAGS, board.getCountryFlags());
        cv.put(KEY_ANON,  board.getForcedAnon());
        cv.put(KEY_MIN_WIDTH,  board.getMinImageWidth());
        cv.put(KEY_MIN_HEIGHT, board.getMinImageHeight());
        cv.put(KEY_MAX_FSIZE,  board.getMaxFilesize());

        return helper.getReadableDatabase().insertOrThrow(TABLE_NAME, null, cv);
    }

    /**
     * Move Board id to starred boards
     * @param helper SQLite application helper instance
     * @param id Board row primary key
     * @return true if the operation was successful, false otherwise
     */
    public static boolean starringBoard(KuboSQLHelper helper, long id) {
        // Returns true if only one row is affected from the change
        return writeStarring(helper, id, 1) == 1;
    }

    /**
     * Move Board id to unstarred boards
     * @param helper SQLite application helper instance
     * @param id Board row primary key
     * @return true if the operation was successful, false otherwise
     */
    public static boolean unstarringBoard(KuboSQLHelper helper, long id) {
        // Same as above
        return writeStarring(helper, id, 0) == 1;
    }

    /**
     * Get all the starred boards into the DB
     * @param helper SQLite application helper instance
     * @return Starred boards cursor
     */
    public static Cursor getStarredBoards(KuboSQLHelper helper) {
        return helper.getReadableDatabase().query(
                TABLE_NAME,             // SELECT FROM tablename
                null,                   // all columns to return
                KEY_STARD + "=1",       // WHERE keyStard=true
                null, null, null, null
        );
    }

    /**
     * Get all the unstarred boards into the DB
     * @param helper SQLite application helper instance
     * @return Unstarred boards cursor
     */
    public static Cursor getUnstarredBoards(KuboSQLHelper helper) {
        return helper.getReadableDatabase().query(
                TABLE_NAME, null, KEY_STARD + "=0", null, null, null, null//, LIMIT_QUERY
                // LIMIT limitQuery (using pagination to prevent  overusing memory)
        );
    }

    /**
     * Cursor getters
     */

    /**
     * Returns board 'Board" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board field of the row in position
     */
    public static String getBoard(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(KEY_BOARD));
    }

    /**
     * Returns board 'Title" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board title of the row in position
     */
    public static String getTitle(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(KEY_TITLE));
    }

    /**
     * Returns board 'Starred" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board starring status of the row in position
     */
    public static boolean getStarred(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return KuboUtilities.convertFromInt(cursor.getInt(cursor.getColumnIndex(KEY_TITLE)));
    }

    /**
     * Returns board 'Description" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board description of the row in position
     */
    public static String getMetaDescription(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(KEY_DESCR));
    }

    /**
     * Returns board 'CountryFlags" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board country flags abilitation of the row in position
     */
    public static boolean getCountryFlags(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return KuboUtilities.convertFromInt(cursor.getInt(cursor.getColumnIndex(KEY_FLAGS)));
    }

    /**
     * Returns board 'ForcedAnon" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board forced anonymous of the row in position
     */
    public static boolean getForcedAnon(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return KuboUtilities.convertFromInt(cursor.getInt(cursor.getColumnIndex(KEY_ANON)));
    }

    /**
     * Returns board 'MinWidth" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board uploading image min width of the row in position
     */
    public static int getMinWidth(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex(KEY_MIN_WIDTH));
    }

    /**
     * Returns board 'MinHeight" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board uploading image min height of the row in position
     */
    public static int getMinHeight(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex(KEY_MIN_HEIGHT));
    }

    /**
     * Returns board 'MaxFilesize" field from a Cursor in position 'position'
     * @param cursor Query cursor
     * @param position Row position in the cursor
     * @return Board uploading image max filesize of the row in position
     */
    public static int getMaxFilesize(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex(KEY_MAX_FSIZE));
    }
}
