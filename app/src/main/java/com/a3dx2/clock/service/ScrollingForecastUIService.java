package com.a3dx2.clock.service;

import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.a3dx2.clock.view.ScrollAuto;

public class ScrollingForecastUIService {

    private final Handler handler = new Handler();
    private final ScrollingRunner scrollingRunner = new ScrollingRunner();
    private final ScrollAuto scrollAuto = new ScrollAuto();
    private final int orientation;

    private final FrameLayout scrollingView;

    public ScrollingForecastUIService(FrameLayout scrollingView, int orientation) {
        this.scrollingView = scrollingView;
        this.orientation = orientation;
    }

    public void activateScroll() {
        handler.removeCallbacks(scrollingRunner);
        handler.post(scrollingRunner);
    }

    private class ScrollingRunner implements Runnable {
        @Override
        public void run() {
            if (orientation == LinearLayout.VERTICAL) {
                scrollingView.scrollBy(0, scrollAuto.getIncrement());
                View lastView = scrollingView.getChildAt(scrollingView.getChildCount() - 1);
                int diff = lastView.getBottom() - (scrollingView.getHeight() + scrollingView.getScrollY());
                if (!scrollAuto.isFlippedLastCall()) {
                    if (diff == 0) {
                        scrollAuto.flip();
                    }
                    if (scrollingView.getScrollY() == 0) {
                        scrollAuto.flip();
                    }
                } else {
                    scrollAuto.nextCall();
                }
            } else {
                scrollingView.scrollBy(scrollAuto.getIncrement(), 0);
                View lastView = scrollingView.getChildAt(scrollingView.getChildCount() - 1);
                int diff = lastView.getRight() - (scrollingView.getWidth() + scrollingView.getScrollX());
                if (!scrollAuto.isFlippedLastCall()) {
                    if (diff == 0) {
                        scrollAuto.flip();
                    }
                    if (scrollingView.getScrollX() == 0) {
                        scrollAuto.flip();
                    }
                } else {
                    scrollAuto.nextCall();
                }
            }
            handler.postDelayed(this, scrollAuto.getHandlerDelay());
        }
    }

}
