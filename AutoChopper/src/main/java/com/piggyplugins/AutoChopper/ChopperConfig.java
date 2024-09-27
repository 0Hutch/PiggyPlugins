package com.piggyplugins.AutoChopper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import com.piggyplugins.AutoChopper.Enums.AxeType;
import com.piggyplugins.AutoChopper.Enums.LogAction;
import com.piggyplugins.AutoChopper.Enums.TreeType;
import com.piggyplugins.AutoChopper.Enums.UiLayoutOption;

@ConfigGroup("autoChopperConfig")
public interface ChopperConfig extends Config {

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
        return "If you choose to bank, make sure there is a bank nearby. The plugin will look for the nearest tree, so make sure you watch it.";
    }

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Auto Settings</font>",
            description = "Auto looting settings",
            position = 1
    )
    String automaticSettings = "automaticSettings";


//    @ConfigItem(
//            keyName = "bankLogs",
//            name = "Bank Logs",
//            description = "",
//            section = automaticSettings
//    )
//    default boolean bankLogs() {
//        return false;
//    }
//
//    @ConfigItem(
//            keyName = "burnLogs",
//            name = "Burn Logs",
//            description = "",
//            section = automaticSettings
//    )
//    default boolean burnLogs() {
//        return false;
//    }
//
//    @ConfigItem(
//            keyName = "dropLogs",
//            name = "Drop Logs",
//            description = "",
//            section = automaticSettings
//    )
//    default boolean dropLogs() {
//        return false;
//    }

    @ConfigItem(
            keyName = "logAction",
            name = "Log Action",
            description = "",
            section = automaticSettings,
            position = 1
    )
    default LogAction logAction() {
        return LogAction.BURN;
    }

    @ConfigItem(
            keyName = "treeType",
            name = "Tree to Cut",
            description = "",
            section = automaticSettings,
            position = 1
    )
    default TreeType treeType() {
        return TreeType.NORMAL;
    }

    //@ConfigItem(
    //        keyName = "axeType",
    //        name = "Axe to use",
    //        description = "",
    //        section = automaticSettings,
    //        position = 2
    //)
    //default AxeType axeType() {
    //    return AxeType.RUNE;
    //}

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">UI Settings</font>",
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
}
