package com.piggyplugins.AutoPohPrayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autoPrayerConfig")
public interface PrayerConfig extends Config {

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
        return "Choose what bones to use and what Altar to use them on. Start with noted bones and coins in your inventory." +
                "" +
                "Make sure you find a house that has an altar first, and then run the plugin.";
    }
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">UI Settings</font>",
            description = "Settings related to the user interface",
            position = 2
    )
    String uiSettings = "uiSettings";


    @ConfigItem(
            keyName = "uiLayout",
            name = "UI Layout",
            description = "Select the UI layout for the overlay",
            section = uiSettings,
            position = 2
    )
    default UiLayoutOption uiLayout() {
        return UiLayoutOption.FULL;
    }

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Bones & Altar Configuration</font>",
            description = "Pick the bones to use and the altar to use them on",
            position = 1

    )
    String userConfiguration = "userConfiguration";

    @ConfigItem(
            keyName = "boneName",
            name = "Bones to use",
            description = "What bones to use on said altar",
            position = 2,
            section = userConfiguration
    )
    default String nameOfBones() {
        return "Bones";
    }

    @ConfigItem(
            keyName = "drinkPool",
            name = "Drink from pool",
            description = "",
            position = 55,
            section = userConfiguration
    )
    default boolean drinkPool() {
        return false;
    }

    @ConfigItem(
            keyName = "altarName",
            name = "Altar name",
            description = "Input what altar to use",
            position = 3,
            section = userConfiguration
    )
    default String nameOfAltar() {
        return "Altar";
    }
}

