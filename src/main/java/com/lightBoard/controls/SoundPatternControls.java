package com.lightBoard.controls;

import com.lightBoard.model.Settings;
import com.lightBoard.utils.FileLoader;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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
	private boolean mediaDone = true;

	// media nd media player
	private MediaWithNameProperty patternSoundProperty = new MediaWithNameProperty();
	private MediaPlayer mediaPlayer;

	// path to media
	private String patternSoundUrl = Settings.DEFAULT_AUDIO_FILE;

	public SoundPatternControls(){
		setupSound();
	}

	/**
	 * useful so not to calculate these at every call to {@link #updateSoundBalance(double)}
	 */
	private static final double RADIANCE_FULL_CYCLE = Math.PI * 2;
	private static final double RADIANCE_QUARTER_CYCLE = Math.PI / 2;
	private static final double RADIANCE_THREE_QUARTERS_CYCLE = Math.PI * 3.0/2;


	private ScheduledThreadPoolExecutor restartSoundService = new ScheduledThreadPoolExecutor(2);
	private Runnable restartSound = new Runnable() {
		@Override
		public void run() {
			mediaPlayer.seek(new Duration(0));
		}
	};

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

			double quarterCycleIndex = Math.floor(balance);

			if ((quarterCycleIndex == 1 | quarterCycleIndex == 3) &&
				quarterCycleIndex != Math.floor(balance - Settings.DEFAULT_PATTERN_SMOOTHNESS))
			{
				if (mediaDone){
					restartSoundService.execute(restartSound);
					mediaDone = false;
				}

				if(!swingingSound)
					mediaPlayer.setBalance(balance > 1 && balance < 3? -1 : 1);

			}
			if(swingingSound)
				mediaPlayer.setBalance(firstHalfCycle ? 1-balance : balance-3);
		}
	}

	/**
	 * setup sound and its player. it starts playing if {@link #playingSound} is true
	 */
	public void setupSound()
	{
//		String filePath = FileLoader.getResourceUrl(patternSoundUrl).toExternalForm();
		Media media = new Media(FileLoader.getExternalUrlString(patternSoundUrl));
		patternSoundProperty.setValue(media);
		if (mediaPlayer != null)
			mediaPlayer.stop();
		mediaPlayer = new MediaPlayer(patternSoundProperty.getValue());
		mediaPlayer.setOnEndOfMedia(() -> mediaDone = true);
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
			this.patternSoundProperty.setValue(new Media(FileLoader.getExternalUrlString(url)));
		setupSound();
	}
}
