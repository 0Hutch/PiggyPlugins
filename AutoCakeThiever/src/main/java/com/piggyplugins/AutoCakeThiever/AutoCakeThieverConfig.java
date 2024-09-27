package com.piggyplugins.AutoCakeThiever;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autoCakeThieverConfig")
public interface AutoCakeThieverConfig extends Config {

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
        return "Simple cake thiever, banks as well as eats the cake in your inventory if you have to.";
    }

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

    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Food Settings</font>",
            description = "Food eating",
            position = 6
    )
    String foodEatingSection = "foodEatingSection";

    @ConfigItem(
            position = 1,
            keyName = "autoEatEnabled",
            name = "Enable Auto-Eating",
            description = "Toggle automatic eating when health is low",
            section = foodEatingSection
    )
    default boolean autoEatEnabled() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "foodName",
            name = "Food Name",
            description = "Name or ID of the food item to eat",
            section = foodEatingSection
    )
    default String getFoodName() {
        return "Cake";
    }

    @ConfigItem(
            position = 3,
            keyName = "healthThreshold",
            name = "Health Threshold",
            description = "Health level at which to eat food",
            section = foodEatingSection
    )
    default int getHealthThreshold() {
        return 50;
    }


    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Drop Settings</font>",
            description = "Food eating",
            position = 7
    )
    String dropsettings = "dropBreadAndSlices";

    @ConfigItem(
            position = 2,
            keyName = "dropBreadAndSlices",
            name = "Drop Bread and Slices",
            description = "If enabled, drops bread and chocolate slices from inventory.",
            section = dropsettings
    )
    default boolean dropBreadAndSlices() {
        return false;
    }
}









