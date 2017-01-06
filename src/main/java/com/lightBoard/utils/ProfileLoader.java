package com.lightBoard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lightBoard.controls.VisualPattern;
import com.lightBoard.model.PatientProfile;
import com.lightBoard.model.PropertyBasedInterfaceMarshal;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Mohammad on 12/31/2016.
 */
public class ProfileLoader
{
	private static Gson gson= new GsonBuilder()
			.registerTypeAdapter(VisualPattern.class, new PropertyBasedInterfaceMarshal())
			.create();

	/**
	 * takes the current profile and saves it's data to a file
	 * @throws IOException
	 */
	public static void saveProfile(PatientProfile patientProfile) throws IOException {
		Writer writer;
		if (patientProfile.isDefault())
			writer = new FileWriter("default.json");
		else writer = new FileWriter(patientProfile.getId() + ".json");

		gson.toJson(patientProfile, writer);
		writer.close();
	}

	/**
	 * loads the profile with the specified id
	 * @param id the id of the profile to load from file, 0 represents the default profile
	 * @throws IOException
	 */
	public static PatientProfile loadProfile(int id) throws IOException {
		Reader reader;
		if(id == 0)
			reader = new FileReader("default.json");
		else reader = new FileReader(id + ".json");
		PatientProfile patientProfile = gson.fromJson(reader, PatientProfile.class);
		reader.close();

		return patientProfile;
	}
}
