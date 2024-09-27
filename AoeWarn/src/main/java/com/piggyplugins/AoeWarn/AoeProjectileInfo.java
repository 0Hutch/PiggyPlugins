/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 *
 * Modified by farhan1666
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.piggyplugins.AoeWarn;

import com.piggyplugins.PiggyUtils.RLApi.GraphicIDExtended;

import java.util.HashMap;
import java.util.Map;


public enum AoeProjectileInfo
{
	LIZARDMAN_SHAMAN_AOE(GraphicIDExtended.LIZARDMAN_SHAMAN_AOE, 5),
	CRAZY_ARCHAEOLOGIST_AOE(GraphicIDExtended.CRAZY_ARCHAEOLOGIST_AOE, 3),
	ICE_DEMON_RANGED_AOE(GraphicIDExtended.ICE_DEMON_RANGED_AOE, 3),

	/**
	 * When you don't have pray range on ice demon does an ice barrage
	 */
	ICE_DEMON_ICE_BARRAGE_AOE(GraphicIDExtended.ICE_DEMON_ICE_BARRAGE_AOE, 3),

	/**
	 * The AOE when vasa first starts
	 */
	VASA_AWAKEN_AOE(GraphicIDExtended.VASA_AWAKEN_AOE, 3),
	VASA_RANGED_AOE(GraphicIDExtended.VASA_RANGED_AOE, 3),
	TEKTON_METEOR_AOE(GraphicIDExtended.TEKTON_METEOR_AOE, 3),

	/**
	 * The AOEs of Vorkath
	 */
	VORKATH_BOMB(GraphicIDExtended.VORKATH_BOMB_AOE, 3),
	VORKATH_POISON_POOL(GraphicIDExtended.VORKATH_POISON_POOL_AOE, 1),
	VORKATH_SPAWN(GraphicIDExtended.VORKATH_SPAWN_AOE, 1), //extra tick because hard to see otherwise
	VORKATH_TICK_FIRE(GraphicIDExtended.VORKATH_TICK_FIRE_AOE, 1),

	/**
	 * the AOEs of Galvek
	 */
	GALVEK_MINE(GraphicIDExtended.GALVEK_MINE, 3),
	GALVEK_BOMB(GraphicIDExtended.GALVEK_BOMB, 3),

	/**
	 * the AOEs of Grotesque Guardians
	 */
	DAWN_FREEZE(GraphicIDExtended.DAWN_FREEZE, 3),
	DUSK_CEILING(GraphicIDExtended.DUSK_CEILING, 3),

	/**
	 * the AOE of Vet'ion
	 */
	VETION_LIGHTNING(GraphicIDExtended.VETION_LIGHTNING, 1),

	/**
	 * the AOE of Chaos Fanatic
	 */
	CHAOS_FANATIC(GraphicIDExtended.CHAOS_FANATIC_AOE, 1),

	/**
	 * The AOE of Mage Arena 2 Bosses
	 */
	JUSTICIAR_LEASH(GraphicIDExtended.JUSTICIAR_LEASH, 1),
	MAGE_ARENA_BOSS_FREEZE(GraphicIDExtended.MAGE_ARENA_BOSS_FREEZE, 1),


	/**
	 * the AOE of the Corporeal Beast
	 */
	CORPOREAL_BEAST(GraphicIDExtended.CORPOREAL_BEAST_AOE, 1),
	CORPOREAL_BEAST_DARK_CORE(GraphicIDExtended.CORPOREAL_BEAST_DARK_CORE_AOE, 3),

	/**
	 * the AOEs of The Great Olm
	 */
	OLM_FALLING_CRYSTAL(GraphicIDExtended.OLM_FALLING_CRYSTAL, 3),
	OLM_BURNING(GraphicIDExtended.OLM_BURNING, 1),
	OLM_FALLING_CRYSTAL_TRAIL(GraphicIDExtended.OLM_FALLING_CRYSTAL_TRAIL, 1),
	OLM_ACID_TRAIL(GraphicIDExtended.OLM_ACID_TRAIL, 1),
	OLM_FIRE_LINE(GraphicIDExtended.OLM_FIRE_LINE, 1),

	/**
	 * the AOE of the Wintertodt snow that falls
	 */
	WINTERTODT_SNOW_FALL(GraphicIDExtended.WINTERTODT_SNOW_FALL_AOE, 3),

	/**
	 * AOE of Xarpus throwing poison
	 */
	XARPUS_POISON_AOE(GraphicIDExtended.XARPUS_ACID, 1),

	/**
	 * Aoe of Addy Drags
	 */
	ADDY_DRAG_POISON(GraphicIDExtended.ADDY_DRAG_POISON, 1),

	/**
	 * the Breath of the Drake
	 */
	DRAKE_BREATH(GraphicIDExtended.DRAKE_BREATH, 1),

	/**
	 * Cerbs fire
	 */
	CERB_FIRE(GraphicIDExtended.CERB_FIRE, 2),

	/**
	 * Demonic gorilla
	 */
	DEMONIC_GORILLA_BOULDER(GraphicIDExtended.DEMONIC_GORILLA_BOULDER, 1),

	/**
	 * Marble gargoyle (Superior Gargoyle)
	 */
	MARBLE_GARGOYLE_AOE(GraphicIDExtended.MARBLE_GARGOYLE_AOE, 1),


	/**
	 * Verzik
	 */
	VERZIK_PURPLE_SPAWN(GraphicIDExtended.VERZIK_PURPLE_SPAWN, 3),
	VERZIK_P1_ROCKS(GraphicIDExtended.DUSK_CEILING, 1);

	private static final Map<Integer, AoeProjectileInfo> map = new HashMap<>();

	static
	{
		for (AoeProjectileInfo aoe : values())
		{
			map.put(aoe.id, aoe);
		}
	}

	/**
	 * The id of the projectile to trigger this AoE warning
	 */
	private final int id;
	/**
	 * How long the indicator should last for this AoE warning This might
	 * need to be a bit longer than the projectile actually takes to land as
	 * there is a fade effect on the warning
	 */
	private final int aoeSize;

	AoeProjectileInfo(int id, int aoeSize)
	{
		this.id = id;
		this.aoeSize = aoeSize;
	}

	public static AoeProjectileInfo getById(int id)
	{
		return map.get(id);
	}

	public int getId()
	{
		return id;
	}

	public int getAoeSize()
	{
		return aoeSize;
	}
}
