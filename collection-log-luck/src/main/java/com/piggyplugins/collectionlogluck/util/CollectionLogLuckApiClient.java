package com.piggyplugins.collectionlogluck.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Slf4j
@Singleton
public class CollectionLogLuckApiClient
{
	private static final String COLLECTION_LOG_API_BASE = "api.collectionlog.net";
	private static final String COLLECTION_LOG_API_SCHEME = "https";
	private static final String COLLECTION_LOG_USER_PATH = "user";
	private static final String COLLECTION_LOG_LOG_PATH = "collectionlog";
	// Use a separate user agent to distinguish collection-log plugin from collection-log-luck plugin
	private static final String COLLECTION_LOG_LUCK_USER_AGENT = "Runelite collection-log-luck/" + CollectionLogLuckConfig.COLLECTION_LOG_VERSION;

	@Inject
	private CollectionLogLuckConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	public void getCollectionLog(String username, Callback callback) throws IOException
	{
		HttpUrl url = new HttpUrl.Builder()
			.scheme(COLLECTION_LOG_API_SCHEME)
			.host(COLLECTION_LOG_API_BASE)
			.addPathSegment(COLLECTION_LOG_LOG_PATH)
			.addPathSegment(COLLECTION_LOG_USER_PATH)
			.addEncodedPathSegment(username)
			.build();

		getRequest(url, callback);
	}

	private Request.Builder createRequestBuilder(HttpUrl url)
	{
		return new Request.Builder()
			.header("User-Agent", COLLECTION_LOG_LUCK_USER_AGENT)
			.url(url);
	}

	private void getRequest(HttpUrl url, Callback callback)
	{
		Request request = createRequestBuilder(url)
			.get()
			.build();
		apiRequest(request, callback);
	}

	private void apiRequest(Request request, Callback callback)
	{
		okHttpClient.newCall(request).enqueue(callback);
	}

	public JsonObject processResponse(Response response) throws IOException
	{
		if (!response.isSuccessful())
		{
			return null;
		}

		ResponseBody resBody = response.body();
		if (resBody == null)
		{
			return null;
		}
		return new JsonParser().parse(resBody.string()).getAsJsonObject();
	}
}
