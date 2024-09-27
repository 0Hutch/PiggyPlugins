package com.piggyplugins.BarbFisher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import com.piggyplugins.BarbFisher.UiLayoutOption;

@ConfigGroup("barbConfig")
public interface BarbConfig extends Config {

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
        return "Start with required items at the Barbarian fishing area.";
    }

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Auto Settings</font>",
            description = "",
            position = 1
    )
    String autoSettings = "autoSettings";


    @ConfigItem(
            position = 3,
            keyName = "maxDrop",
            name = "Max Drop",
            description = "",
            section = autoSettings
    )
    default int maxDrop() {
        return 2;
    }

    @ConfigItem(
            position = 4,
            keyName = "minDrop",
            name = "Min Drop",
            description = "",
            section = autoSettings
    )
    default int minDrop() {
        return 1;
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

