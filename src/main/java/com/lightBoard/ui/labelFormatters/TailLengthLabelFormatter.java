package com.lightBoard.ui.labelFormatters;

import com.lightBoard.controls.MasterControls;

import javafx.util.StringConverter;

/**
 * Created by Moham on 10/28/2016.
 */
public class TailLengthLabelFormatter extends StringConverter<Double>
{
	private final String MIN;
	private final String MAX;
	private double maxValue;

	public TailLengthLabelFormatter(String minValueString, String maxValueString, double maxValue)
	{
		MIN = minValueString;
		MAX = maxValueString;
		this.maxValue = maxValue;
	}

	@Override
	public String toString(Double n) {
		if (n.intValue() < maxValue/2) return MIN;
		else return MAX;
	}

	@Override
	public Double fromString(String s) {
		return null;
	}
}
