package com.lightBoard.controls;
import java.awt.Point;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

	private PatientProfile patientProfile = PatientProfile.defaultProfile();

	// speed controls

	public static final int N_OF_DOTS_IN_HALF_CYCLE = 750;
	/**
	 * difference between time instances where f(time) = current dot location in pattern.
	 * The cycle takes time = 2 * Math.PI to complete
	 */
	public static final double DEFAULT_SMOOTHNESS = Math.PI / N_OF_DOTS_IN_HALF_CYCLE;
	private int repeatDelay = Settings.getMinSpeedMicros();
	private double smoothness = DEFAULT_SMOOTHNESS;
	private int pointsPerFrame = 1;
	private double currentTimeInCycle;

	// pattern length and width
	private int maxBufferSize = 300;
	private float brushSize = 5;

	// state holders
    private boolean playing = true;
    private boolean playSound = false;
	private boolean extendedMode = false;
	private boolean bypassColorCorrection = false;

	private String defaultSoundRoot = Settings.DEFAULT_AUDIO_DIR;

	SoundPatternControls soundControls = new SoundPatternControls();

	// image properties
	private String patternImageUrl = null;
	private SimpleObjectProperty<Image> patternImageProperty = new SimpleObjectProperty<>();
	private String defaultImageRoot = Settings.DEFAULT_IMAGE_DIR;
	private double imageSize = 100;

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
            updateEverything();
            // the repetition of this runnable should be is always above 500 Microseconds
	        pointsPerFrame = Math.max(1, 1000 / repeatDelay);
            service.schedule(this, repeatDelay * pointsPerFrame, TimeUnit.MICROSECONDS);
        }
    };


	/**
	 * according to the requirements, the pattern should only stop at the center of the screen (when
	 * possible) when the user presses pause, this boolean will be set to true and thus will tell
	 * {@link #updateEverything()} to stop when it reaches the next center of the screen and call
	 * {@link #pauseContinued(double)}. the double is basically the direction of the pattern when
	 * it wants to continue
	 */
	private boolean pausing;
	private static final double RADIANCE_FULL_CYCLE = Math.PI * 2;
	private static final double RADIANCE_QUARTER_CYCLE = Math.PI / 2;
	private static final double RADIANCE_THREE_QUARTERS_CYCLE = Math.PI * 3.0/2;
	/**
	 * updates all elements relevant to pattern (visual, sound) that change with the time in the
	 * current cycle ({@link #currentTimeInCycle})
	 */
	public void updateEverything()
    {
	    long start = System.nanoTime();

	    for (int i = 0; i < pointsPerFrame; i++)
	    {
	    	// this piece of code continues providing new points in the pattern until the
		    // pattern reaches the middle of the board
		    if(pausing){
		    	if((currentTimeInCycle + RADIANCE_QUARTER_CYCLE) % (RADIANCE_FULL_CYCLE) < DEFAULT_SMOOTHNESS)
				    pauseContinued(RADIANCE_THREE_QUARTERS_CYCLE);
		        else if((currentTimeInCycle + RADIANCE_THREE_QUARTERS_CYCLE) % (RADIANCE_FULL_CYCLE) < DEFAULT_SMOOTHNESS)
				    pauseContinued(RADIANCE_QUARTER_CYCLE);
			}
			soundControls.updateSoundBalance(currentTimeInCycle);

			addPointToVisualPattern();
	    }

	    // clean the old excess points that are present for some reason
        while (buffer.size() > maxBufferSize)
            buffer.removeLast();

//	    System.out.println(System.nanoTime() - start);
    }

	/**
	 * adds a point to the visual buffer
	 */
	private void addPointToVisualPattern()
	{
		currentTimeInCycle += smoothness;

		Point p;
		// if i have a
		if (patternImageProperty.get() != null)
		{
			p = pattern.getPointAt(
				(int) (canvas.getWidth() - imageSize - brushSize),
				(int) (canvas.getHeight() - imageSize - brushSize),
					currentTimeInCycle);
			p.x += imageSize / 2;
			p.y += imageSize / 2;
		}
		else
			p = pattern.getPointAt(
				(int) (canvas.getWidth() - brushSize),
				(int) (canvas.getHeight() - brushSize),
					currentTimeInCycle);

		p.x += + brushSize/2;
		p.y += + brushSize/2;
		buffer.addFirst(p);
	}



	/**
	 * starts filling the pattern buffer to be drawn by the AnimationTimer in
	 * "{@link MainScreen#startAnimation()}"
	 */
	public void startDrawing() {
        assert canvas != null : "Can't start drawing before setting the canvas.";
        // refresh buffer when canvas size is altered
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> refreshBuffer());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> refreshBuffer());

        // setup sound, start playing if the state says so
        soundControls.setupSound();

        service.schedule(repeatTask, 0L, TimeUnit.MILLISECONDS);
	}



	/**
     * @return new value after toggle after toggle
     */
	public boolean togglePlayPause(){
        if (playing)    pause();
        else            play();

        return playing;
    }

	/**
	 * starts updating everything
	 */
	public void play()
    {
	    if (playSound)
	        soundControls.playSound();

        smoothness = DEFAULT_SMOOTHNESS;
	    pausing = false;
	    playing = true;
    }

    public void pause(){
	    pausing = true;
	    playing = false;
    }

	/**
	 * pause everything related to patterns. it is called after the pause button is pressed and the
	 * visual pattern reached the middle of the screen
	 * @param newTimeInFunc shows the direction that the pattern will continue in when the
	 *                         pattern is played again
	 */
	public void pauseContinued(double newTimeInFunc) {
	    soundControls.pauseSound();
	    smoothness = 0;
	    // go to center of board - requirement
	    currentTimeInCycle = newTimeInFunc;
	    pausing = false;
    }

	/**
	 * recreates the buffer from scratch to mask any artifacts created when modifying variables
	 * relevant in the process of building the pattern (ex. changing screen, and by extension the
	 * canvas, size. changing the image header size...)
	 */
	public void refreshBuffer(){
    	int originalSize = buffer.size();
    	buffer.clear();
	    currentTimeInCycle -= originalSize * smoothness;
    	while (buffer.size() < originalSize)
		    updateEverything();
    }

	/**
	 * loads relevant data from the profile
	 * @param profile the profile to load data from
	 */
	private void loadStateFromProfile(PatientProfile profile) {
		backgroundColor = profile.getBackgroundColor();
		patternColor = profile.getPatternColor();
		setPattern(profile.getDefaultPattern());
		setPatternImageUrl(profile.getImageUrl());
	}

	/**
	 * stores the current state to the profile
	 */
	// TODO: 12/22/2016 keep updating this
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
	public boolean isBypassColorCorrection() {return bypassColorCorrection;}
	public void setBypassColorCorrection(boolean bypassColorCorrection) {this.bypassColorCorrection = bypassColorCorrection;}
	public double getImageSize() {return imageSize;}
	public Image getPatternImage() {return patternImageProperty.getValue();}
	public Property<Image> patternImageProperty(){ return patternImageProperty;}
	public String getDefaultImageRoot() {return defaultImageRoot;}
	public void setDefaultImageRoot(String defaultImageRoot) {this.defaultImageRoot = defaultImageRoot;}
	public SoundPatternControls getSoundControls() { return soundControls; }
	public String getDefaultSoundRoot() {return defaultSoundRoot;}
	public void setDefaultSoundRoot(String defaultSoundRoot) {this.defaultSoundRoot = defaultSoundRoot;}
	public boolean getPlaySound() {return playSound;}
	public void setPlaySound(boolean playSound) {this.playSound = playSound;}

	public void setPatientProfile(PatientProfile patientProfile) {
		this.patientProfile = patientProfile;
		loadStateFromProfile(patientProfile);
	}

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
			patternImageProperty.set(null);
		else patternImageProperty.set(new Image(url));
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

	/**
	 * toggle if the sound should be played when the master controls is playing
	 * @return new value after toggle
	 */
	public boolean togglePlayPauseSound(){
		if (playSound) {
			playSound = false;
			soundControls.pauseSound();
		}
		else {
			playSound = true;
			if (playing)
				soundControls.playSound();
		}

		return playSound;
	}

	/**
	 * utility method to load a file
	 * @param path the absolute or relative file path
	 * @return the loaded file
	 */
	public File loadFile(String path){
		try {
			return new File(getClass().getResource(path).toURI());
		}catch (NullPointerException e) {
			return new File(path);
		}catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getFilePath(String path){
		String absFilepath;
		try {
			new File(getClass().getResource(path).toURI());
			absFilepath = getClass().getResource(path).toURI().toASCIIString();
		}catch (NullPointerException e) {
			new File(path);
			absFilepath = path;
		}catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return absFilepath;
	}
}