/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, MasterKenth, thefungus
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

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import net.runelite.api.ItemID;

// All rarity data have been manually scraped from osrs wiki
// Data set only contain (subjectively) rare drops so that if a drop matches against a pickpocket it is always posted to Discord
@AllArgsConstructor
public enum PickpocketRarity
{
	// @formatter:off
	VYRE_BLOOD_SHARD(ItemID.BLOOD_SHARD, 1f / 5000f),
	ELF_TELEPORT_CRYSTAL(ItemID.ENHANCED_CRYSTAL_TELEPORT_SEED, 1f / 1024f);

	// @formatter:on
	private final int itemId;
	private final float rarity;

	public static final ImmutableMap<Integer, RarityItemData> PICKPOCKET_TABLE_MAPPING = initPickpocketMapping();

	private static ImmutableMap<Integer, RarityItemData> initPickpocketMapping()
	{
		ImmutableMap.Builder<Integer, RarityItemData> builder = new ImmutableMap.Builder<>();
		for (PickpocketRarity r : values())
		{
			RarityItemData data = new RarityItemData();
			data.Unique = true;
			data.Rarity = r.rarity;
			builder.put(r.itemId, data);
		}

		return builder.build();
	}
}
