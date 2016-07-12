package com.github.ar3s3ru.kubo.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Board {

    @SerializedName("board")
    @Expose
    private String board;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("ws_board")
    @Expose
    private int wsBoard;

    @SerializedName("per_page")
    @Expose
    private int perPage;

    @SerializedName("pages")
    @Expose
    private int pages;

    @SerializedName("max_filesize")
    @Expose
    private int maxFilesize;

    @SerializedName("max_webm_filesize")
    @Expose
    private int maxWebmFilesize;

    @SerializedName("max_comment_chars")
    @Expose
    private int maxCommentChars;

    @SerializedName("max_webm_duration")
    @Expose
    private int maxWebmDuration;

    @SerializedName("bump_limit")
    @Expose
    private int bumpLimit;

    @SerializedName("image_limit")
    @Expose
    private int imageLimit;

    @SerializedName("cooldowns")
    @Expose
    private Cooldowns cooldowns;

    @SerializedName("meta_description")
    @Expose
    private String metaDescription;

    @SerializedName("is_archived")
    @Expose
    private int isArchived;

    @SerializedName("spoilers")
    @Expose
    private int spoilers;

    @SerializedName("custom_spoilers")
    @Expose
    private int customSpoilers;

    @SerializedName("forced_anon")
    @Expose
    private int forcedAnon;

    @SerializedName("user_ids")
    @Expose
    private int userIds;

    @SerializedName("code_tags")
    @Expose
    private int codeTags;

    @SerializedName("webm_audio")
    @Expose
    private int webmAudio;

    @SerializedName("min_image_width")
    @Expose
    private int minImageWidth;

    @SerializedName("min_image_height")
    @Expose
    private int minImageHeight;

    @SerializedName("oekaki")
    @Expose
    private int oekaki;

    @SerializedName("country_flags")
    @Expose
    private int countryFlags;

    @SerializedName("sjis_tags")
    @Expose
    private int sjisTags;

    @SerializedName("text_only")
    @Expose
    private int textOnly;

    @SerializedName("require_subject")
    @Expose
    private int requireSubject;

    @SerializedName("math_tags")
    @Expose
    private int mathTags;

    public String getBoard() {
        return board;
    }

    public String getTitle() {
        return title;
    }

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

    public Cooldowns getCooldowns() {
        return cooldowns;
    }

    public String getMetaDescription() {
        return metaDescription;
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
}
