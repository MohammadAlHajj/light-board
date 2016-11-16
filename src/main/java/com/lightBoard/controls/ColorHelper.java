/**
 * 
 */
package com.lightBoard.controls;


import javafx.scene.paint.Color;

/**
 * @author Moham
 * Library:
 * Holds Utility functions related to Color
 */
public class ColorHelper
{
	public static final Color SOFT_WHITE = new Color(0.9,0.9,0.9, 1);
	public static final Color SOFT_BLACK = new Color(0.1,0.1,0.1, 1);

	/**
	 * // FIXME: 11/16/2016 test this method
	 * returns an appropriate forground depending on the luminosities of the current forground
	 * and background
	 * @param backgroundColor
	 * @return
	 */
	public static Color getForgroundColor(Color backgroundColor, Color forgroundColor)
	{
		// get luminosities while taking human eye color weights into consideration
		double backLuminosity = Math.sqrt(
			0.299*Math.pow(backgroundColor.getRed(), 2) +
			0.587*Math.pow(backgroundColor.getGreen(), 2) +
			0.114*Math.pow(backgroundColor.getBlue(), 2));

		double frontLuminosity = Math.sqrt(
			0.299*Math.pow(forgroundColor.getRed(), 2) +
			0.587*Math.pow(forgroundColor.getGreen(), 2) +
			0.114*Math.pow(forgroundColor.getBlue(), 2));

		// if luminosity difference is more then half the color spectrum, then return the same
		// forground color
		if (Math.abs(frontLuminosity - backLuminosity) >= 0.5)
			return forgroundColor;
		// else return a default color with luminosity difference of half the color spectrum
		else return backLuminosity > 0.5 ? ColorHelper.SOFT_BLACK : ColorHelper.SOFT_WHITE;
	}
}
