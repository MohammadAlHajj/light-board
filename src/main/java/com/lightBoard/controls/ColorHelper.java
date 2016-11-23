/**
 * 
 */
package com.lightBoard.controls;


import com.sun.istack.internal.Nullable;

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
	 * NOTE: accepted color means that it's luminosity difference to the background color is
	 * more then half the color spectrum
	 *
	 * returns an accepted foreground color. this method takes the old foreground color
	 * into consideration, {@link #getForegroundColor(Color, Color)} doesn't
	 * @param backgroundColor accepted foreground color
	 * @return
	 */
	public static Color getForegroundColor(Color backgroundColor, Color newForegroundColor,
		Color oldForegroundColor)
	{
		// get luminosities while taking human eye color weights into consideration
		double backLuminosity = Math.sqrt(
			0.299*Math.pow(backgroundColor.getRed(), 2) +
			0.587*Math.pow(backgroundColor.getGreen(), 2) +
			0.114*Math.pow(backgroundColor.getBlue(), 2));

		double newFrontLuminosity = Math.sqrt(
			0.299*Math.pow(newForegroundColor.getRed(), 2) +
			0.587*Math.pow(newForegroundColor.getGreen(), 2) +
			0.114*Math.pow(newForegroundColor.getBlue(), 2));

		double oldFrontLuminosity = Math.sqrt(
			0.299*Math.pow(oldForegroundColor.getRed(), 2) +
			0.587*Math.pow(oldForegroundColor.getGreen(), 2) +
			0.114*Math.pow(oldForegroundColor.getBlue(), 2));

		// NOTE: accepted color means that it's luminosity difference to the background color is
		// more then half the color spectrum
		// if new foreground color is accepted, return it
		if (Math.abs(newFrontLuminosity - backLuminosity) >= 0.5)       return newForegroundColor;
		//else if old foreground color is accepted, return it
		else if (Math.abs(oldFrontLuminosity - backLuminosity) >= 0.5)  return oldForegroundColor;
		// else return a default accepted color
		else return backLuminosity > 0.5 ? ColorHelper.SOFT_BLACK : ColorHelper.SOFT_WHITE;
	}

	/**
	 * NOTE: accepted color means that it's luminosity difference to the background color is
	 * more then half the color spectrum
	 *
	 * returns an accepted foreground color. this method does not take the old foreground color
	 * into consideration and returns a default directly if the passed foreground color is not
	 * accepted. For consideration use {@link #getForegroundColor(Color, Color, Color)}
	 * @param backgroundColor accepted foreground color
	 * @return
	 */
	public static Color getForegroundColor(Color backgroundColor, Color foregroundColor)
	{
		// get luminosities while taking human eye color weights into consideration
		double backLuminosity = Math.sqrt(
			0.299*Math.pow(backgroundColor.getRed(), 2) +
			0.587*Math.pow(backgroundColor.getGreen(), 2) +
			0.114*Math.pow(backgroundColor.getBlue(), 2));

		double newFrontLuminosity = Math.sqrt(
			0.299*Math.pow(foregroundColor.getRed(), 2) +
			0.587*Math.pow(foregroundColor.getGreen(), 2) +
			0.114*Math.pow(foregroundColor.getBlue(), 2));

		// if foreground color is accepted, return it
		if (Math.abs(newFrontLuminosity - backLuminosity) >= 0.5)       return foregroundColor;
		// else return a default accepted color
		else return backLuminosity > 0.5 ? ColorHelper.SOFT_BLACK : ColorHelper.SOFT_WHITE;
	}
}
