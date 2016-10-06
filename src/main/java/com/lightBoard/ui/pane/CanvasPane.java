package com.lightBoard.ui.pane;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Created by Mohammad on 10/6/2016.
 *
 * ignore this class
 */
public final class CanvasPane extends Region {

    private final Canvas delegated = new Canvas();

    public CanvasPane() {
        getChildren().add(delegated);
        delegated.widthProperty().addListener(observable -> draw());
        delegated.heightProperty().addListener(observable -> draw());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        final double contentX = insets.getLeft();
        final double contentY = insets.getTop();
        final double contentWith = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double contentHeight = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        delegated.relocate(contentX, contentY);
        delegated.setWidth(contentWith);
        delegated.setHeight(contentHeight);
    }

    private void draw() {
        final double width = delegated.getWidth();
        final double height = delegated.getHeight();
        final GraphicsContext gc = delegated.getGraphicsContext2D();
        gc.setFill(Color.GREENYELLOW);
        gc.fillRect(0, 0, width, height);
    }
}