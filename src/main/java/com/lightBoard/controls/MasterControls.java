package com.lightBoard.controls;
import java.awt.Point;
import java.util.LinkedList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lightBoard.controls.userProfiles.PatientProfile;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * @author Moham
 * Master Controls singleton
 */
public enum MasterControls
{
    INSTANCE;

	private PatientProfile patientProfile = PatientProfile.defaultProfile();

	private int repeatDelay = 1000;
	private int maxBufferSize = 300;
	private float brushSize = 5;
	private double smoothness = 0.005;
	private int pointsPerFrame = 2;
	private double timeInFunc;
    private boolean playing = true;
	private boolean extendedMode = false;
	private boolean bypassColorCorrection = false;

    private LinkedList<Point> buffer = new LinkedList<>();

    private Canvas canvas;
    private Color patternColor = patientProfile.getPatternColor();
	private Color backgroundColor = patientProfile.getBackgroundColor();
	private Pattern pattern = patientProfile.getDefaultPattern();

	private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    private Runnable repeatTask = new Runnable() {
        @Override
        public void run() {
            updateBuffer();
            service.schedule(this, repeatDelay, TimeUnit.MICROSECONDS);
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
	public Color getBackgroundColor() { return backgroundColor; }
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
	public PatientProfile getPatientProfile() {return patientProfile;}
	public void setPatientProfile(PatientProfile patientProfile) {this.patientProfile = patientProfile;}
	public boolean isBypassColorCorrection() {return bypassColorCorrection;}
	public void setBypassColorCorrection(boolean bypassColorCorrection) {this.bypassColorCorrection = bypassColorCorrection;}

	public void setPatternColor(Color patternColor) {
		if(bypassColorCorrection) this.patternColor = patternColor;
		else this.patternColor = ColorHelper.getForegroundColor(backgroundColor, patternColor,
			this.patternColor);
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (!bypassColorCorrection)
			this.patternColor = ColorHelper.getForegroundColor(backgroundColor, patternColor);
	}
}
