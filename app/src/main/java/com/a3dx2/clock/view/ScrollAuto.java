package com.a3dx2.clock.view;

import android.view.View;

public class ScrollAuto {

    private int increment = 1;
    private boolean flippedLastCall;

    public ScrollAuto() {
        super();
    }

    public void flip() {
        if (increment == 1) {
            increment = -1;
        } else {
            increment = 1;
        }
        flippedLastCall = true;
    }

    public void nextCall() {
        flippedLastCall = false;
    }

    public boolean isFlippedLastCall() {
        return flippedLastCall;
    }

    public int getIncrement() {
        return increment;
    }
}
