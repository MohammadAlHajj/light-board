package com.lightBoard.model;

/**
 * Created by Moham on 11/16/2016.
 */
public class Settings
{
	public static final int N_OF_DELTAS_IN_HALF_CYCLE = 750;
	/**
	 * difference between x instances where f(x) = current dot location in pattern.
	 * The cycle takes Math.PI to complete
	 */
	public static final double DEFAULT_PATTERN_SMOOTHNESS = Math.PI / N_OF_DELTAS_IN_HALF_CYCLE;
	// the weird ratio is because the smoothness has been changed from 0.05 to be divisible by
	// Math.PI. this keeps the speed equal to the previously agreed upon requirements
	private static int maxSpeedMicros = (int)(150 / (0.005 / DEFAULT_PATTERN_SMOOTHNESS));
	private static int minSpeedMicros = (int)(1000 / (0.005 / DEFAULT_PATTERN_SMOOTHNESS));
	public static final String DEFAULT_AUDIO_DIR = "/sound/pattern_sounds/";
	public static final String DEFAULT_AUDIO_FILE = "/sound/pattern_sounds/sound.m4a";
	public static final String DEFAULT_IMAGE_DIR = "/images/pattern_images/";

	// TODO: 11/16/2016 these are not used yet
	private static int minTailLengthPixels = 0;
	private static int maxTailLengthPixels = 0;
	private static int minTailThicknessPixels = 0;
	private static int maxTailThicknessPixels = 0;

	private static long fadeDelayMillis = 2000;
	private static long fadeLengthMillis = 500;

	private static int tooltipFontSize = 18;

	public static int getMaxSpeedMicros() {return maxSpeedMicros;}
	public static int getMinSpeedMicros() {return minSpeedMicros;}
	public static int getMinTailLengthPixels() {return minTailLengthPixels;}
	public static int getMaxTailLengthPixels() {return maxTailLengthPixels;}
	public static int getMinTailThicknessPixels() {return minTailThicknessPixels;}
	public static int getMaxTailThicknessPixels() {return maxTailThicknessPixels;}
	public static long getFadeDelayMillis() {return fadeDelayMillis;}
	public static long getFadeLengthMillis() {return fadeLengthMillis;}
	public static int getTooltipFontSize() {return tooltipFontSize;}



	public static void setMaxSpeedMicros(int maxSpeedMicros) {
		Settings.maxSpeedMicros = maxSpeedMicros;}
	public static void setMinSpeedMicros(int minSpeedMicros) {
		Settings.minSpeedMicros = minSpeedMicros;}
	public static void setMinTailLengthPixels(int minTailLengthPixels) {
		Settings.minTailLengthPixels = minTailLengthPixels;}
	public static void setMaxTailLengthPixels(int maxTailLengthPixels) {
		Settings.maxTailLengthPixels = maxTailLengthPixels;}
	public static void setMinTailThicknessPixels(int minTailThicknessPixels) {
		Settings.minTailThicknessPixels = minTailThicknessPixels;}
	public static void setMaxTailThicknessPixels(int maxTailThicknessPixels) {
		Settings.maxTailThicknessPixels = maxTailThicknessPixels;}
	public static void setFadeDelayMillis(long fadeDelayMillis) {
		Settings.fadeDelayMillis = fadeDelayMillis;}
	public static void setFadeLengthMillis(long fadeLengthMillis) {
		Settings.fadeLengthMillis = fadeLengthMillis;}
	public static void setTooltipFontSize(int tooltipFontSize) {
		Settings.tooltipFontSize = tooltipFontSize;}
}
