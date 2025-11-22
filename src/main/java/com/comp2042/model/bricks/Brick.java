package com.comp2042.logic.bricks;

import java.util.List;

public interface Brick {
    List<int[][]> getShapeMatrix();

    default int getColorCode() {
        List<int[][]> shapes = getShapeMatrix();
        if (shapes != null && !shapes.isEmpty()) {
            int[][] firstShape = shapes.get(0);
            for (int[] row: firstShape) {
                for(int cell:row) {
                    if (cell != 0) return cell;
                }
            }
        }
        return 0;
    }
}