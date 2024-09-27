package com.discordlevelnotifications;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.UsernameChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
	name = "DisFree"
)
public class LevelNotificationsPlugin extends Plugin
{
	private Hashtable<String, Integer> currentLevels;
	private ArrayList<String> leveledSkills;
	private boolean shouldSendMessage = false;
	private int ticksWaited = 0;

	@Inject
	private Client client;

	@Inject
	private LevelNotificationsConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private DrawManager drawManager;

	@Provides
	LevelNotificationsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LevelNotificationsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		currentLevels = new Hashtable<String, Integer>();
		leveledSkills = new ArrayList<String>();
	}

	@Override
	protected void shutDown() throws Exception
	{

	}

	@Subscribe
	public void onUsernameChanged(UsernameChanged usernameChanged)
	{
		resetState();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState().equals(GameState.LOGIN_SCREEN))
		{
			resetState();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!shouldSendMessage)
		{
			return;
		}

		if (ticksWaited < 2)
		{
			ticksWaited++;
			return;
		}

		shouldSendMessage = false;
		ticksWaited = 0;
		sendMessage();
	}

	@Subscribe
	public void onStatChanged(net.runelite.api.events.StatChanged statChanged)
	{
		String skillName = statChanged.getSkill().getName();
		int level = statChanged.getLevel();

		/* .contains wasn't behaving so I went with == null*/
		if (currentLevels.get(skillName) == null || currentLevels.get(skillName) == 0)
		{
			currentLevels.put(skillName, level);
			return;
		}

		if (currentLevels.get(skillName) != level)
		{
			currentLevels.put(skillName, level);

			if (shouldSendForThisLevel(level))
			{
				leveledSkills.add(skillName);
				shouldSendMessage = true;
			}
		}
	}

	private boolean shouldSendForThisLevel(int level)
	{
		return level >= config.minLevel()
				&& levelMeetsIntervalRequirement(level);
	}

	private boolean levelMeetsIntervalRequirement(int level)
	{
		return config.levelInterval() <= 1
				|| level == 99
				|| level % config.levelInterval() == 0;
	}

	private void sendMessage()
	{
		String levelUpString = client.getLocalPlayer().getName();

		String[] skills = new String[leveledSkills.size()];
		skills = leveledSkills.toArray(skills);
		leveledSkills.clear();

		for (int i = 0; i < skills.length; i++)
		{
			String skill = skills[i];
			leveledSkills.remove(skill);
			if (i > 0)
			{
				if (i == skills.length - 1)
				{
					levelUpString += " and ";
				}
				else
				{
					levelUpString += ", ";
				}
			}

			levelUpString += " Subio " + skill + " a " + currentLevels.get(skill) + " \n**Plugin:** Elite\n**Discord:** freedarwuin  \n**Actualizacion:** 17/08/2024";
		}

		DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
		discordWebhookBody.setContent(levelUpString);
		sendWebhook(discordWebhookBody);
	}

	private void sendWebhook(DiscordWebhookBody discordWebhookBody)
	{
		String configUrl = config.webhook();
		if (Strings.isNullOrEmpty(configUrl)) { return; }

		HttpUrl url = HttpUrl.parse(configUrl);
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));

		if (config.sendScreenshot())
		{
			sendWebhookWithScreenshot(url, requestBodyBuilder);
		}
		else
		{
			buildRequestAndSend(url, requestBodyBuilder);
		}
	}

	private void sendWebhookWithScreenshot(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
	{
		drawManager.requestNextFrameListener(image ->
		{
			BufferedImage bufferedImage = (BufferedImage) image;
			byte[] imageBytes;
			try
			{
				imageBytes = convertImageToByteArray(bufferedImage);
			}
			catch (IOException e)
			{
				log.warn("Error converting image to byte array", e);
				return;
			}

			requestBodyBuilder.addFormDataPart("file", "image.png",
					RequestBody.create(MediaType.parse("image/png"), imageBytes));
			buildRequestAndSend(url, requestBodyBuilder);
		});
	}

	private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
	{
		RequestBody requestBody = requestBodyBuilder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		sendRequest(request);
	}

	private void sendRequest(Request request)
	{
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.debug("Error submitting webhook", e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				response.close();
			}
		});
	}

	private static byte[] convertImageToByteArray(BufferedImage bufferedImage) throws IOException
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	private void resetState()
	{
		currentLevels.clear();
		leveledSkills.clear();
		shouldSendMessage = false;
		ticksWaited = 0;
	}
}