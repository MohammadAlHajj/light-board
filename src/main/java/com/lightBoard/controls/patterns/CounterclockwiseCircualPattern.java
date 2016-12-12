package com.lightBoard.controls.patterns;

import com.lightBoard.controls.Pattern;

import java.awt.Point;

/**
 * Created by Moham on 12/7/2016.
 */
public class CounterclockwiseCircualPattern implements Pattern
{
	@Override
	public Point getPointAt(int maxWidth, int maxHeight, double time) {
		Point p = new Point();
		double scale = Math.min(maxWidth, maxHeight);
		double x = - Math.cos(time) / 1.8; // 0.1 to not reach the border of the view
		double y =  Math.sin(time) / 2.1;

		p.x = (int) Math.floor(x * scale) + maxWidth/2;
		p.y = (int) Math.floor(y * scale) + maxHeight/2;
		return p;
	}
}
