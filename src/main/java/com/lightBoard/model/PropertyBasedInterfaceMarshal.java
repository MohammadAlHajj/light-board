package com.lightBoard.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Moham on 12/6/2016.
 *
 * This is a magic class. now one other then the creator knows how it works, but it does. Thank
 * you StackOverflow
 */
public class PropertyBasedInterfaceMarshal implements
	JsonSerializer<Object>, JsonDeserializer<Object>
{

	private static final String CLASS_META_KEY = "CLASS_META_KEY";

	@Override
	public Object deserialize(JsonElement jsonElement, Type type,
		JsonDeserializationContext jsonDeserializationContext)
		throws JsonParseException
	{
		JsonObject jsonObj = jsonElement.getAsJsonObject();
		String className = jsonObj.get(CLASS_META_KEY).getAsString();
		try {
			Class<?> clz = Class.forName(className);
			return jsonDeserializationContext.deserialize(jsonElement, clz);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(Object object, Type type,
		JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
		jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY,
			object.getClass().getCanonicalName());
		return jsonEle;
	}
}