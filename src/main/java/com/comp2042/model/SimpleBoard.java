// refactor: simplified movement logic, improved readability, and removed hardcoded spawn point

package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.*;

import java.awt.Point;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentGameMatrix = new int[width][height];
        this.brickGenerator = new RandomBrickGenerator();
        this.brickRotator = new BrickRotator();
        this.score = new Score();
    }

  // Brick Movement

    private boolean tryMove(int dx, int dy){
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point newPosition = new Point(currentOffset);
        newPosition.translate(dx, dy);
        boolean conflict = MatrixOperations.intersect(currentMatrix,brickRotator.getCurrentShape(),(int)newPosition.getX(), (int)newPosition.getY());

        if(conflict){
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }

    @Override
    public boolean moveBrickDown() {
        return tryMove(0, 1);
    }

    @Override
    public boolean moveBrickLeft() {
        return tryMove(-1,0);
    }

    @Override
    public boolean moveBrickRight() {
        return tryMove(1,0);
    }

    // Brick Rotation


    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());

        if(conflict){
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    // Brick Generation

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);

        int spawnX = Math.max(0, height/2 - brickRotator.getCurrentShape()[0].length/2);
        int spawnY = 0;
        currentOffset = new Point(spawnX, spawnY);

        return MatrixOperations.intersect(currentGameMatrix,brickRotator.getCurrentShape(),(int) currentOffset.getX(), (int)currentOffset.getY());
    }

    // Board State Handling
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0)
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
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
    public  void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}