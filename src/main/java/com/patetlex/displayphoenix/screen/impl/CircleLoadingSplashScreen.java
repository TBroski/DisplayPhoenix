package com.patetlex.displayphoenix.screen.impl;

import com.patetlex.displayphoenix.screen.SplashScreen;
import com.patetlex.displayphoenix.widgets.impl.CircleProgressWidget;

import java.awt.*;

/**
 * @author TBroski
 */
public class CircleLoadingSplashScreen extends SplashScreen {
    public CircleLoadingSplashScreen(Image image, int width, int height, float radius, float thickness, Color loadingColor, Color emptyColor) {
        super(new CircleProgressWidget(100, radius, thickness, loadingColor, emptyColor), image, width, height);
    }

    public CircleLoadingSplashScreen(Image image, int width, int height, float radius, float thickness, Color loadingColor, Color emptyColor, int offsetCircleX, int offsetCircleY) {
        super(new CircleProgressWidget(100, radius, thickness, loadingColor, emptyColor).offset(offsetCircleX, offsetCircleY), image, width, height);
    }
}
