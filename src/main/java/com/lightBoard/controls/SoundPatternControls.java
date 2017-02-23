package com.lightBoard.controls;

import com.lightBoard.model.Settings;
import com.lightBoard.utils.FileLoader;

import java.io.File;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

/**
 * Created by Mohammad on 12/31/2016.
 */
public class SoundPatternControls
{
	/**
	 * this class is to enable binding of the media name with the UI directly
	 */
	public class AudioClipWithNameProperty extends SimpleObjectProperty<AudioClip>
	{
		/**
		 * holds the name of the media
		 */
		private SimpleStringProperty audioNameProperty = new SimpleStringProperty();
		/**
		 * sets the new sound to be played and updates the media name accordingly
		 * @param newValue new sound
		 */
		@Override
		public void set(AudioClip newValue) {
			super.set(newValue);
			String rawName = new File(newValue.getSource()).getName();
			rawName = rawName.replaceAll("%20"," ");
			String cleanName = rawName.substring(0, rawName.lastIndexOf('.'));
			audioNameProperty.set(cleanName);
		}
		public ReadOnlyStringProperty getAudioNameProperty() {
			return audioNameProperty;
		}
	}

	// state holders
	private boolean playingSound = false;
	private boolean swingingSound = false;
	private boolean mediaDone = true;

	// media nd media player
	private AudioClipWithNameProperty patternSoundProperty = new AudioClipWithNameProperty();

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

	/**
	 * this method will update the sound balance depending on the time frame in the current
	 * pattern cycle
	 * @param currentTimeInCycle the current time of the latest cycle of the pattern
	 */
	public void updateSoundBalance(double currentTimeInCycle) {
		// control the balance of the sound
		if(playingSound && patternSoundProperty.get() != null)
		{
			long b = System.nanoTime();

			// 0 <= balance < 4. balance represents the cycle phase.
			double balance = currentTimeInCycle % RADIANCE_FULL_CYCLE / RADIANCE_QUARTER_CYCLE;
			boolean firstHalfCycle = balance < 2;

			double quarterCycleIndex = Math.floor(balance);

			long a = System.nanoTime();
			if(swingingSound)
				patternSoundProperty.get().setBalance(firstHalfCycle ? 1 - balance : balance - 3);
			else
				patternSoundProperty.get().setBalance(balance > 1 && balance < 3? -1 : 1);
			System.out.println("C   :::   sound balance = " + (System.nanoTime() - a));

			if ((quarterCycleIndex == 1 | quarterCycleIndex == 3) &&
				balance - quarterCycleIndex <= Settings.DEFAULT_PATTERN_SMOOTHNESS)
			{
				a = System.nanoTime();

				if (!patternSoundProperty.get().isPlaying()){
					System.out.println("Z   :::   " + (balance));
					System.out.println("Z   :::   " + (quarterCycleIndex));
					System.out.println("Z   :::   " + (balance - quarterCycleIndex) + " - " +
						(Settings.DEFAULT_PATTERN_SMOOTHNESS) + " - " +
						(balance - quarterCycleIndex <= Settings.DEFAULT_PATTERN_SMOOTHNESS));
					patternSoundProperty.get().play();
					mediaDone = false;
				}
				System.out.println("B   :::   call reset = " + (System.nanoTime() - a));
			}
			System.out.println("A   :::   total time = " + (System.nanoTime() - b));
		}
	}

	/**
	 * setup sound and its player. it starts playing if {@link #playingSound} is true
	 */
	public void setupSound()
	{
		patternSoundProperty.setValue(new AudioClip(FileLoader.getExternalUrlString(patternSoundUrl)));
		if (patternSoundProperty.get() != null)
			patternSoundProperty.get().stop();
		if (playingSound)
			playSound();
	}

	public void playSound() {
		playingSound = true;
		if (patternSoundProperty.getValue() != null && patternSoundProperty.get()!= null)
			patternSoundProperty.get().play();
	}

	public void pauseSound() {
		playingSound = false;
		if (patternSoundProperty.getValue() != null && patternSoundProperty.get()!= null)
			patternSoundProperty.get().stop();
//		if (patternSoundProperty.getValue() != null && mediaPlayer!= null)
//			mediaPlayer.pause();
	}

	public boolean isSwingingSound() {return swingingSound;}
	public void setSwingingSound(boolean swingingSound) {this.swingingSound = swingingSound;}
	public AudioClipWithNameProperty patternSoundProperty() { return patternSoundProperty;}
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
			this.patternSoundProperty.setValue(new AudioClip(FileLoader.getExternalUrlString(url)));
		setupSound();
	}
}
