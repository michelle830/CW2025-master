package com.comp2042.model.game;

import com.comp2042.model.data.ClearRow;
import com.comp2042.model.data.ViewData;

public interface Board {

    boolean moveBrickDown();
    boolean moveBrickLeft();
    boolean moveBrickRight();
    boolean rotateLeftBrick();
    boolean createNewBrick();
    int[][] getBoardMatrix();
    ViewData getViewData();
    void mergeBrickToBackground();
    ClearRow clearRows;
    Score getScore();
    void newGame();

    int hardDrop();
    boolean holdPiece();
    int[][] getHeldPiece();


