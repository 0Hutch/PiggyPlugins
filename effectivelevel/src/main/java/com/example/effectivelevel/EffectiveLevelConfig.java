package com.example.effectivelevel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("effectivelevel")
public interface EffectiveLevelConfig extends Config
{
	@ConfigItem(
		keyName = "showPrayerBoost",
		name = "Show prayer boost",
		description = "Apply prayer boost multipliers to appropriate boosted skill levels.",
		position = 0
	)
	default boolean showPrayerBoost()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showStanceBonus",
		name = "Show stance bonus",
		description = "Add stance bonuses to appropriate boosted skill levels",
		position = 1
	)
	default boolean showStanceBonus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showAdjustmentConstant",
		name = "Show adjustment constant",
		description = "Add the adjustment constant of +8.",
		position = 2
	)
	default boolean showAdjustmentConstant()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showVoidBonus",
		name = "Show void equipment bonus",
		description = "Apply void equipment boost multipliers to appropriate boosted skill levels.",
		position = 3
	)
	default boolean showVoidBonus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showInvisibleBoost",
		name = "Show invisible skill boost",
		description = "Apply invisible boosts to appropriate boosted non-combat skill levels.",
		position = 4
	)
	default boolean showInvisibleBoost()
	{
		return true;
	}
}
