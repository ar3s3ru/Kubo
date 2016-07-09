
package com.github.ar3s3ru.kubo.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cooldowns {

    @SerializedName("threads")
    @Expose
    private int threads;
    @SerializedName("replies")
    @Expose
    private int replies;
    @SerializedName("images")
    @Expose
    private int images;
    @SerializedName("replies_intra")
    @Expose
    private int repliesIntra;
    @SerializedName("images_intra")
    @Expose
    private int imagesIntra;

    /**
     * 
     * @return
     *     The threads
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 
     * @param threads
     *     The threads
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * 
     * @return
     *     The replies
     */
    public int getReplies() {
        return replies;
    }

    /**
     * 
     * @param replies
     *     The replies
     */
    public void setReplies(int replies) {
        this.replies = replies;
    }

    /**
     * 
     * @return
     *     The images
     */
    public int getImages() {
        return images;
    }

    /**
     * 
     * @param images
     *     The images
     */
    public void setImages(int images) {
        this.images = images;
    }

    /**
     * 
     * @return
     *     The repliesIntra
     */
    public int getRepliesIntra() {
        return repliesIntra;
    }

    /**
     * 
     * @param repliesIntra
     *     The replies_intra
     */
    public void setRepliesIntra(int repliesIntra) {
        this.repliesIntra = repliesIntra;
    }

    /**
     * 
     * @return
     *     The imagesIntra
     */
    public int getImagesIntra() {
        return imagesIntra;
    }

    /**
     * 
     * @param imagesIntra
     *     The images_intra
     */
    public void setImagesIntra(int imagesIntra) {
        this.imagesIntra = imagesIntra;
    }

}
