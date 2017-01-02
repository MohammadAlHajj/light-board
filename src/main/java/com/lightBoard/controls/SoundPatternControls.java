package com.lightBoard.controls;

import com.lightBoard.model.Settings;

import java.io.File;
import java.net.URISyntaxException;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Created by Mohammad on 12/31/2016.
 */
public class SoundPatternControls
{
	public class MediaWithNameProperty extends SimpleObjectProperty<Media> {
		private SimpleStringProperty mediaNameProperty = new SimpleStringProperty();

		@Override
		public void set(Media newValue) {
			super.set(newValue);
			String rawName = new File(newValue.getSource()).getName();
			rawName = rawName.replaceAll("%20"," ");
			String cleanName = rawName.substring(0, rawName.lastIndexOf('.'));
			mediaNameProperty.set(cleanName);
		}
		public ReadOnlyStringProperty getMediaNameProperty() {
			return mediaNameProperty;
		}
	}

	// sound properties
	private boolean playingSound = false;
	private boolean swingingSound = false;
	private MediaWithNameProperty patternSoundProperty = new MediaWithNameProperty();
	private MediaPlayer mediaPlayer;
	private String patternSoundUrl = null;
	private String defaultSoundRoot = Settings.DEFAULT_AUDIO_DIR;


	private static final double RADIANCE_FULL_CYCLE = Math.PI * 2;
	private static final double RADIANCE_QUARTER_CYCLE = Math.PI / 2;
	private static final double RADIANCE_THREE_QUARTERS_CYCLE = Math.PI * 3.0/2;

	public void updateSound(double currentTimeInCycle) {
		// control the balance of the sound
		if(playingSound && mediaPlayer != null)
		{
			double balance = currentTimeInCycle % RADIANCE_FULL_CYCLE / RADIANCE_QUARTER_CYCLE;
			boolean firstHalfCycle = balance < 2;

			if(swingingSound)
				mediaPlayer.setBalance(firstHalfCycle ? 1-balance : balance-3);
			else
				mediaPlayer.setBalance(firstHalfCycle ? -1 : 1);
		}
	}

	/**
	 * setup sound and its player. it starts playing if {@link #playingSound} is true
	 */
	private void setupSound() {
		if (patternSoundProperty.getValue() == null){
			try {
				patternSoundProperty.setValue(new Media(getClass().getResource(
						"/sound/pattern_sounds/sound.m4a").toURI().toString()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		else patternSoundProperty.setValue(new Media(patternSoundUrl));
		if (mediaPlayer != null)
			mediaPlayer.stop();
		mediaPlayer = new MediaPlayer(patternSoundProperty.getValue());
		if (playingSound)
			playSound();
	}

	/**
	 * @return new value after toggle after toggle
	 */
	public boolean togglePlayPauseSound(){
		if (playingSound)   pauseSound();
		else                playSound();

		return playingSound;
	}

	public void playSound() {
		playingSound = true;
		if (MasterControls.INSTANCE.isPlaying() && patternSoundProperty.getValue() != null && mediaPlayer!= null)
			mediaPlayer.play();

	}
	public void pauseSound() {
		playingSound = false;
		if (patternSoundProperty.getValue() != null && mediaPlayer!= null)
			mediaPlayer.pause();
	}

	public boolean isSwingingSound() {return swingingSound;}
	public void setSwingingSound(boolean swingingSound) {this.swingingSound = swingingSound;}
	public MediaWithNameProperty patternSoundProperty() { return patternSoundProperty;}
	public String getPatternSoundUrl() {return patternSoundUrl;}
	public boolean isPlayingSound() { return playingSound; }
	public void setPlayingSound(boolean playingSound) {
		this.playingSound = playingSound;
	}
	public String getDefaultSoundRoot() {return defaultSoundRoot;}
	public void setDefaultSoundRoot(String defaultSoundRoot) {this.defaultSoundRoot = defaultSoundRoot;}

	public void setPatternSoundUrl(String url)
	{
		this.patternSoundUrl = url;
		if (url == null || url.isEmpty())
			this.patternSoundProperty.setValue(null);
		else
			this.patternSoundProperty.setValue(new Media(url));
		setupSound();
	}
}
