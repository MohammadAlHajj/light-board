package com.lightBoard.controls;
import java.awt.Point;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lightBoard.controls.patterns.InfinityPattern;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import sun.plugin2.util.ColorUtil;

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

	private int repeatDelay = 1000;
	private int maxBufferSize = 300;
	private float brushSize = 5;
	private double smoothness = 0.005;
	private int pointsPerFrame = 2;
	private double timeInFunc;
    private boolean playing = true;
	private boolean extendedMode = false;

    private LinkedList<Point> buffer = new LinkedList<>();

    private Canvas canvas;
    private Color patternColor = new Color(0.0, 0.0, 0.0, 1);
	private Color backColor = new Color(0.8, 0.8, 0.8, 1);

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

	public void startDrawing() {
        assert canvas != null : "Can't start drawing before setting the canvas.";
        service.schedule(repeatTask, 0L, TimeUnit.MILLISECONDS);
	}

    /**
     * @return true if playing after toggle
     */
	public boolean togglePlayPause(){
        if (playing){
            pause();
        } else {
            play();
        }
        return playing;
    }
    public void play(){
        smoothness = 0.005;
        playing = true;
    }
    public void pause(){
        smoothness = 0;
        playing = false;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }
	public int getMaxBufferSize() { return maxBufferSize; }
	public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
	public Color getBackColor() { return backColor; }
	public Color getPatternColor() { return patternColor; }
	public int getRepeatDelay() { return repeatDelay; }
	public double getSmoothness() { return smoothness; }
	public void setSmoothness(double smoothness) { this.smoothness = smoothness; }
	public float getBrushSize() { return brushSize; }
	public void setBrushSize(float brushSize) { this.brushSize = brushSize; }
    public LinkedList<Point> getBuffer() { return buffer; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
	public boolean isExtendedMode() { return extendedMode; }
	public void setExtendedMode(boolean extendedMode) { this.extendedMode = extendedMode; }
	public Canvas getCanvas() { return canvas; }
	public void setCanvas(Canvas canvas) { this.canvas = canvas; }
    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }

	public void setPatternColor(Color patternColor) {
		this.patternColor = ColorHelper.getForgroundColor(backColor, patternColor);
	}
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
		this.patternColor = ColorHelper.getForgroundColor(backColor, patternColor);
	}


}
