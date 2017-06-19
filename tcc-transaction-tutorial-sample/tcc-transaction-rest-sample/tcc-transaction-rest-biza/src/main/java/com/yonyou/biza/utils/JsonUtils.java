package com.yonyou.biza.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		OBJECT_MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
		OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T> T readJson(String json, Class<T> cls) {
		try {
			return OBJECT_MAPPER.readValue(json, cls);
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static String toJson(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

}
