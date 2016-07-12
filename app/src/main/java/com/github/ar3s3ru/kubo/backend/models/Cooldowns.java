
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

    public int getThreads() {
        return threads;
    }

    public int getReplies() {
        return replies;
    }

    public int getImages() {
        return images;
    }

    public int getRepliesIntra() {
        return repliesIntra;
    }

    public int getImagesIntra() {
        return imagesIntra;
    }
}
