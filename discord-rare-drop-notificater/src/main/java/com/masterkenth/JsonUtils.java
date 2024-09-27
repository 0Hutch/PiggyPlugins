package com.masterkenth;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.masterkenth.models.Npc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils
{
	private static JsonUtils _instance;
	private final List<Npc> npcList;

	public static JsonUtils getInstance()
	{
		if (_instance == null)
		{
			_instance = new JsonUtils();
		}

		return _instance;
	}

	public JsonUtils()
	{
		try(InputStreamReader reader = new InputStreamReader(
				Objects.requireNonNull(DiscordRareDropNotificaterPlugin.class.getResourceAsStream("/monster-drops.json")),
				StandardCharsets.UTF_8)) {
			npcList = new Gson().fromJson(reader, new TypeToken<List<Npc>>() {}.getType());
		}
		catch (Exception e)
		{
			log.error("Error getting json", e);
			throw new RuntimeException(e);
		}
	}

	public Npc getNpc(String npcName)
	{
		return npcList.stream().filter((i) -> npcName.equals(i.getNpcName())).findFirst().orElse(null);
	}
}
