package com.lightBoard.utils;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Created by Moham on 2/19/2017.
 */
public class MultiMediaPlayer
{
	private MediaPlayer[] mp = new MediaPlayer[5];
	private int mpIndex;

	private ScheduledThreadPoolExecutor seekService = new ScheduledThreadPoolExecutor(
		ManagementFactory.getThreadMXBean().getThreadCount() );
	private Runnable seekRun = new Runnable() {
		@Override
		public void run() {
			try
			{
				long a = System.nanoTime();

				for (int i = 1; i < mp.length; i++)
					if(i != mpIndex){
						mp[i].pause();
						mp[i].seek(Duration.ZERO);
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
		mp[0] = new MediaPlayer(value);

		for (int i = 1; i< mp.length; i++){
			mp[i] = new MediaPlayer(value);
			mp[i].balanceProperty().bindBidirectional(mp[0].balanceProperty());
			mp[i].onEndOfMediaProperty().bindBidirectional(mp[0].onEndOfMediaProperty());
		}

	}

	public void setBalance(double value){
		mp[0].setBalance(value);
	}

	public void reset(){
		mp[mpIndex].pause();
		mp[(mpIndex+1) % mp.length].play();
		System.out.println("D   :::   reset sound time taken = "+(mpIndex));

		seekService.execute(() ->{
			long a = System.nanoTime();

			mp[mpIndex].seek(Duration.ZERO);

			System.out.println("D   :::   reset sound time taken = "+(mpIndex) + " - " + (System
				.nanoTime() - a));
		});
		mpIndex = (mpIndex+1) % mp.length;
	}

	public void play(){
		mp[mpIndex].play();
	}

	public void stop(){
		for (MediaPlayer aMp : mp) {
			aMp.stop();
		}
	}

	public void pause(){
		for (MediaPlayer aMp : mp) {
			aMp.pause();
		}
	}

	public void setOnEndOfMedia(Runnable r) {
		mp[0].setOnEndOfMedia(r);
	}
}
