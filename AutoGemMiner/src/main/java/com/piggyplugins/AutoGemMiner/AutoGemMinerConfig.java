package com.piggyplugins.AutoGemMiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("gemMinerConfig")
public interface AutoGemMinerConfig extends Config {

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Instructions</font>",
            description = "Instructions on how to use the plugin",
            position = 0
    )
    String instructionsSection = "instructionsSection";

    @ConfigItem(
            position = 1,
            keyName = "instructionsText",
            name = "Plugin Instructions",
            description = "Detailed instructions on how to use the plugin",
            section = instructionsSection
    )
    default String instructionsText() {
        return "Start with pickaxe equipped, graceful is optional. Start in Shilo Village.";
    }
    @ConfigSection(
            name = "UI Settings",
            description = "Settings related to the user interface",
            position = 1
    )
    String uiSettings = "uiSettings";


    @ConfigItem(
            keyName = "uiLayout",
            name = "UI Layout",
            description = "Select the UI layout for the overlay",
            section = uiSettings,
            position = 1
    )
    default UiLayoutOption uiLayout() {
        return UiLayoutOption.FULL;
    }

}
