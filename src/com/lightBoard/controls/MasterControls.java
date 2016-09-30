package com.lightBoard.controls;
import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.lightBoard.controls.patterns.InfinityPattern;

/**
 * 
 */

/**
 * @author Moham
 *
 */
public class MasterControls
{
	private int repeatDelay = 2;
	private int bufferSize = 300;
	private float brushSize = 5;
	private double smoothness = 0.002;
	private double timeInFunc;

	private LinkedList<Point> buffer = new LinkedList<>();
	
	private DrawingPanel drawingPanel;
	private Color backColor = new Color(0, 0, 0);
	private Color PatternColor = new Color(204,204,204);
	
	private Pattern pattern = new InfinityPattern();
	
	private Timer timer = new Timer();
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run(){
			drawingPanel.repaint();
			timeInFunc += smoothness;
		}
	};
	
	public LinkedList<Point> getUpdatedBuffer(DrawingPanel drawingPanel)
	{
		buffer.addFirst(
			pattern.getPointAt(
				drawingPanel.getWidth(), 
				drawingPanel.getHeight(), 
				timeInFunc));
		
		return buffer;
	}
	

	
	public void startDrawing(DrawingPanel drawingPanel)
	{
		if (drawingPanel == null)
			this.drawingPanel = drawingPanel;
		
		timer.schedule(timerTask, 0, repeatDelay);
	}
	


	public int getBufferSize() { return bufferSize; }
	public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }
	public Color getBackColor() { return backColor; }
	public void setBackColor(Color backColor) { this.backColor = backColor; }
	public Color getPatternColor() { return PatternColor; }
	public void setPatternColor(Color patternColor) { PatternColor = patternColor; }
	public int getRepeatDelay() { return repeatDelay; }
	public void setRepeatDelay(int repeatDelay) { this.repeatDelay = repeatDelay; }
	public double getSmoothness() { return smoothness; }
	public void setSmoothness(double smoothness) { this.smoothness = smoothness; }
	public float getBrushSize() { return brushSize; }
	public void setBrushSize(float brushSize) { this.brushSize = brushSize; }



	


	
}
