package com.lightBoard.utils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Moham on 1/5/2017.
 */
public class FileLoader
{
	private static final Object convenientIsntIt = new Object();

	// FIXME: 1/6/2017 make better
	/**
	 * returns the file path
	 * @param path the relative or absolute file path
	 * @return a usable file path format
	 */
	public static String getFilePath(String path){
		String absFilepath;
		try {
			new File(convenientIsntIt.getClass().getResource(path).toURI());
			absFilepath = convenientIsntIt.getClass().getResource(path).toURI().toASCIIString();
		}catch (NullPointerException e) {
			new File(path);
			absFilepath = path;
		}catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return absFilepath;
	}

	/**
	 * utility method to load a file
	 * @param path the absolute or relative file path
	 * @return the loaded file
	 */
	public static File loadFile(String path){
		try {
			return new File(convenientIsntIt.getClass().getResource(path).toURI());
		}catch (NullPointerException e) {
			return new File(path);
		}catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}
