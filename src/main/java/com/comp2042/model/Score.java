package com.comp2042.model.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void add(int value){
        score.set(score.get() + value);
    }

    public void reset() {
        score.set(0);
    }
}
