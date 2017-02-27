package com.lightBoard.controls;

import com.lightBoard.model.PatientProfile;
import com.lightBoard.model.Settings;
import com.lightBoard.utils.ColorHelper;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import static com.lightBoard.model.Settings.DEFAULT_PATTERN_SMOOTHNESS;

/**
 * @author Moham
 * Master Controls singleton
 */
public enum MasterControls
{
	/**
	 * the enum type in java is very good as a base for a singleton since all characteristics of
	 * an enum (as a singleton) are enforced by the JVM
	 * {@link #INSTANCE} is like calling SingletonClass.getInstance() in a conventional singleton
	 * class
	 */
    INSTANCE;

	/**
	 * profile holding patient specific settings. It is currently defaulted to the default profile
	 */
	private PatientProfile patientProfile = PatientProfile.defaultProfile();

	// speed controls
	private int updatePatternsRunnableRepeatDelay = Settings.getMinSpeedMicros();
	private double smoothness = DEFAULT_PATTERN_SMOOTHNESS;
	private int pointsPerUpdatePatternsRunnable = 1;

	private double currentTimeInCycle;

	// state holders
    private boolean playing = true;
    private boolean playSound = false;
	private boolean extendedMode = false;
	private boolean bypassColorCorrection = false;

	// default file chooser locations when picking image and audio files. these change every time
	// the user picks a new file
	private String defaultSoundRoot = Settings.DEFAULT_AUDIO_DIR;
	private String defaultImageRoot = Settings.DEFAULT_IMAGE_DIR;

	// the different pattern controls
	private SoundPatternControls soundControls = new SoundPatternControls();
	private VisualPatternControl visualControl = new VisualPatternControl(
		patientProfile.getDefaultVisualPattern());

	// image properties
	private String patternImageUrl = null;
	private SimpleObjectProperty<Image> patternImageProperty = new SimpleObjectProperty<>();
	private double imageSize = 100;

	// color properties
	private Color patternColor = patientProfile.getPatternColor();
	private Color backgroundColor = patientProfile.getBackgroundColor();

	MasterControls(){

	}

	// pattern filling process
	private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1);
    private Runnable updatePatternsRunnable = new Runnable() {
        @Override
        public void run() {
            updateAudioVisualPatterns();
            // the repetition of this runnable should be is always above 500 Microseconds
	        pointsPerUpdatePatternsRunnable = Math.max(1, 1000 / updatePatternsRunnableRepeatDelay);
            service.schedule(this,
	            updatePatternsRunnableRepeatDelay * pointsPerUpdatePatternsRunnable,
	            TimeUnit.MICROSECONDS);
        }
    };


	/**
	 * according to the requirements, the pattern should only stop at the center of the screen (when
	 * possible) when the user presses pause, this boolean will be set to true and thus will tell
	 * {@link #updateAudioVisualPatterns()} to stop when it reaches the next center of the screen and call
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
	public void updateAudioVisualPatterns()
    {
	    long start = System.nanoTime();

	    for (int i = 0; i < pointsPerUpdatePatternsRunnable; i++)
	    {
		    currentTimeInCycle %= RADIANCE_FULL_CYCLE;
		    // this piece of code blocks pausing until the pattern reaches the middle of the canvas
		    if(pausing){
		    	if(Math.abs(currentTimeInCycle - RADIANCE_QUARTER_CYCLE) <=
				    DEFAULT_PATTERN_SMOOTHNESS)
			    {
				    pauseContinued(RADIANCE_QUARTER_CYCLE);
			    }
		        else if(Math.abs(currentTimeInCycle - RADIANCE_THREE_QUARTERS_CYCLE) <=
				    DEFAULT_PATTERN_SMOOTHNESS)
			    {
				    pauseContinued(RADIANCE_THREE_QUARTERS_CYCLE);
			    }
			}
		    currentTimeInCycle += smoothness;

		    soundControls.updateSoundBalance(currentTimeInCycle);
			visualControl.addPointToVisualPattern(currentTimeInCycle,
				patternImageProperty.get() != null, imageSize);
	    }
	    visualControl.trimExcessTailPoints();

//	    System.out.println(System.nanoTime() - start);
    }


	/**
	 * starts the pattern updating process. the process will be infinitely repeated until the
	 * program is closed
	 */
	public void startUpdatingPatterns() {
		assert visualControl.getCanvas() != null : "Can't start drawing before setting the canvas.";
        service.schedule(updatePatternsRunnable, 0L, TimeUnit.MILLISECONDS);
	}

	/**
	 * recreates the buffer from scratch to mask any artifacts created when modifying variables
	 * relevant in the process of building the pattern (ex. changing screen, and by extension the
	 * canvas, size. changing the image header size...)
	 */
	public void refreshVisualPattern(){
		if (playing) {
			smoothness = 0;
			visualControl.refreshPattern(currentTimeInCycle, DEFAULT_PATTERN_SMOOTHNESS,
				patternImageProperty.get() != null, imageSize);
			smoothness = DEFAULT_PATTERN_SMOOTHNESS;
		}
		else visualControl.clearBuffer();
	}

	/**
     * @return new value after toggle
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

        smoothness = DEFAULT_PATTERN_SMOOTHNESS;
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
	 * loads relevant data from the profile
	 * @param profile the profile to load data from
	 */
	private void loadStateFromProfile(PatientProfile profile) {
		backgroundColor = profile.getBackgroundColor();
		patternColor = profile.getPatternColor();
		visualControl.setPattern(profile.getDefaultVisualPattern());
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
				.defaultPattern(visualControl.getPattern())
				.firstName(patientProfile.getFirstName())
				.lastName(patientProfile.getLastName())
				.imageUrl(patternImageUrl)
				.build();
	}


	public void setUpdatePatternsRunnableRepeatDelay(int updatePatternsRunnableRepeatDelay) {this.updatePatternsRunnableRepeatDelay = updatePatternsRunnableRepeatDelay;}
	public int getUpdatePatternsRunnableRepeatDelay() { return updatePatternsRunnableRepeatDelay; }
	public Color getBackgroundColor() { return backgroundColor; }
	public Color getPatternColor() { return patternColor; }
	public boolean isExtendedMode() { return extendedMode; }
    public boolean isPlaying() { return playing; }
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
	public VisualPatternControl getVisualControl() { return visualControl; }

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
		if (url == null || url.isEmpty()) patternImageProperty.set(null);
		else                              patternImageProperty.set(new Image(url));
		refreshVisualPattern();
	}

	public void setExtendedMode(boolean extendedMode) {
		this.extendedMode = extendedMode;
		refreshVisualPattern();
	}

	public void setImageSize(double imageSize) {
		this.imageSize = imageSize;
		refreshVisualPattern();
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


}