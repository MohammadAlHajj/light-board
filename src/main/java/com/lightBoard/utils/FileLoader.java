package com.lightBoard.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Moham on 1/5/2017.
 */
public class FileLoader
{
	// FIXME: 1/6/2017 make better

	/**
	 * returns the file path
	 *
	 * @param path the relative or absolute file path
	 * @return a usable file path format
	 */
	public static String getFilePath(String path)
	{
		File f;
		try
		{
			URL resource = FileLoader.class.getResource(path);
			System.out.println(resource);
			f = new File(resource.toExternalForm());
		} catch (NullPointerException e)
		{
			f = new File(path);
		}
		if (!f.exists())
			f = new File(path);

		if (!f.exists())
			throw new IllegalStateException("file doesn't exist");

		String absFilepath;
		absFilepath = f.toURI().toString();
//		try {
//			absFilepath = f.getCanonicalPath();
//		} catch (IOException e) {
//			throw new IllegalStateException("file doesn't exist");
//		}
		System.out.println(absFilepath);
		return absFilepath;
	}

	/**
	 * utility method to load a file
	 *
	 * @param path the absolute or relative file path
	 * @return the loaded file
	 */
	public static File loadFile(String path)
	{

		File f = null;
		try
		{
			f = new File(FileLoader.class.getResource(path).toURI());
		} catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

		if (!f.exists())
		{
			System.out.println(f);
			f = new File(path);
		}
		if (!f.exists())
			throw new IllegalArgumentException("file doesn't exist: " + path);

		return f;
	}

	public static URL getResourceUrl(String path)
	{
		File f;
		URL resource = FileLoader.class.getResource(path);
		if (resource == null)
		{
			try
			{
				resource = new URL(path);
			} catch (MalformedURLException e)
			{
				throw new IllegalArgumentException("path is not valid: " + path);
			}
		}

		return resource;
	}

	public static String getExternalUrlString(String path)
	{
		try {
			return new File(path).toURI().toURL().toString();
		} catch (MalformedURLException e)
		{
			throw new IllegalArgumentException("path invalid: "+path);
		}
	}

	public static File loadExternalFile(String path){
		File extFile = new File(path);
		if (extFile.exists())
			return extFile;
		else throw new IllegalArgumentException("path invalid: "+path);
	}
}
