package com.lightBoard.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Moham on 1/5/2017.
 */
public class FileLoader
{
	private static final Object convenientIsntIt = new Object();

	// FIXME: 1/6/2017 make better
//	/**
//	 * returns the file path
//	 * @param path the relative or absolute file path
//	 * @return a usable file path format
//	 */
//	public static String getFilePath(String path){
//		try {
//			System.out.println(loadFile(path).getAbsolutePath());
//			return loadFile(path).getAbsolutePath();
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			throw new NullPointerException();
//		}
//
//	}

	/**
	 * utility method to load a file
	 * @param path the absolute or relative file path
	 * @return the loaded file
	 */
	public static File loadResource(String path)
	{
		assert path != null || !path.isEmpty(): "fuck this bullshit";
		File tempFile = new File(path.substring(path.lastIndexOf(File.separator) +1));
		tempFile.deleteOnExit();
		try {
			tempFile.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Path p = tempFile.toPath();
		System.out.println(tempFile.exists()+ " - "+ tempFile.getPath());
		try {
			Files.copy(convenientIsntIt.getClass().getResourceAsStream(path), p);
			System.out.println(path);
			System.out.println(tempFile.exists());
//			p = Paths.get(convenientIsntIt.getClass().getResource(path).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
		tempFile = p.toAbsolutePath().toFile();

		return tempFile;
	}

	public static String getResourceUrlString(String path) {
		return loadResource(path).toURI().toString();
	}

	public static File loadFolder(String path)
	{
		File f;
		try {
			f = new File(convenientIsntIt.getClass().getResource(path).getPath());
			System.out.println(f);
			if (!f.exists())
				throw new NullPointerException("I fucked UP 1");
		}catch (NullPointerException e) {
			f = new File(path);
			System.out.println(f);
			if (!f.exists())
				throw new IllegalStateException("I fucked UP 2");
		}
		return f;
	}

	public static String fixPathSeparator(String path){
		path = path.replaceAll("\\\\", File.separator);
		while (path.contains("/"))
			path = path.replace("/", File.separator);
		return path;
	}
}
