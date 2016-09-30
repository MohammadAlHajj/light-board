package com.lightBoard.controls.patterns;

import java.awt.Point;

import com.lightBoard.controls.Pattern;

/**
 * 
 */




/**
 * @author Moham
 *
 */
public class InfinityPattern extends Pattern
{
	@Override
	public Point getPointAt(int maxWidth, int maxHeight, double time)
	{
		Point p = new Point();
		double scale = 2 / (3 - Math.cos(2*time));
		double x = scale * Math.cos(time) / 2.1; // 0.1 to note reach the border of the view 
		double y = scale * Math.sin(2*time) / 2;
		
		p.x = (int)Math.floor(x * maxWidth) + maxWidth/2;
		p.y = (int)Math.floor(y * maxHeight) + maxHeight/2;	
		return p;
	}


}
