package cn.pptest.ppmock.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class Json {
	
	private Json() {}

    public static <T> T read(String json, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			return mapper.readValue(json, clazz);
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to bind JSON to object. Reason: " + ioe.getMessage() + "  JSON:" + json, ioe);
		}
	}
	
	public static <T> String write(T object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to generate JSON from object. Reason: " + ioe.getMessage(), ioe);
		}
	}
}
