package com.comp2042.model.data;

import com.comp2042.ViewData;

public class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;
    private final int[][] boardMatrix;

    public DownData(ClearRow clearRow, ViewData viewData, int[][] boardMatrix) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.boardMatrix = boardMatrix;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }

    public int[][] getBoardMatrix() { return boardMatrix; }
}
