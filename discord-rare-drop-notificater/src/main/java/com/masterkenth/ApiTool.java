/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, MasterKenth
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.masterkenth;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.runelite.client.RuneLite;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiTool
{
	private static ApiTool _instance;

	private OkHttpClient httpClient = null;

	public static ApiTool getInstance()
	{
		if (_instance == null)
		{
			_instance = new ApiTool();
			_instance.httpClient = new OkHttpClient.Builder()
				.cache(new Cache(new File(RuneLite.CACHE_DIR, "okhttp_drdn"), 20 * 1024 * 1024)) // 20mb cache
				.build();
		}
		return _instance;
	}


	public String getIconUrl(int id)
	{
		return String.format("https://static.runelite.net/cache/item/icon/%d.png", id);
	}

	public CompletableFuture<ResponseBody> postRaw(String url, String data, String type)
	{
		Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse(type), data)).build();

		return callRequest(request);
	}

	public CompletableFuture<Void> postFormImage(String url, byte[] imageBytes, String type)
	{
		MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
			.addFormDataPart("file", "image.png", RequestBody.create(MediaType.parse(type), imageBytes));

		Request request = new Request.Builder().url(url).post(requestBuilder.build()).build();

		return callRequest(request).thenAccept(rb ->
		{
		});
	}

	private CompletableFuture<ResponseBody> callRequest(Request request)
	{
		CompletableFuture<ResponseBody> future = new CompletableFuture<>();

		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				future.completeExceptionally(e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				try (ResponseBody responseBody = response.body())
				{
					if (!response.isSuccessful())
					{
						future.completeExceptionally(new IOException("Unexpected code " + response));
					}
					else
					{
						future.complete(responseBody);
					}
				}
				response.close();
			}
		});

		return future;
	}
}
