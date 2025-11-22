package com.comp2042;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    void createNewGame();

    // new:
    DownData onHardDrop(); // triggered by Space
    ViewData onHoldEvent(); // triggered by Shift

    enum EventType {
        DOWN, LEFT, RIGHT, ROTATE
    }
}
