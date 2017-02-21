package com.lightBoard.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Created by Moham on 2/19/2017.
 */
public class MultiMediaPlayer
{
	private MediaPlayer mp1, mp2;
	private boolean usingMp1 = true;

	private ScheduledThreadPoolExecutor seekService = new ScheduledThreadPoolExecutor(2);
	private Runnable seekRun = new Runnable() {
		@Override
		public void run() {
			try
			{
				long a = System.nanoTime();
				if(usingMp1) {
					usingMp1 = false;
					mp1.stop();
					mp2.play();
					mp1.seek(new Duration(0));
				} else {
					usingMp1 = true;
					mp2.stop();
					mp1.play();
					mp2.seek(new Duration(0));
				}
				System.out.println("D   :::   reset sound time taken = " + (System.nanoTime() - a));
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	};

	public MultiMediaPlayer(Media value)
	{
		mp1 = new MediaPlayer(value);
		mp2 = new MediaPlayer(value);

		mp1.balanceProperty().bindBidirectional(mp2.balanceProperty());
		mp1.onEndOfMediaProperty().bindBidirectional(mp2.onEndOfMediaProperty());
	}

	public void setBalance(double value){
		mp1.setBalance(value);
	}

	public void reset(){
		seekService.execute(seekRun);
	}

	public void play(){
		if(usingMp1) {
			mp1.play();
		} else {
			mp2.play();
		}
	}

	public void stop(){
		mp1.stop();
		mp2.stop();
	}

	public void pause(){
		mp1.pause();
		mp2.pause();
	}

	public void setOnEndOfMedia(Runnable r) {
		mp1.setOnEndOfMedia(r);
	}
}
