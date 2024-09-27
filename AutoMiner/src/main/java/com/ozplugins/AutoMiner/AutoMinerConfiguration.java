package com.ozplugins.AutoMiner;

import com.ozplugins.AutoMiner.Constants.AmethystMineSpot;
import com.ozplugins.AutoMiner.Constants.MiningGuildRocks;
import com.ozplugins.AutoMiner.Constants.Mode;
import net.runelite.client.config.*;

@ConfigGroup("AutoMiner")
public interface AutoMinerConfiguration extends Config
{
	String version = "v0.2.1";

	@ConfigItem(
			keyName = "instructions",
			name = "",
			description = "Instructions.",
			position = 1,
			section = "instructionsConfig"
	)
	default String instructions()
	{
		return "Select your plugin mode and configure it's corresponding configuration section.\n\n" +
				"Set hotkey up and activate plugin with the hotkey.";
	}

	@ConfigSection(
			//keyName = "delayTickConfig",
			name = "Instructions",
			description = "Plugin instructions.",
			position = 2
	)
	String instructionsConfig = "instructionsConfig";

	@ConfigSection(
			name = "Setup",
			description = "Plugin setup.",
			position = 5
	)
	String setupConfig = "setupConfig";

	@ConfigItem(
			keyName = "start/stop hotkey",
			name = "Start/Stop Hotkey",
			description = "Toggle for turning plugin on and off.",
			position = 6,
			section = "setupConfig"
	)
	default Keybind toggle()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "MineMode",
			name = "Mine mode:",
			position = 7,
			section = "setupConfig",
			description = "Select your mining method."
	)
	default Mode Mode() {
		return Mode.MINING_GUILD;
	}


	@ConfigSection(
			name = "Mining Guild Config",
			description = "Mining Guild configuration section.",
			position = 8,
			closedByDefault = true
	)
	String guildConfig = "guildConfig";

	@ConfigItem(
			keyName = "guildRock",
			name = "Rock",
			position = 9,
			section = "guildConfig",
			description = "What rock do you want to mine."
	)
	default MiningGuildRocks guildRock() {
		return MiningGuildRocks.IRON_1;
	}


	@ConfigSection(
			name = "Powermine Config",
			description = "Powermine configuration section.",
			position = 16,
			closedByDefault = true
	)
	String powermineConfig = "powermineConfig";

	@ConfigItem(
			keyName = "powermineRock",
			name = "Rock to powermine",
			description = "Input the name of the rock you want to powermine.",
			position = 18,
			section = "powermineConfig"
	)
	default String powermineRock() {
		return "Iron";
	}

	@Range(
			min = 1,
			max = 10
	)
	@ConfigItem(
			keyName = "minToDrop",
			name = "Min Drop per Tick",
			description = "Minimum amount to drop per tick",
			position = 20,
			section = "powermineConfig"
	)
	default int minDropPerTick()
	{
		return 1;
	}

	@Range(
			min = 1,
			max = 10
	)
	@ConfigItem(
			keyName = "maxToDrop",
			name = "Max Drop per Tick",
			description = "Maximum amount to drop per tick",
			position = 21,
			section = "powermineConfig"
	)
	default int maxDropPerTick()
	{
		return 7;
	}

	@ConfigSection(
			name = "Amethyst Config",
			description = "Amethyst configuration section.",
			position = 24,
			closedByDefault = true
	)
	String amethystConfig = "amethystConfig";

	@ConfigItem(
			keyName = "amethystMineSpot",
			name = "Amethyst spot",
			description = "Choose your preferred mining spot for amethyst in the mining guild.",
			position = 26,
			section = "amethystConfig"
	)
	default AmethystMineSpot amethystSpot() {
		return AmethystMineSpot.AMETHYST_1;
	}

	@ConfigSection(
		name = "Game Tick Configuration",
		description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
		position = 57,
			closedByDefault = true
	)
	String delayTickConfig = "delayTickConfig";

	@Range(
		min = 0,
		max = 10
	)
	@ConfigItem(
		keyName = "tickDelayMin",
		name = "Game Tick Min",
		description = "",
		position = 58,
		section = "delayTickConfig"
	)
	default int tickDelayMin()
	{
		return 1;
	}

	@Range(
		min = 0,
		max = 10
	)
	@ConfigItem(
		keyName = "tickDelayMax",
		name = "Game Tick Max",
		description = "",
		position = 59,
		section = "delayTickConfig"
	)
	default int tickDelayMax()
	{
		return 3;
	}

	@Range(
		min = 0,
		max = 10
	)
	@ConfigItem(
		keyName = "tickDelayTarget",
		name = "Game Tick Target",
		description = "",
		position = 60,
		section = "delayTickConfig"
	)
	default int tickDelayTarget()
	{
		return 2;
	}

	@Range(
		min = 0,
		max = 10
	)
	@ConfigItem(
		keyName = "tickDelayDeviation",
		name = "Game Tick Deviation",
		description = "",
		position = 61,
		section = "delayTickConfig"
	)
	default int tickDelayDeviation()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "tickDelayWeightedDistribution",
		name = "Game Tick Weighted Distribution",
		description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
		position = 62,
		section = "delayTickConfig"
	)
	default boolean tickDelayWeightedDistribution()
	{
		return false;
	}

	@ConfigSection(
			name = "UI Settings",
			description = "UI settings.",
			position = 80,
			closedByDefault = true
	)
	String UIConfig = "UIConfig";

	@ConfigItem(
		keyName = "enableUI",
		name = "Enable UI",
		description = "Enable to turn on in game UI",
			section = "UIConfig",
			position = 140
	)
	default boolean enableUI()
	{
		return true;
	}

}
