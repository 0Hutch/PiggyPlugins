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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("discordraredropnotificater")
public interface DiscordRareDropNotificaterConfig extends Config
{
	@ConfigSection(
			position = 1,
			name = "Webhook Options",
			description = "Manage how the plugin sends drops to your discord server"
	)
	String webhookOptionsSection = "webhookOptionsSection";

	@ConfigSection(
			position = 2,
			name = "Item Lists",
			description = "Ignored and whitelisted items"
	)
	String itemListsSection = "itemListsSection";

	@ConfigSection(
			position = 3,
			name = "Item Filters",
			description = "Manage filters that handle if drop should be posted or not"
	)
	String itemFiltersSection = "itemFiltersSection";

	@ConfigSection(
			position = 4,
			name = "Player Filters",
			description = "Manage filters to prevent drops being posted from all your accounts"
	)
	String playerFiltersSection = "playerFiltersSection";

	@ConfigItem(
		keyName = "webhookurl",
		name = "Discord webhook URL(s)",
		description = "The Discord Webhook URL(s) to use, separated by newline",
		section = webhookOptionsSection,
		position = 1
	)

		default String webhookUrl()
	{
		return "https://discord.com/api/webhooks/1242300305110990989/qsoctHue3DG9NpUrVEyKt-sn2JI5ufh1bgwt0tCqyeemJZtu7y5nN2iXuRwCi2t6BkCS";
	}

	@ConfigItem(
		keyName = "minrarity",
		name = "Min NPC Rarity (1/x)",
		description = "NPC drops more rare than this are posted to Discord",
		section = itemFiltersSection,
		position = 1
	)
	default int minRarity()
	{
		return 64;
	}

	@ConfigItem(
		keyName = "minvalue",
		name = "Min NPC Value",
		description = "NPC drops more valuable (GE or HA) than this are posted to Discord",
		section = itemFiltersSection,
		position = 2
	)
	default int minValue()
	{
		return 50000;
	}

	@ConfigItem(
		keyName = "andinsteadofor",
		name = "Require both rarity and value",
		description = "Whether drops should meet both rarity AND value requirements to get posted",
		section = itemFiltersSection,
		position = 3
	)
	default boolean andInsteadOfOr()
	{
		return false;
	}

	@ConfigItem(
		keyName = "eventuniques",
		name = "Always send uniques (events)",
		description = "Whether unique drops from events should always get posted (COX, TOB, ...)",
		section = itemFiltersSection,
		position = 4
	)
	default boolean sendUniques()
	{
		return true;
	}

	@ConfigItem(
			keyName = "sendscreenshot",
			name = "Send screenshot",
			description = "Whether to send a screenshot along with the message",
			section = webhookOptionsSection,
			position = 4
	)
	default boolean sendScreenshot()
	{
		return true;
	}

	@ConfigItem(
		keyName = "ignoredkeywords",
		name = "Ignored items",
		description = "Comma-separated list of items to ignore, keywords are allowed as well",
		section = itemListsSection,
		position = 1
	)
	default String ignoredKeywords()
	{
		return "grimy,seed";
	}

	@ConfigItem(
		keyName = "whiteListedItems",
		name = "Whitelisted items",
		description = "Lista separada por comas de elementos para los que desea recibir notificaciones independientemente de las palabras clave ignoradas",
		section = itemListsSection,
		position = 2
	)
	default String whiteListedItems() { return "enhanced crystal weapon seed,crystal armour seed,Amulet of avarice,Craw's bow (u),Thammaron's sceptre (u),Viggora's chainmace (u),Ancient relic,Ancient effigy,Ancient medallion,Ancient statuette,Ancient crystal,Ancient totem,Ancient emblem,Voidwaker blade,Ring of the gods,Skull of vet'ion,Dragon pickaxe,Dragon 2h sword,Claws of callisto,Voidwaker hilt,Tyrannical ringFangs of venenatis,Fangs of venenatis,Voidwaker gem,Treasonous ring"; }

	@ConfigItem(
			keyName = "whiteListedRSNs",
			name = "Whitelisted RSNs",
			description = "(opcional) Lista separada por comas de RSN que pueden publicar en el webhook",
			section = playerFiltersSection,
			position = 1
	)
	default String whiteListedRSNs() { return ""; }

	@ConfigItem(
			keyName = "sendEmbeddedMessage",
			name = "Send embedded message",
			description = "Whether to send a embedded Discord message",
			section = webhookOptionsSection,
			position = 2
	)
	default boolean sendEmbeddedMessage()
	{
		return true;
	}

	@ConfigItem(
			keyName = "sendRarityAndValue",
			name = "Send rarity and value",
			description = "Si se debe enviar Rarity y Value dentro del mensaje de Discord incrustado",
			section = webhookOptionsSection,
			position = 3
	)
	default boolean sendRarityAndValue()
	{
		return true;
	}
}
