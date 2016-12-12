package com.lightBoard.controls;
import java.awt.Point;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lightBoard.model.PropertyBasedInterfaceMarshal;
import com.lightBoard.model.PatientProfile;
import com.lightBoard.model.Settings;
import com.lightBoard.view.MainScreen;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author Moham
 * Master Controls singleton
 *
 */
public enum MasterControls
{
    INSTANCE;


	private Gson gson= new GsonBuilder()
		.registerTypeAdapter(Pattern.class, new PropertyBasedInterfaceMarshal())
		.create();

	private PatientProfile patientProfile = PatientProfile.defaultProfile();

	// speed controls

	public static final int N_OF_DOTS_IN_HALF_CYCLE = 750;
	/**
	 * difference between time instances where f(time) = current dot location in pattern.
	 * The cycle takes time = 2 * Math.PI to complete
	 */
	public static final double DEFAULT_SMOOTHNESS = Math.PI / N_OF_DOTS_IN_HALF_CYCLE;
	private int repeatDelay = Settings.getMaxSpeedMicros();
	private double smoothness = DEFAULT_SMOOTHNESS;
	private int pointsPerFrame = 1;
	private double timeInFunc;

	// pattern length and width
	private int maxBufferSize = 300;
	private float brushSize = 5;

	// state holders
    private boolean playing = true;
	private boolean extendedMode = false;
	private boolean bypassColorCorrection = false;

	// image properties
	private double imageSize = 100;
	private Image patternImage = null;
	private String patternImageUrl = null;
	private SimpleObjectProperty<Image> patternImageProperty =
		new SimpleObjectProperty<>(patternImage);

	/**
	 * pattern points holder...thread safe
	 */
	private ConcurrentLinkedDeque<Point> buffer = new ConcurrentLinkedDeque<>();

    private Canvas canvas;
    private Color patternColor = patientProfile.getPatternColor();
	private Color backgroundColor = patientProfile.getBackgroundColor();
	private Pattern pattern = patientProfile.getDefaultPattern();

	// pattern filling process
	private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    private Runnable repeatTask = new Runnable() {
        @Override
        public void run() {
            updateBuffer();
            // the repetition of this runnable should be is always above 500 Microseconds
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
		    if (patternImage != null)
		    {
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
        smoothness = DEFAULT_SMOOTHNESS;
	    playing = true;
    }

    public void pause(){
        smoothness = 0;
        playing = false;
        // go to center of board - requirement
	    timeInFunc = Math.PI/2;
    }

    public void refreshBuffer(){
    	int originalSize = buffer.size();
    	buffer.clear();
	    timeInFunc -= originalSize * smoothness;
    	while (buffer.size() < originalSize)
		    updateBuffer();
    }

	public void saveProfile() throws IOException {
		updatePatientProfile();
		Writer writer;
		if (patientProfile.isDefault())
			writer = new FileWriter("default.json");
		else writer = new FileWriter(patientProfile.getId() + ".json");

		gson.toJson(patientProfile, writer);
		writer.close();
	}

	public void loadProfile(int id) throws IOException {
		Reader reader;
		if (patientProfile.isDefault())
			reader = new FileReader("default.json");
		else reader = new FileReader(id + ".json");
		patientProfile = gson.fromJson(reader, PatientProfile.class);
		reader.close();

		loadStateFromProfile(patientProfile);
	}

	private void loadStateFromProfile(PatientProfile profile) {
		backgroundColor = profile.getBackgroundColor();
		patternColor = profile.getPatternColor();
		setPattern(profile.getDefaultPattern());
		setPatternImageUrl(profile.getImageUrl());
	}

	private void updatePatientProfile()
	{
		patientProfile = new PatientProfile.Builder()
			.backgroundColor(backgroundColor)
			.patternColor(patternColor)
			.defaultPattern(pattern)
			.firstName(patientProfile.getFirstName())
			.lastName(patientProfile.getLastName())
			.imageUrl(patternImageUrl)
			.build();
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

	public void setPatternImageUrl(String url) {
		this.patternImageUrl = url;
		if (url == null || url.isEmpty())
			this.patternImage = null;
		else this.patternImage = new Image(url);
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
