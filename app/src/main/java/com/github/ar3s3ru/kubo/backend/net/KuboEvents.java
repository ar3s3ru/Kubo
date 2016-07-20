package com.github.ar3s3ru.kubo.backend.net;

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
 * Container for all the private intents to use across the application,
 * with LocalBroadcastManager receivers.
 */
public class KuboEvents {
    /**
     * Requesting Boards list event
     */
    public static final String BOARDS        = "com.github.ar3s3ru.kubo.event.boards";
    public static final String BOARDS_STATUS = "com.github.ar3s3ru.kubo.event.boards.status";

    public static final String BOARDS_ERR    = "com.github.ar3s3ru.kubo.event.boards.err";
    public static final String BOARDS_ERRCOD = "com.github.ar3s3ru.kubo.event.boards.errcod";

    /**
     * Requesting Catalog event
     */
    public static final String CATALOG        = "com.github.ar3s3ru.kubo.event.catalog";
    public static final String CATALOG_STATUS = "com.github.ar3s3ru.kubo.event.catalog.status";
    public static final String CATALOG_RESULT = "com.github.ar3s3ru.kubo.event.catalog.result";

    // TODO: ERROR HANDLING!
    public static final String CATALOG_ERROR  = "com.github.ar3s3ru.kubo.event.catalog.error";
    public static final String CATALOG_ERRCOD = "com.github.ar3s3ru.kubo.event.catalog.errcod";

    /**
     * Requesting Thread replies
     */
    public static final String REPLIES        = "com.github.ar3s3ru.kubo.event.replies";
    public static final String REPLIES_STATUS = "com.github.ar3s3ru.kubo.event.replies.status";
    public static final String REPLIES_RESULT = "com.github.ar3s3ru.kubo.event.replies.result";

    // TODO: ERROR HANDLING!
    public static final String REPLIES_ERROR  = "com.github.ar3s3ru.kubo.event.replies.error";
    public static final String REPLIES_ERRCOD = "com.github.ar3s3ru.kubo.event.replies.errcod";

    /**
     * Notifying followed thread update
     */
    public static final String FOLLOWING_UPDATE = "com.github.ar3s3ru.kubo.event.update.follow";
}
