package com.lightBoard.controls.userProfiles;

import com.lightBoard.controls.ColorHelper;
import com.lightBoard.controls.Pattern;
import com.lightBoard.controls.patterns.HorizontalPattern;

import java.net.URL;

import javafx.scene.paint.Color;

/**
 * Created by Moham on 11/23/2016.
 */
public class PatientProfile
{
	public static class Builder
	{
		private Pattern defaultPattern = new HorizontalPattern();
		private String firstName = "";
		private String lastName = "";
		private URL imageLink;
		private Color patternColor = ColorHelper.SOFT_WHITE;
		private Color backgroundColor = ColorHelper.SOFT_BLACK;

		public Builder defaultPattern(final Pattern defaultPattern) {
			this.defaultPattern = defaultPattern;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder imageLink(URL imageLink) {
			this.imageLink = imageLink;
			return this;
		}

		public Builder patternColor(Color patternColor) {
			this.patternColor = patternColor;
			return this;
		}

		public Builder backgroundColor(Color backgroundColor) {
			this.backgroundColor = backgroundColor;
			return this;
		}

		public PatientProfile build() {
			return new PatientProfile(defaultPattern, firstName, lastName, imageLink, patternColor,
				backgroundColor);
		}
	}


	private Pattern defaultPattern;
	private String firstName;
	private String lastName;
	private URL imageLink;
	private Color patternColor;
	private Color backgroundColor;

	private PatientProfile(Pattern defaultPattern, String firstName, String lastName,
		URL imageLink, Color patternColor, Color backgroundColor)
	{
		this.defaultPattern = defaultPattern;
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageLink = imageLink;
		this.patternColor = patternColor;
		this.backgroundColor = backgroundColor;
	}

	public static PatientProfile defaultProfile(){
		return new PatientProfile.Builder().build();
	}

	public Pattern getDefaultPattern() {return defaultPattern;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public URL getImageLink() {return imageLink;}
	public Color getPatternColor() {return patternColor;}
	public Color getBackgroundColor() {return backgroundColor;}

	public void setDefaultPattern(Pattern defaultPattern) {this.defaultPattern = defaultPattern;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setImageLink(URL imageLink) {this.imageLink = imageLink;}
	public void setPatternColor(Color patternColor) {this.patternColor = patternColor;}
	public void setBackgroundColor(Color backgroundColor) {this.backgroundColor = backgroundColor;}
}
