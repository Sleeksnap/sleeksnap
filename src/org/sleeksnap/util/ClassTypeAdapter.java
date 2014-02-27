package org.sleeksnap.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A TypeAdapter for the Class type
 * 
 * @author Nikki
 *
 */
public class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

	@Override
	public Class<?> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		try {
			return Class.forName(element.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Type not found : " + element.getAsString());
		}
	}

	@Override
	public JsonElement serialize(Class<?> cl, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(cl.getName());
	}

}
