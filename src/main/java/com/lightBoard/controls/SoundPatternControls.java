package com.lightBoard.controls;

import com.lightBoard.model.Settings;
import com.lightBoard.utils.FileLoader;

import java.io.File;

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
	/**
	 * this class is to enable binding of the media name with the UI directly
	 */
	public class MediaWithNameProperty extends SimpleObjectProperty<Media>
	{
		/**
		 * holds the name of the media
		 */
		private SimpleStringProperty mediaNameProperty = new SimpleStringProperty();
		/**
		 * sets the new sound to be played and updates the media name accordingly
		 * @param newValue new sound
		 */
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

	// state holders
	private boolean playingSound = false;
	private boolean swingingSound = false;

	// media nd media player
	private MediaWithNameProperty patternSoundProperty = new MediaWithNameProperty();
	private MediaPlayer mediaPlayer;

	// path to media
	private String patternSoundUrl = Settings.DEFAULT_AUDIO_FILE;

	/**
	 * useful so not to calculate these at every call to {@link #updateSoundBalance(double)}
	 */
	private static final double RADIANCE_FULL_CYCLE = Math.PI * 2;
	private static final double RADIANCE_QUARTER_CYCLE = Math.PI / 2;
	private static final double RADIANCE_THREE_QUARTERS_CYCLE = Math.PI * 3.0/2;

	/**
	 * this method will update the sound balance depending on the time frame in the current
	 * pattern cycle
	 * @param currentTimeInCycle the current time of the latest cycle of the pattern
	 */
	public void updateSoundBalance(double currentTimeInCycle) {
		// control the balance of the sound
		if(playingSound && mediaPlayer != null)
		{
			// 0 <= balance < 4. balance represents the cycle phase.
			double balance = currentTimeInCycle % RADIANCE_FULL_CYCLE / RADIANCE_QUARTER_CYCLE;
			boolean firstHalfCycle = balance < 2;

			if(swingingSound)
				mediaPlayer.setBalance(firstHalfCycle ? 1-balance : balance-3);
			else
				mediaPlayer.setBalance(balance > 1 && balance < 3? -1 : 1);
		}
	}

	/**
	 * setup sound and its player. it starts playing if {@link #playingSound} is true
	 */
	public void setupSound()
	{
		patternSoundProperty.setValue(new Media(FileLoader.getFilePath(patternSoundUrl)));
		if (mediaPlayer != null)
			mediaPlayer.stop();
		mediaPlayer = new MediaPlayer(patternSoundProperty.getValue());
		if (playingSound)
			playSound();
	}

	public void playSound() {
		playingSound = true;
		if (patternSoundProperty.getValue() != null && mediaPlayer!= null)
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

	/**
	 * updates the pattern sound
	 * @param url the file path of the new sound
	 */
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
