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

    /**
     * 
     * @return
     *     The board
     */
    public String getBoard() {
        return board;
    }

    /**
     * 
     * @param board
     *     The board
     */
    public void setBoard(String board) {
        this.board = board;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The wsBoard
     */
    public int getWsBoard() {
        return wsBoard;
    }

    /**
     * 
     * @param wsBoard
     *     The ws_board
     */
    public void setWsBoard(int wsBoard) {
        this.wsBoard = wsBoard;
    }

    /**
     * 
     * @return
     *     The perPage
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * 
     * @param perPage
     *     The per_page
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    /**
     * 
     * @return
     *     The pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * 
     * @param pages
     *     The pages
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * 
     * @return
     *     The maxFilesize
     */
    public int getMaxFilesize() {
        return maxFilesize;
    }

    /**
     * 
     * @param maxFilesize
     *     The max_filesize
     */
    public void setMaxFilesize(int maxFilesize) {
        this.maxFilesize = maxFilesize;
    }

    /**
     * 
     * @return
     *     The maxWebmFilesize
     */
    public int getMaxWebmFilesize() {
        return maxWebmFilesize;
    }

    /**
     * 
     * @param maxWebmFilesize
     *     The max_webm_filesize
     */
    public void setMaxWebmFilesize(int maxWebmFilesize) {
        this.maxWebmFilesize = maxWebmFilesize;
    }

    /**
     * 
     * @return
     *     The maxCommentChars
     */
    public int getMaxCommentChars() {
        return maxCommentChars;
    }

    /**
     * 
     * @param maxCommentChars
     *     The max_comment_chars
     */
    public void setMaxCommentChars(int maxCommentChars) {
        this.maxCommentChars = maxCommentChars;
    }

    /**
     * 
     * @return
     *     The maxWebmDuration
     */
    public int getMaxWebmDuration() {
        return maxWebmDuration;
    }

    /**
     * 
     * @param maxWebmDuration
     *     The max_webm_duration
     */
    public void setMaxWebmDuration(int maxWebmDuration) {
        this.maxWebmDuration = maxWebmDuration;
    }

    /**
     * 
     * @return
     *     The bumpLimit
     */
    public int getBumpLimit() {
        return bumpLimit;
    }

    /**
     * 
     * @param bumpLimit
     *     The bump_limit
     */
    public void setBumpLimit(int bumpLimit) {
        this.bumpLimit = bumpLimit;
    }

    /**
     * 
     * @return
     *     The imageLimit
     */
    public int getImageLimit() {
        return imageLimit;
    }

    /**
     * 
     * @param imageLimit
     *     The image_limit
     */
    public void setImageLimit(int imageLimit) {
        this.imageLimit = imageLimit;
    }

    /**
     * 
     * @return
     *     The cooldowns
     */
    public Cooldowns getCooldowns() {
        return cooldowns;
    }

    /**
     * 
     * @param cooldowns
     *     The cooldowns
     */
    public void setCooldowns(Cooldowns cooldowns) {
        this.cooldowns = cooldowns;
    }

    /**
     * 
     * @return
     *     The metaDescription
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /**
     * 
     * @param metaDescription
     *     The meta_description
     */
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    /**
     * 
     * @return
     *     The isArchived
     */
    public int getIsArchived() {
        return isArchived;
    }

    /**
     * 
     * @param isArchived
     *     The is_archived
     */
    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    /**
     * 
     * @return
     *     The spoilers
     */
    public int getSpoilers() {
        return spoilers;
    }

    /**
     * 
     * @param spoilers
     *     The spoilers
     */
    public void setSpoilers(int spoilers) {
        this.spoilers = spoilers;
    }

    /**
     * 
     * @return
     *     The customSpoilers
     */
    public int getCustomSpoilers() {
        return customSpoilers;
    }

    /**
     * 
     * @param customSpoilers
     *     The custom_spoilers
     */
    public void setCustomSpoilers(int customSpoilers) {
        this.customSpoilers = customSpoilers;
    }

    /**
     * 
     * @return
     *     The forcedAnon
     */
    public int getForcedAnon() {
        return forcedAnon;
    }

    /**
     * 
     * @param forcedAnon
     *     The forced_anon
     */
    public void setForcedAnon(int forcedAnon) {
        this.forcedAnon = forcedAnon;
    }

    /**
     * 
     * @return
     *     The userIds
     */
    public int getUserIds() {
        return userIds;
    }

    /**
     * 
     * @param userIds
     *     The user_ids
     */
    public void setUserIds(int userIds) {
        this.userIds = userIds;
    }

    /**
     * 
     * @return
     *     The codeTags
     */
    public int getCodeTags() {
        return codeTags;
    }

    /**
     * 
     * @param codeTags
     *     The code_tags
     */
    public void setCodeTags(int codeTags) {
        this.codeTags = codeTags;
    }

    /**
     * 
     * @return
     *     The webmAudio
     */
    public int getWebmAudio() {
        return webmAudio;
    }

    /**
     * 
     * @param webmAudio
     *     The webm_audio
     */
    public void setWebmAudio(int webmAudio) {
        this.webmAudio = webmAudio;
    }

    /**
     * 
     * @return
     *     The minImageWidth
     */
    public int getMinImageWidth() {
        return minImageWidth;
    }

    /**
     * 
     * @param minImageWidth
     *     The min_image_width
     */
    public void setMinImageWidth(int minImageWidth) {
        this.minImageWidth = minImageWidth;
    }

    /**
     * 
     * @return
     *     The minImageHeight
     */
    public int getMinImageHeight() {
        return minImageHeight;
    }

    /**
     * 
     * @param minImageHeight
     *     The min_image_height
     */
    public void setMinImageHeight(int minImageHeight) {
        this.minImageHeight = minImageHeight;
    }

    /**
     * 
     * @return
     *     The oekaki
     */
    public int getOekaki() {
        return oekaki;
    }

    /**
     * 
     * @param oekaki
     *     The oekaki
     */
    public void setOekaki(int oekaki) {
        this.oekaki = oekaki;
    }

    /**
     * 
     * @return
     *     The countryFlags
     */
    public int getCountryFlags() {
        return countryFlags;
    }

    /**
     * 
     * @param countryFlags
     *     The country_flags
     */
    public void setCountryFlags(int countryFlags) {
        this.countryFlags = countryFlags;
    }

    /**
     * 
     * @return
     *     The sjisTags
     */
    public int getSjisTags() {
        return sjisTags;
    }

    /**
     * 
     * @param sjisTags
     *     The sjis_tags
     */
    public void setSjisTags(int sjisTags) {
        this.sjisTags = sjisTags;
    }

    /**
     * 
     * @return
     *     The textOnly
     */
    public int getTextOnly() {
        return textOnly;
    }

    /**
     * 
     * @param textOnly
     *     The text_only
     */
    public void setTextOnly(int textOnly) {
        this.textOnly = textOnly;
    }

    /**
     * 
     * @return
     *     The requireSubject
     */
    public int getRequireSubject() {
        return requireSubject;
    }

    /**
     * 
     * @param requireSubject
     *     The require_subject
     */
    public void setRequireSubject(int requireSubject) {
        this.requireSubject = requireSubject;
    }

    /**
     * 
     * @return
     *     The mathTags
     */
    public int getMathTags() {
        return mathTags;
    }

    /**
     * 
     * @param mathTags
     *     The math_tags
     */
    public void setMathTags(int mathTags) {
        this.mathTags = mathTags;
    }
}
