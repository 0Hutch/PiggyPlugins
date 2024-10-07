package com.piggyplugins.collectionlogluck.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
public class JsonUtils
{
	@Inject
	Gson gson;

	public <T, D extends JsonDeserializer<T>> T fromJsonObject(JsonObject data, Class<T> type, D deserializer)
	{
		return gson.newBuilder()
			.registerTypeAdapter(type, deserializer)
			.create()
			.fromJson(data, type);
	}
}
