package com.comp2042.model.game;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.data.ClearRow;
import com.comp2042.model.data.NextShapeInfo;
import com.comp2042.model.data.ViewData;
import com.comp2042.util.MatrixOperations;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    // hold & next pieces
    private Brick heldPiece = null;
    private boolean holdUsedThisDrop = false;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        // matrix[row][col] -> [height][width]
        currentGameMatrix = new int[this.height][this.width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        // spawn near top: x center, y = 0
        currentOffset = new Point(4,0);
        // reset hold usage for this drop
        holdUsedThisDrop = false;
        // return true if spawn collides (game over)
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        // include next 3 preview and held piece snapshot
        int[][] nextBrick = brickGenerator.getNextBrick().getShapeMatrix().get(0); // assume RandomBrickGenerator has getNext(int)
        int[][] heldPieceMatrix= null;

        if (heldBrick != null) {
            heldPieceMatrix = heldBrick.getShapeMatrix.get(0);
        }
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), nextBrick, heldPieceMatrix);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        heldBrick = null;
        holdUsedThisDrop = false;
        createNewBrick();
    }

    // --- new feature methods ---

    @Override
    public int hardDrop() {
        // move current piece down until it collides; return rows dropped
        int dropRows = 0;
        while (true) {
            Point p = new Point(currentOffset);
            p.translate(0, 1);
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
            if (conflict) break;
            currentOffset = p;
            dropRows++;
        }
        return dropRows;
    }

    @Override
    public boolean holdPiece() {
        if (holdUsedThisDrop) {
            return false;
        }
        Brick currentBrick = brickRotator.getBrick();
        if (heldPiece == null) {
            heldPiece = currentBrick;
            createNewBrick();
        } else {
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(4,0);

            if(MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY())) {
                brickRotator.setBrick(currentBrick);
                heldBrick =temp;
                return false;
            }
        }
        holdUsedThisDrop = true;
        return true;
    }

    @Override
    public int[][] getHeldPiece() {
        if (heldPiece == null) {
            return null;
        }
        return MatrixOperations.copy(heldBrick.getShapeMatrix().get(0));
    }
}
