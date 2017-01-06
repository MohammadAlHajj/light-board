package com.lightBoard.controls;

import com.sun.istack.internal.NotNull;

import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedDeque;

import javafx.scene.canvas.Canvas;

/**
 * Created by Moham on 1/4/2017.
 */
public class VisualPatternControl
{
	// pattern length and width
	private int maxBufferSize = 300;
	private float brushSize = 5;
	private VisualPattern pattern;
	private Canvas canvas;

	public VisualPatternControl(VisualPattern visualPattern){
		this.pattern = visualPattern;
	}

	/**
	 * visual pattern points holder...thread safe
	 */
	private ConcurrentLinkedDeque<Point> buffer = new ConcurrentLinkedDeque<>();

	/**
	 * adds a new point to {@link #buffer} at the specified time In cycle
	 * @param timeInCycle the x in f(x) where 0 < x < 2 * PI
	 * @param hasImage if the patterns has an image. this is to take the image's size into
	 *                    consideration so as to not go out of the canvas' borders
	 * @param imageSize the image size. this value is not used if hasImage is false
	 */
	public void addPointToVisualPattern(double timeInCycle, boolean hasImage, double
		imageSize)
	{
		Point p;
		// if i have a
		if (hasImage)
		{
			p = pattern.getPointAt(
				(int) (canvas.getWidth() - imageSize - brushSize),
				(int) (canvas.getHeight() - imageSize - brushSize),
				timeInCycle);
			p.x += imageSize / 2;
			p.y += imageSize / 2;
		}
		else
			p = pattern.getPointAt(
				(int) (canvas.getWidth() - brushSize),
				(int) (canvas.getHeight() - brushSize),
				timeInCycle);

		p.x += brushSize / 2;
		p.y += brushSize / 2;
		buffer.addFirst(p);
	}

	/**
	 * clean the old excess points that are present for some reason
	 */
	public void trimExcessTailPoints() {
		while (buffer.size() > maxBufferSize)
			buffer.removeLast();
	}

	/**
	 * recreates the buffer from scratch to mask any artifacts created when modifying variables
	 * relevant in the process of building the pattern (ex. changing screen, and by extension the
	 * canvas, size. changing the image header size...)
	 */
	public void refreshPattern(double currentTimeInCycle, double deltaValue, boolean hasImage,
		double imageSize)
	{
		int originalSize = buffer.size();
		buffer.clear();
		currentTimeInCycle -= originalSize * deltaValue;
		currentTimeInCycle %= 2 * Math.PI;

		while (buffer.size() < originalSize) {
			currentTimeInCycle += deltaValue;
			addPointToVisualPattern(currentTimeInCycle, hasImage, imageSize);
		}
	}

	public int getMaxBufferSize() { return maxBufferSize; }
	public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
	public void clearBuffer(){ buffer.clear(); }
	public float getBrushSize() { return brushSize; }
	public ConcurrentLinkedDeque<Point> getBuffer() { return buffer; }
	public void setPattern(VisualPattern pattern) { this.pattern = pattern; }
	public VisualPattern getPattern() { return pattern; }
	public Canvas getCanvas() { return canvas; }

	public void setCanvas(@NotNull Canvas canvas) {
		assert canvas != null : "Canvas can't be null.";
		// refresh buffer when canvas size is altered
		this.canvas = canvas;
		this.canvas.heightProperty().addListener((observable, oldValue, newValue) ->
			MasterControls.INSTANCE.refreshVisualPattern());
		this.canvas.widthProperty().addListener((observable, oldValue, newValue) ->
			MasterControls.INSTANCE.refreshVisualPattern());
	}

	public void setBrushSize(float brushSize) {
		this.brushSize = brushSize;
		MasterControls.INSTANCE.refreshVisualPattern();
	}
}
