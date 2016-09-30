/**
 * 
 */
package com.lightBoard.controls;

import java.awt.Color;

/**
 * @author Moham
 *
 */
public class ColorHelper
{
	public static final Color SOFT_WHITE = new Color(204,204,204);
	public static final Color SOFT_BLACK = new Color(51,51,51);

	public static Color getForgroundColor(Color backgroundColor)
	{
		double luminocity = Math.sqrt( 
				0.299*Math.pow(backgroundColor.getRed(), 2) + 
				0.587*Math.pow(backgroundColor.getGreen(), 2) + 
				0.114*Math.pow(backgroundColor.getBlue(), 2));
		
		System.out.println(luminocity);
		Color forground = luminocity > 128 ? ColorHelper.SOFT_BLACK : ColorHelper.SOFT_WHITE;
		System.out.println(forground);
		return forground;
	}
}
