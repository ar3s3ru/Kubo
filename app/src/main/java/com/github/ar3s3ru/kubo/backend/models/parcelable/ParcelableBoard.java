package com.github.ar3s3ru.kubo.backend.models.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.ar3s3ru.kubo.backend.models.Board;

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

public class ParcelableBoard implements Parcelable {

    private int    wsBoard;
    private int    perPage;
    private int    pages;
    private int    maxFilesize;
    private int    maxWebmFilesize;
    private int    maxCommentChars;
    private int    maxWebmDuration;
    private int    bumpLimit;
    private int    imageLimit;
    private int    isArchived;
    private int    spoilers;
    private int    customSpoilers;
    private int    forcedAnon;
    private int    userIds;
    private int    codeTags;
    private int    webmAudio;
    private int    minImageWidth;
    private int    minImageHeight;
    private int    oekaki;
    private int    countryFlags;
    private int    sjisTags;
    private int    textOnly;
    private int    requireSubject;
    private int    mathTags;
    private String board;
    private String title;
    private String metaDescription;
    private ParcelableCooldowns cooldowns;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.wsBoard);
        dest.writeInt(this.perPage);
        dest.writeInt(this.pages);
        dest.writeInt(this.maxFilesize);
        dest.writeInt(this.maxWebmFilesize);
        dest.writeInt(this.maxCommentChars);
        dest.writeInt(this.maxWebmDuration);
        dest.writeInt(this.bumpLimit);
        dest.writeInt(this.imageLimit);
        dest.writeInt(this.isArchived);
        dest.writeInt(this.spoilers);
        dest.writeInt(this.customSpoilers);
        dest.writeInt(this.forcedAnon);
        dest.writeInt(this.userIds);
        dest.writeInt(this.codeTags);
        dest.writeInt(this.webmAudio);
        dest.writeInt(this.minImageWidth);
        dest.writeInt(this.minImageHeight);
        dest.writeInt(this.oekaki);
        dest.writeInt(this.countryFlags);
        dest.writeInt(this.sjisTags);
        dest.writeInt(this.textOnly);
        dest.writeInt(this.requireSubject);
        dest.writeInt(this.mathTags);
        dest.writeString(this.board);
        dest.writeString(this.title);
        dest.writeString(this.metaDescription);
        dest.writeParcelable(this.cooldowns, flags);
    }

    public ParcelableBoard(Board board) {
        this.wsBoard = board.getWsBoard();
        this.perPage = board.getPerPage();
        this.pages = board.getPages();
        this.maxFilesize = board.getMaxFilesize();
        this.maxWebmFilesize = board.getMaxWebmFilesize();
        this.maxCommentChars = board.getMaxCommentChars();
        this.maxWebmDuration = board.getMaxWebmDuration();
        this.bumpLimit = board.getBumpLimit();
        this.imageLimit = board.getImageLimit();
        this.isArchived = board.getIsArchived();
        this.spoilers = board.getSpoilers();
        this.customSpoilers = board.getCustomSpoilers();
        this.forcedAnon = board.getForcedAnon();
        this.userIds = board.getUserIds();
        this.codeTags = board.getCodeTags();
        this.webmAudio = board.getWebmAudio();
        this.minImageWidth = board.getMinImageWidth();
        this.minImageHeight = board.getMinImageHeight();
        this.oekaki = board.getOekaki();
        this.countryFlags = board.getCountryFlags();
        this.sjisTags = board.getSjisTags();
        this.textOnly = board.getTextOnly();
        this.requireSubject = board.getRequireSubject();
        this.mathTags = board.getMathTags();
        this.board = board.getBoard();
        this.title = board.getTitle();
        this.metaDescription = board.getMetaDescription();
        this.cooldowns = new ParcelableCooldowns(board.getCooldowns());
    }

    protected ParcelableBoard(Parcel in) {
        this.wsBoard = in.readInt();
        this.perPage = in.readInt();
        this.pages = in.readInt();
        this.maxFilesize = in.readInt();
        this.maxWebmFilesize = in.readInt();
        this.maxCommentChars = in.readInt();
        this.maxWebmDuration = in.readInt();
        this.bumpLimit = in.readInt();
        this.imageLimit = in.readInt();
        this.isArchived = in.readInt();
        this.spoilers = in.readInt();
        this.customSpoilers = in.readInt();
        this.forcedAnon = in.readInt();
        this.userIds = in.readInt();
        this.codeTags = in.readInt();
        this.webmAudio = in.readInt();
        this.minImageWidth = in.readInt();
        this.minImageHeight = in.readInt();
        this.oekaki = in.readInt();
        this.countryFlags = in.readInt();
        this.sjisTags = in.readInt();
        this.textOnly = in.readInt();
        this.requireSubject = in.readInt();
        this.mathTags = in.readInt();
        this.board = in.readString();
        this.title = in.readString();
        this.metaDescription = in.readString();
        this.cooldowns = in.readParcelable(ParcelableCooldowns.class.getClassLoader());
    }

    public static final Parcelable.Creator<ParcelableBoard> CREATOR =
            new Parcelable.Creator<ParcelableBoard>() {
                @Override
                public ParcelableBoard createFromParcel(Parcel source) {
                    return new ParcelableBoard(source);
                }

                @Override
                public ParcelableBoard[] newArray(int size) {
                    return new ParcelableBoard[size];
                }
            };

    /**
     * Getters
     */

    public int getWsBoard() {
        return wsBoard;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getPages() {
        return pages;
    }

    public int getMaxFilesize() {
        return maxFilesize;
    }

    public int getMaxWebmFilesize() {
        return maxWebmFilesize;
    }

    public int getMaxCommentChars() {
        return maxCommentChars;
    }

    public int getMaxWebmDuration() {
        return maxWebmDuration;
    }

    public int getBumpLimit() {
        return bumpLimit;
    }

    public int getImageLimit() {
        return imageLimit;
    }

    public int getIsArchived() {
        return isArchived;
    }

    public int getSpoilers() {
        return spoilers;
    }

    public int getCustomSpoilers() {
        return customSpoilers;
    }

    public int getForcedAnon() {
        return forcedAnon;
    }

    public int getUserIds() {
        return userIds;
    }

    public int getCodeTags() {
        return codeTags;
    }

    public int getWebmAudio() {
        return webmAudio;
    }

    public int getMinImageWidth() {
        return minImageWidth;
    }

    public int getMinImageHeight() {
        return minImageHeight;
    }

    public int getOekaki() {
        return oekaki;
    }

    public int getCountryFlags() {
        return countryFlags;
    }

    public int getSjisTags() {
        return sjisTags;
    }

    public int getTextOnly() {
        return textOnly;
    }

    public int getRequireSubject() {
        return requireSubject;
    }

    public int getMathTags() {
        return mathTags;
    }

    public String getBoard() {
        return board;
    }

    public String getTitle() {
        return title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public ParcelableCooldowns getCooldowns() {
        return cooldowns;
    }
}
