package com.lightBoard.controls;
import java.awt.Point;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lightBoard.controls.userProfiles.PatientProfile;
import com.lightBoard.view.MainScreen;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
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
	private double imageSize = 100;
	private Image patternImage = null;
	private SimpleObjectProperty<Image> patternImageProperty =
		new SimpleObjectProperty<>(patternImage);


	private ConcurrentLinkedDeque<Point> buffer = new ConcurrentLinkedDeque<>();

    private Canvas canvas;
    private Color patternColor = patientProfile.getPatternColor();
	private Color backgroundColor = patientProfile.getBackgroundColor();
	private Pattern pattern = patientProfile.getDefaultPattern();

	private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    private Runnable repeatTask = new Runnable() {
        @Override
        public void run() {
            updateBuffer();
	        pointsPerFrame = Math.max(1, 1000 / repeatDelay);
            service.schedule(this, repeatDelay * pointsPerFrame, TimeUnit.MICROSECONDS);
        }
    };

	/**
	 * updates the pattern buffer accordingly. takes into consideration the bounds of the canvas,
	 * the brush size, and the image size
	 * @return
	 */
	public ConcurrentLinkedDeque<Point> updateBuffer()
    {
	    for (int i = 0; i < pointsPerFrame; i++)
	    {
		    timeInFunc += smoothness;
		    Point p;
		    if (patternImage != null){
		    	p = pattern.getPointAt(
		    		(int) (canvas.getWidth() - imageSize - brushSize),
				    (int) (canvas.getHeight() - imageSize - brushSize),
				    timeInFunc);
		    	p.x += imageSize / 2;
		    	p.y += imageSize / 2;
		    }
		    else
		    	p = pattern.getPointAt(
		    		(int) (canvas.getWidth() - brushSize),
				    (int) (canvas.getHeight() - brushSize),
				    timeInFunc);

		    p.x += + brushSize/2;
		    p.y += + brushSize/2;
		    buffer.addFirst(p);
	    }

        while (buffer.size() > maxBufferSize)
            buffer.removeLast();
        return buffer;
    }

	/**
	 * starts filling the pattern buffer to be drawn by the AnimationTimer in
	 * "{@link MainScreen#startAnimation()}"
	 */
	public void startDrawing() {
        assert canvas != null : "Can't start drawing before setting the canvas.";
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> refreshBuffer());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> refreshBuffer());
        service.schedule(repeatTask, 0L, TimeUnit.MILLISECONDS);
	}

    /**
     * @return true if playing after toggle
     */
	public boolean togglePlayPause(){
        if (playing)    pause();
        else            play();

        return playing;
    }

    public void play(){
        smoothness = 0.005;
        playing = true;
        refreshBuffer();
    }

    public void pause(){
        smoothness = 0;
        playing = false;
	    refreshBuffer();
    }

    public void refreshBuffer(){
    	int originalSize = buffer.size();
    	buffer.clear();
	    timeInFunc -= originalSize * smoothness;
    	while (buffer.size() < originalSize)
		    updateBuffer();
    }

    public void clearBuffer(){
    	buffer.clear();
    }

	public void setRepeatDelay(int repeatDelay) {this.repeatDelay = repeatDelay;}
	public int getMaxBufferSize() { return maxBufferSize; }
	public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
	public Color getBackgroundColor() { return backgroundColor; }
	public Color getPatternColor() { return patternColor; }
	public int getRepeatDelay() { return repeatDelay; }
	public double getSmoothness() { return smoothness; }
	public void setSmoothness(double smoothness) { this.smoothness = smoothness; }
	public float getBrushSize() { return brushSize; }
    public ConcurrentLinkedDeque<Point> getBuffer() { return buffer; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
	public boolean isExtendedMode() { return extendedMode; }
	public Canvas getCanvas() { return canvas; }
	public void setCanvas(Canvas canvas) { this.canvas = canvas; }
    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }
	public PatientProfile getPatientProfile() {return patientProfile;}
	public void setPatientProfile(PatientProfile patientProfile) {this.patientProfile = patientProfile;}
	public boolean isBypassColorCorrection() {return bypassColorCorrection;}
	public void setBypassColorCorrection(boolean bypassColorCorrection) {this.bypassColorCorrection = bypassColorCorrection;}
	public double getImageSize() {return imageSize;}
	public Image getPatternImage() {return patternImage;}
	public Property<Image> patternImageProperty(){ return patternImageProperty;}

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

	public void setPatternImage(Image patternImage) {
		this.patternImage = patternImage;
		patternImageProperty.set(patternImage);
		refreshBuffer();
	}

	public void setExtendedMode(boolean extendedMode) {
		this.extendedMode = extendedMode;
		refreshBuffer();
	}

	public void setBrushSize(float brushSize) {
		this.brushSize = brushSize;
		refreshBuffer();
	}

	public void setImageSize(double imageSize) {
		this.imageSize = imageSize;
		refreshBuffer();
	}

}
