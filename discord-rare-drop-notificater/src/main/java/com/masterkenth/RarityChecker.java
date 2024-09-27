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

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.masterkenth.models.Npc;
import com.masterkenth.models.NpcItem;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import org.json.JSONObject;

@Slf4j
public class RarityChecker
{
	public ItemData CheckRarityEvent(String eventName, ItemData item, ItemManager itemManager)
	{
		String lowerName = eventName.toLowerCase();
		Map<Integer, RarityItemData> table = EventRarity.EVENT_TABLE_MAPPING.getOrDefault(lowerName, null);
		RarityItemData rarityInfo = null;
		if (table != null)
		{
			if (table.containsKey(item.ItemId))
			{
				rarityInfo = table.get(item.ItemId);
			}
			else
			{
				int mapId = ItemVariationMapping.map(item.ItemId);
				Collection<Integer> idVariations = ItemVariationMapping.getVariations(mapId);

				String origName = itemManager.getItemComposition(item.ItemId).getName();

				for (Integer id : idVariations)
				{
					if (table.containsKey(id))
					{
						String variationName = itemManager.getItemComposition(id).getName();
						if (origName.equals(variationName))
						{
							rarityInfo = table.get(item.ItemId);
							break;
						}
						else
						{
							log.warn(String.format("item id %d=%d found in table '%s' but other name ('%s' vs '%s')", item.ItemId, id,
								eventName, origName, variationName));
						}
					}
				}
			}
		}
		else
		{
			log.warn(String.format("No event table for '%s'", eventName));
		}
		if (rarityInfo != null)
		{
			item.Unique = rarityInfo.Unique;
			item.Rarity = rarityInfo.Rarity;
		}
		else
		{
			log.warn(String.format("no rarity for item %d in table '%s'", item.ItemId, eventName));
		}
		return item;
	}

	public ItemData CheckRarityPickpocket(String pickpocketName, ItemData item, ItemManager itemManager)
	{
		RarityItemData itemData = PickpocketRarity.PICKPOCKET_TABLE_MAPPING.getOrDefault(item.ItemId, null);
		if (itemData != null)
		{
			item.Rarity = itemData.Rarity;
			item.Unique = itemData.Unique;
		}

		return item;
	}

	public CompletableFuture<ItemData> CheckRarityNPC(String npcName, ItemData itemData, ItemManager itemManager, int quantity)
	{
		CompletableFuture<ItemData> f = new CompletableFuture<>();

		// Call this before the API call so we're in the main clients thread.
		ItemComposition ic = itemManager.getItemComposition(itemData.ItemId);
		String origName = ic.getName();
		int mapId = ItemVariationMapping.map(itemData.ItemId);
		Collection<Integer> idVariations = new HashSet<Integer>();
		idVariations.add(itemData.ItemId); // First item, will be resolved first.

		// Map all ids
		for (Integer id : ItemVariationMapping.getVariations(mapId))
		{
			String variationName = itemManager.getItemComposition(id).getName();
			if (origName.equals(variationName))
			{
				idVariations.add(id);
			}
		}


		try
		{
			Npc npcDrops = JsonUtils.getInstance().getNpc(npcName);

			if(npcDrops != null) {
				for (Integer id : idVariations)
				{
					NpcItem item = npcDrops.getItems().stream().filter((i) -> id.equals(i.getItemID())).findFirst().orElse(null);

					if (item != null)
					{
						itemData.Rarity = item.getRarity();
						String dropQuantityStr = item.getQuantity();
						String[] quantityParts = dropQuantityStr.split("-");
						if (quantityParts.length == 2)
						{
							try
							{
								int dropQuantityMin = Integer.parseInt(quantityParts[0]);
								int dropQuantityMax = Integer.parseInt(quantityParts[1]);
								if (quantity < dropQuantityMin || quantity > dropQuantityMax)
									continue;
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
								// Assume it matches;
							}
						}
						else
						{
							try
							{
								int dropQuantity = Integer.parseInt(dropQuantityStr);
								if (dropQuantity != quantity)
									continue;
							} catch (Exception ex)
							{
								ex.printStackTrace();
								// Assume it matches;
							}
						}

						itemData.Unique = false;
						f.complete(itemData);
					}
				}
			}

			// No entry for item, default to 100% drop
			f.complete(itemData);
		}
		catch (Exception e)
		{
			f.completeExceptionally(e);
		}

		return f;
	}
}
