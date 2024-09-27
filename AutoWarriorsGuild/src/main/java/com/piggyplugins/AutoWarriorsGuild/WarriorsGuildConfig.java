package com.piggyplugins.AutoWarriorsGuild;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("warriorsGuildConfig")
public interface WarriorsGuildConfig extends Config {

    @ConfigSection(
            name = "Instructions",
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
        return "Select the armor to use and enter the animator room. Only gathers tokens and stops at set amount.";
    }



    @ConfigSection(
            name = "Combat Configurations",
            description = "Armors and Combat Configurations",
            position = 4
    )
    String armorConfigurations = "armorConfigurations";

    @ConfigItem(
            keyName = "armorsConfig",
            name = "Armors",
            description = "Select the armor to use",
            section = armorConfigurations,
            position = 5
    )
    default Armors armorType() {
        return Armors.BLACK;

    }

    @ConfigItem(
            keyName = "offensivePrayer",
            name = "Prayer",
            description = " ",
            position = 55,
            section = armorConfigurations
    )
    default OffensivePrayer offensivePrayer() {
        return OffensivePrayer.PIETY;
    }


    @ConfigSection(
            name = "UI Settings & Debugging",
            description = "Settings related to the user interface",
            position = 4
    )
    String uiSettings = "uiSettings";

    @ConfigItem(
            keyName = "uiLayout",
            name = "UI Layout",
            description = "Select the UI layout for the overlay",
            section = uiSettings,
            position = 5
    )
    default UiLayoutOption uiLayout() {
        return UiLayoutOption.FULL;

    }
    @ConfigItem(
            keyName = "showOverlay",
            name = "Room Overlays",
            description = "Configures whether or not the overlay is shown",
            position = 0,
            section = uiSettings
    )
    default boolean showRoomOverlays() {
        return false; // By default, the overlay is shown
    }

    @ConfigItem(
            position = 3,
            keyName = "tokenThreshold",
            name = "Token Threshold",
            description = "Will leave once you hit this threshold of tokens",
            section = armorConfigurations
    )
    default int tokenThreshold() {
        return 100;
    }
}













