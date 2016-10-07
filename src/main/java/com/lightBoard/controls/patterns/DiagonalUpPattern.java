package com.lightBoard.controls.patterns;

import com.lightBoard.controls.Pattern;

import java.awt.Point;

/**
 * Created by Mohammad on 10/7/2016.
 */
public class DiagonalUpPattern implements Pattern {

    @Override
    public Point getPointAt(int maxWidth, int maxHeight, double time) {
        Point p = new Point();
        double x = Math.cos(time) / 2.1; // 0.1 to not reach the border of the view
        double y = - Math.cos(time) / 2.1; // 0.1 to not reach the border of the view

        p.x = (int)Math.floor(x * maxWidth) + maxWidth/2;
        p.y = (int)Math.floor(y * maxHeight) + maxHeight/2;
        return p;
    }
}
