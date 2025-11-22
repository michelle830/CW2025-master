package com.comp2042.model.data;

import com.comp2042.util.MatrixOperations;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData; // original single next preview (backwards-compatible)
    private final int[][] heldPieceData; // may be null

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int[][] heldPieceData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.heldPieceData = heldPieceData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    public int[][] getHeldPieceData() {
        return heldPieceData == null ? null : MatrixOperations.copy(heldPieceData);
    }
}
