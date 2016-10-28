package com.lightBoard.controls;
import java.awt.Point;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lightBoard.controls.patterns.InfinityPattern;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 */

/**
 * @author Moham
 * Master Controls singleton
 */
public enum MasterControls
{
    INSTANCE;

	private int repeatDelay = 200;
	private int maxBufferSize = 300;
	private float brushSize = 5;
	private double smoothness = 0.005;
	private int pointsPerFrame = 2;
	private double timeInFunc;

    private LinkedList<Point> buffer = new LinkedList<>();

    private Canvas canvas;
    private Color backColor = new Color(0, 0, 0, 1);
	private Color patternColor = new Color(0.8, 0.8, 0.8, 1);

	private Pattern pattern = new InfinityPattern();

	private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    private Runnable repeatTask = new Runnable() {
        @Override
        public void run() {
            try{
                updateBuffer();
            } finally {
                service.schedule(this, repeatDelay, TimeUnit.MICROSECONDS);
            }
        }
    };

    public LinkedList<Point> updateBuffer()
    {
	    for (int i = 0; i < pointsPerFrame; i++)
	    {
		    timeInFunc += smoothness;
		    buffer.addFirst(
			    pattern.getPointAt((int) canvas.getWidth(), (int) canvas.getHeight(), timeInFunc));
	    }
        while (buffer.size() > maxBufferSize)
            buffer.removeLast();
        return buffer;
    }



	public void startDrawing(Canvas canvas) {
        this.canvas = canvas;
        service.schedule(repeatTask, 0L, TimeUnit.MILLISECONDS);
	}

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }
	public int getMaxBufferSize() { return maxBufferSize; }
	public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
	public Color getBackColor() { return backColor; }
	public void setBackColor(Color backColor) { this.backColor = backColor; }
	public Color getPatternColor() { return patternColor; }
	public void setPatternColor(Color patternColor) { this.patternColor = patternColor; }
	public int getRepeatDelay() { return repeatDelay; }
	public double getSmoothness() { return smoothness; }
	public void setSmoothness(double smoothness) { this.smoothness = smoothness; }
	public float getBrushSize() { return brushSize; }
	public void setBrushSize(float brushSize) { this.brushSize = brushSize; }
    public LinkedList<Point> getBuffer() { return buffer; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
}
