// refactor: improved GameController flow, fixed scoring logic, and enhanced
// communication between model and GUI for COMP2042 coursework
// description: this controller now prevents unwanted score increments
// triggers GUI updates more cleanly, and handles brick lifecycle reliably

package com.comp2042.controller;

import com.comp2042.model.*;
import com.comp2042.view.GameOverPanel;

public class GameController implements InputEventListener{

    private final Board board;
    private final GuiController gui;

    public GameController(GuiController guiController) {
        this.gui = guiController;

        // Create game board (25 rows X 10 columns)
        this.board = new SimpleBoard(25,10);

        // Prepare first brick and initial board state
        board.createNewBrick();
        gui.setEventListener(this);
        gui.initGameView(board.getBoardMatrix(), board.getViewData());
        gui.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow cleared = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            cleared = board.clearRows();

            // Score update only when lines are cleared
            if (cleared.getLinesRemoved() > 0) {
                board.getScore().add(cleared.getScoreBonus());
            }

            if (board.createNewBrick()) {
                gui.gameOver();
                return new DownData (cleared, board.getViewData());
            }

            gui.refreshGameBackground(board.getBoardMatrix());
        }

        return new DownData(cleared, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        gui.refreshGameBackground(board.getBoardMatrix());
    }
}