package com.lightBoard.model.userProfiles;

import com.lightBoard.controls.ColorHelper;
import com.lightBoard.controls.Pattern;
import com.lightBoard.controls.patterns.HorizontalPattern;

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
		private String imageUrl;
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

		public Builder imageUrl(String url) {
			this.imageUrl = url;
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
			return new PatientProfile(defaultPattern, firstName, lastName, imageUrl, patternColor,
				backgroundColor);
		}
	}


	private Pattern defaultPattern;
	private String firstName;
	private String lastName;
	private String imageUrl;
	private Color patternColor;
	private Color backgroundColor;

	private PatientProfile(Pattern defaultPattern, String firstName, String lastName,
		String imageUrl, Color patternColor, Color backgroundColor)
	{
		this.defaultPattern = defaultPattern;
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageUrl = imageUrl;
		this.patternColor = patternColor;
		this.backgroundColor = backgroundColor;
	}

	public static PatientProfile defaultProfile(){
		return new PatientProfile.Builder().build();
	}

	public Pattern getDefaultPattern() {return defaultPattern;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public String getImageUrl() {return imageUrl;}
	public Color getPatternColor() {return patternColor;}
	public Color getBackgroundColor() {return backgroundColor;}

	public void setDefaultPattern(Pattern defaultPattern) {this.defaultPattern = defaultPattern;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
	public void setPatternColor(Color patternColor) {this.patternColor = patternColor;}
	public void setBackgroundColor(Color backgroundColor) {this.backgroundColor = backgroundColor;}
}
