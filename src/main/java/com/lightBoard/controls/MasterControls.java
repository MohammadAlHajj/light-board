package com.lightBoard.controls;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

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

	private int repeatDelay = 5;
	private int maxBufferSize = 300;
	private float brushSize = 5;
	private double smoothness = 0.002;
	private double timeInFunc;

    private LinkedList<Point> buffer = new LinkedList<>();

    private Canvas canvas;
    private Color backColor = new Color(0, 0, 0, 1);
	private Color patternColor = new Color(0.8, 0.8, 0.8, 1);

	private Pattern pattern = new InfinityPattern();

	private Timer timer = new Timer();
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run(){
			updateBuffer();
		}
	};





    public LinkedList<Point> updateBuffer()
    {
        timeInFunc += smoothness;
        buffer.addFirst(pattern.getPointAt((int) canvas.getWidth(), (int) canvas.getHeight(), timeInFunc));
        if (buffer.size() > maxBufferSize)
            buffer.removeLast();
        return buffer;
    }



	public void startDrawing(Canvas canvas)
	{
        this.canvas = canvas;
		timer.schedule(timerTask, 0, repeatDelay);
	}



	public int getMaxBufferSize() { return maxBufferSize; }
	public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
	public Color getBackColor() { return backColor; }
	public void setBackColor(Color backColor) { this.backColor = backColor; }
	public Color getPatternColor() { return patternColor; }
	public void setPatternColor(Color patternColor) { this.patternColor = patternColor; }
	public int getRepeatDelay() { return repeatDelay; }
	public void setRepeatDelay(int repeatDelay) { this.repeatDelay = repeatDelay; }
	public double getSmoothness() { return smoothness; }
	public void setSmoothness(double smoothness) { this.smoothness = smoothness; }
	public float getBrushSize() { return brushSize; }
	public void setBrushSize(float brushSize) { this.brushSize = brushSize; }
    public LinkedList<Point> getBuffer() { return buffer; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
}
