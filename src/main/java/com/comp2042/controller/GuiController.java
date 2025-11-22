package com.comp2042.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private GridPane nextPreviewPane;

    @FXML
    private GridPane holdPreviewPane;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                    keyEvent.consume();
                    return;
                }
                if (keyEvent.getCode() == KeyCode.P) {
                    pauseGame(null);
                    keyEvent.consume();
                    return;
                }
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        // Hard drop
                        DownData d = eventListener.onHardDrop();
                        if (d.getClearRow() != null && d.getClearRow().getLinesRemoved() > 0) {
                            NotificationPanel notificationPanel = new NotificationPanel("+" + d.getClearRow().getScoreBonus());
                            groupNotification.getChildren().add(notificationPanel);
                            notificationPanel.showScore(groupNotification.getChildren());
                        }
                        refreshBrick(d.getViewData());
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SHIFT) {
                        refreshBrick(eventListener.onHoldEvent());
                        keyEvent.consume();
                    }
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // boardMatrix: rows x cols
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        gamePanel.getChildren().clear();
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        brickPanel.getChildren().clear();
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        updateBrickPanelPosition(brick);

        // timeline
        if (timeLine != null) timeLine.stop();
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        // initial previews
        refreshPreviewPanels(brick);
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0:  return Color.TRANSPARENT;
            case 1:  return Color.AQUA;
            case 2:  return Color.BLUEVIOLET;
            case 3:  return Color.DARKGREEN;
            case 4:  return Color.YELLOW;
            case 5:  return Color.RED;
            case 6:  return Color.BEIGE;
            case 7:  return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }

    private void updateBrickPanelPosition(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            updateBrickPanelPosition(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            // draw ghost and previews
            drawGhost(brick);
            refreshPreviewPanels(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(6);
        rectangle.setArcWidth(6);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void bindScore(IntegerProperty integerProperty) {
        // optional: bind to score label if needed
    }

    public void gameOver() {
        if (timeLine != null) timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        groupNotification.setVisible(true);
        // Optionally save highscore here by calling HighScoreStore
    }

    public void newGame(ActionEvent actionEvent) {
        if (timeLine != null) timeLine.stop();
        gameOverPanel.setVisible(false);
        if (eventListener != null) eventListener.createNewGame();
        gamePanel.requestFocus();
        if (timeLine != null) timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        if (timeLine == null) return;
        if (isPause.get()) {
            timeLine.play();
            isPause.set(false);
            groupNotification.setVisible(false);
        } else {
            timeLine.pause();
            isPause.set(true);
            groupNotification.setVisible(true);
        }
        gamePanel.requestFocus();
    }

    // --- Preview + Hold + Ghost helpers ---

    private void refreshPreviewPanels(ViewData v) {
        // Next pieces preview
        nextPreviewPane.getChildren().clear();
        int[][][] nextPieces = v.getNextPieces();
        if (nextPieces != null) {
            int cellSize = BRICK_SIZE / 2;
            for (int idx = 0; idx < nextPieces.length; idx++) {
                int[][] shape = nextPieces[idx];
                // render shape in its own grid section (stack vertically)
                int offsetY = idx * (shape.length + 1) * cellSize;
                for (int r = 0; r < shape.length; r++) {
                    for (int c = 0; c < shape[r].length; c++) {
                        Rectangle rect = new Rectangle(cellSize, cellSize);
                        rect.setFill(getFillColor(shape[r][c]));
                        rect.setTranslateX(c * cellSize);
                        rect.setTranslateY(offsetY + r * cellSize);
                        nextPreviewPane.getChildren().add(rect);
                    }
                }
            }
        }

        // Held piece
        holdPreviewPane.getChildren().clear();
        int[][] held = v.getHeldPiece();
        if (held != null) {
            int cellSize = BRICK_SIZE / 2;
            for (int r = 0; r < held.length; r++) {
                for (int c = 0; c < held[r].length; c++) {
                    Rectangle rect = new Rectangle(cellSize, cellSize);
                    rect.setFill(getFillColor(held[r][c]));
                    rect.setTranslateX(c * cellSize);
                    rect.setTranslateY(r * cellSize);
                    holdPreviewPane.getChildren().add(rect);
                }
            }
        }
    }

    private void drawGhost(ViewData v) {
        // Clear any existing ghost by re-rendering background (we maintain displayMatrix)
        // Find drop Y:
        int[][] board = new int[displayMatrix.length][displayMatrix[0].length];
        for (int r = 0; r < board.length; r++) for (int c = 0; c < board[0].length; c++) board[r][c] = (int) (displayMatrix[r][c].getFill() == Color.TRANSPARENT ? 0 : 1);
        int[][] shape = v.getBrickData();
        int x = v.getxPosition();
        int y = v.getyPosition();
        int dropY = y;
        while (true) {
            if (MatrixOperations.intersect(board, shape, x, dropY + 1)) break;
            dropY++;
        }
        // Draw ghost into brickPanel overlay (we'll create translucent rectangles)
        // First, remove any existing ghost rectangles (we don't have tags — so simplest way: re-render current brickRectangles normally then overlay)
        // We'll overlay ghosts onto displayMatrix directly (but only where empty)
        // Simpler approach: draw semi-transparent rects on gamePanel (not ideal but works)
        // Remove previous ghost nodes (we'll just re-add each time).
        // Create translucent rectangles on top of gamePanel layer:
        // Note: for simplicity, we won't clear previous nodes — instead we set opacity low on tile edges by updating displayMatrix for empty only
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int gr = dropY + i;
                    int gc = x + j;
                    if (gr >= 0 && gr < displayMatrix.length && gc >= 0 && gc < displayMatrix[0].length) {
                        Rectangle r = displayMatrix[gr][gc];
                        Paint base = getFillColor(0);
                        // create ghost effect by overlaying a faint rectangle on top (we'll set a stroke)
                        r.setStroke(Color.GRAY);
                        r.setStrokeWidth(0.5);
                        r.setOpacity(0.7);
                    }
                }
            }
        }
    }
}
