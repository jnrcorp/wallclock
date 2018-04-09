package com.a3dx2.clock.view;

import android.view.View;

public class ScrollAuto {

    private static final int DOWN_INCREMENT = 1;
    private static final int UP_INCREMENT = -10;

    private static final int HANDLER_DELAY_FLIPPED_LAST_CALL = 3000;
    private static final int HANDLER_DELAY_DOWN = 25;
    private static final int HANDLER_DELAY_UP = 5;

    private int increment = 1;
    private boolean flippedLastCall;

    public ScrollAuto() {
        super();
    }

    public void flip() {
        if (increment == DOWN_INCREMENT) {
            increment = UP_INCREMENT;
        } else {
            increment = DOWN_INCREMENT;
        }
        flippedLastCall = true;
    }

    public void nextCall() {
        flippedLastCall = false;
    }

    public int getHandlerDelay() {
        if (isFlippedLastCall()) {
            return HANDLER_DELAY_FLIPPED_LAST_CALL;
        } else if (increment == DOWN_INCREMENT) {
            return HANDLER_DELAY_DOWN;
        } else {
            return HANDLER_DELAY_UP;
        }
    }

    public boolean isFlippedLastCall() {
        return flippedLastCall;
    }

    public int getIncrement() {
        return increment;
    }
}
