package com.comp2042.logic.bricks;

import java.util.Random;

public class RandomBrickGenerator implements BrickGenerator {
    private final Random random;
    private Brick currentBrick;
    private Brick nextBrick;

    public RandomBrickGenerator() {
        this.random = new Random();
        this.currentBrick = generateRandomBrick();
        this.nextBrick = generateRandomBrick();
    }

    @Override
    public Brick getBrick() {
        currentBrick = nextBrick;
        nextBrick = generateRandomBrick();
        return currentBrick;
    }

    @Override
    public Brick getNextBrick() {
        return nextBrick;
    }

    private Brick generateRandomBrick() {
        int type = random.nextInt(7);
        switch (type) {
            case 0: return BrickFactory.createIBrick();
            case 1: return BrickFactory.createJBrick();
            case 2: return BrickFactory.createLBrick();
            case 3: return BrickFactory.createOBrick();
            case 4: return BrickFactory.createSBrick();
            case 5: return BrickFactory.createTBrick();
            case 6: return BrickFactory.createZBrick();
            default: return BrickFactory.createIBrick();
        }
    }
}




