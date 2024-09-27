
package com.piggyplugins.AutoAlcher;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autoAlcherConfig")
public interface AutoAlchConfig extends Config {

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Alch Configuration</font>",
            description = "",
            position = 1,
            closedByDefault = false
    )
    String autoAlchConfig = "autoAlchConfig";

    @ConfigItem(
            keyName = "itemsToAlch",
            name = "Items to Alch:",
            description = "List of items to alch, separated by commas",
            section = autoAlchConfig,
            position = 1
    )
    default String itemsToAlch() {
        return "Rune platebody, Rune platelegs";
    }

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Instructions</font>",
            description = "Instructions on how to use the plugin",
            position = 0,
            closedByDefault = false
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
        return "Enter items to be alched, separated by comma.";
    }
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">UI Settings</font>",
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




