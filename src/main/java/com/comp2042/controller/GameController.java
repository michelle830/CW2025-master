package com.comp2042.controller;

import com.comp2042.model.*;
import com.comp2042.view.GameOverPanel;

public class GameController implements InputEventListener {

    private final Board board;
    private final GuiController viewGuiController;

    public GameController(GuiController controller) {
        this.viewGuiController = controller;
        this.board = new SimpleBoard(25,10);

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow cleared = null;

        if (!canMove) {
            // Brick has landed - merge and create a new one
            board.mergeBrickToBackground();
            cleared = board.clearRows();

            // Add score if any rows were cleared
            if (cleared.getLinesRemoved() > 0) {
                board.getScore().add(cleared.getScoreBonus());
            }

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
                return new DownData(cleared, board.getViewData());
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
            }
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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
