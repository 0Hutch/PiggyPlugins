package com.ozplugins.K1Tick;

import net.runelite.client.config.*;

@ConfigGroup("K1Tick")
public interface K1TickConfiguration extends Config
{
	String version = "0.1";

	@ConfigItem(
			keyName = "instructions",
			name = "",
			description = "Instructions.",
			position = 1,
			section = "instructionsConfig"
	)
	default String instructions()
	{
		return "Start at Rogue's den next to fire.";
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
