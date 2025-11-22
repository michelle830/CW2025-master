package com.comp2042.config;

import java.util.logging.Level;

public final class GameConfig {

    private static GameConfig instance;

    // Board dimensions
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;
    public static final int VISIBLE_HEIGHT = 23;

    // Visual settings
    public static final int BRICK_SIZE = 20;
    public static final int GRID_GAP = 1;

    // Game timing (milliseconds)
    public static final int INITIAL_FALL_SPEED = 400;
    public static final double SPEED_MULTIPLIER_PER_LEVEL = 0.9; // 10% faster each level
    public static final int MIN_FALL_SPEED = 50; // Minimum speed cap

    // Scoring
    public static final int SOFT_DROP_POINTS = 1;
    public static final int HARD_DROP_POINTS_PER_CELL = 2;
    public static final int BASE_LINE_CLEAR_POINTS = 50;

    // Level progression
    public static final int LINES_PER_LEVEL = 10;
    public static final int STARTING_LEVEL = 1;

    // Brick spawn position
    public static final int SPAWN_X = 3;
    public static final int SPAWN_Y = 0;

    // UI Layout offsets (from FXML)
    public static final int BRICK_PANEL_Y_OFFSET = -42;

    // Color codes for bricks
    public static final int COLOR_EMPTY = 0;
    public static final int COLOR_I_BRICK = 1;
    public static final int COLOR_J_BRICK = 2;
    public static final int COLOR_L_BRICK = 3;
    public static final int COLOR_O_BRICK = 4;
    public static final int COLOR_S_BRICK = 5;
    public static final int COLOR_T_BRICK = 6;
    public static final int COLOR_Z_BRICK = 7;

    // Animation settings
    public static final int NOTIFICATION_FADE_DURATION = 2000;
    public static final int NOTIFICATION_SLIDE_DURATION = 2500;
    public static final int NOTIFICATION_SLIDE_DISTANCE = -40;

    // Reflection effect settings
    public static final double REFLECTION_FRACTION = 0.8;
    public static final double REFLECTION_TOP_OPACITY = 0.9;
    public static final int REFLECTION_TOP_OFFSET = -12;

    // Ghost piece opacity
    public static final double GHOST_PIECE_OPACITY = 0.3;

    // Combo settings
    public static final int COMBO_TIMEOUT = 5000;
    public static final double COMBO_MULTIPLIER = 0.5;

    private GameConfig() {
        // Private constructor to prevent instantiation
    }

    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public static int getFallSpeedForLevel(int level) {
        int speed = (int) (INITIAL_FALL_SPEED * Math.pow(SPEED_MULTIPLIER_PER_LEVEL, level -1));
        return Math.max(speed, MIN_FALL_SPEED);
    }

    public static int getScoreForLines (int linesCleared) {
        return BASE_LINE_CLEAR_POINTS * linesCleared * linesCleared;
    }

    public static int getHardDropBonus (int cellsDropped) {
        return cellsDropped * HARD_DROP_POINTS_PER_CELL;
    }
}
