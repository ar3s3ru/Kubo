
package com.github.ar3s3ru.kubo.backend.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardsList {

    @SerializedName("boards")
    @Expose
    private List<Board> boards = new ArrayList<>();

    /**
     * 
     * @return
     *     The boards
     */
    public List<Board> getBoards() {
        return boards;
    }

    /**
     * 
     * @param boards
     *     The boards
     */
    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

}
