package com.lightBoard.controls;

/**
 * Created by Moham on 11/16/2016.
 */
public class Settings
{
	private static int MIN_SPEED_MICROS = 50;
	private static int MAX_SPEED_MICROS = 3000;
	// FIXME: 11/16/2016 these are not used yet
	private static int MIN_TAIL_LENGTH_PIXELS = 0;
	private static int MAX_TAIL_LENGTH_PIXELS = 0;
	private static int MIN_TAIL_THICKNESS_PIXELS = 0;
	private static int MAX_TAIL_THICKNESS_PIXELS = 0;
	private static long FADE_DELAY_MILLIS = 3000;

	public static int getMinSpeedMicros() {return MIN_SPEED_MICROS;}
	public static int getMaxSpeedMicros() {return MAX_SPEED_MICROS;}
	public static int getMinTailLengthPixels() {return MIN_TAIL_LENGTH_PIXELS;}
	public static int getMaxTailLengthPixels() {return MAX_TAIL_LENGTH_PIXELS;}
	public static int getMinTailThicknessPixels() {return MIN_TAIL_THICKNESS_PIXELS;}
	public static int getMaxTailThicknessPixels() {return MAX_TAIL_THICKNESS_PIXELS;}
	public static long getFadeDelayMillis() {return FADE_DELAY_MILLIS;}

	public static void setMinSpeedMicros(int minSpeedMicros) {MIN_SPEED_MICROS = minSpeedMicros;}
	public static void setMaxSpeedMicros(int maxSpeedMicros) {MAX_SPEED_MICROS = maxSpeedMicros;}
	public static void setMinTailLengthPixels(int minTailLengthPixels) {MIN_TAIL_LENGTH_PIXELS = minTailLengthPixels;}
	public static void setMaxTailLengthPixels(int maxTailLengthPixels) {MAX_TAIL_LENGTH_PIXELS = maxTailLengthPixels;}
	public static void setMinTailThicknessPixels(int minTailThicknessPixels) {MIN_TAIL_THICKNESS_PIXELS = minTailThicknessPixels;}
	public static void setMaxTailThicknessPixels(int maxTailThicknessPixels) {MAX_TAIL_THICKNESS_PIXELS = maxTailThicknessPixels;}
	public static void setFadeDelayMillis(long fadeDelayMillis) {FADE_DELAY_MILLIS = fadeDelayMillis;}
}
