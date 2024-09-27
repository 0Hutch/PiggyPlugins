package com.ozplugins.AutoThiever;

import net.runelite.client.config.*;

@ConfigGroup("AutoThiever")
public interface AutoThieverConfiguration extends Config {
    String version = "v0.2";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Set hotkey up and activate plugin with the hotkey.\n\n" +
                "Automatically opens pouches on your set amount in settings.\n\n Custom NPCs are added but untested. Expect weird shit.\n\n" +
                "To use Shadow Veil, have runes in inventory. If using Rune Pouch, make sure you have enough runes as plugin will not check for runes inside pouch.\n\n" +
                "If using Redemption/Ancient brews, will not use food unless you run out of brews.";
    }

    @ConfigSection(
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
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "ThieveNPC",
            name = "NPC to thieve:",
            position = 7,
            section = "setupConfig",
            description = "Input the type of NPC you want to yoink from."
    )
    default NPCs NPCToThieve() {
        return NPCs.ARDOUGNE_KNIGHT;
    }

    @ConfigItem(
            keyName = "useShadowVeil",
            name = "Use Shadow Veil?",
            description = "Enable this to use the Shadow Veil spell",
            section = "setupConfig",
            position = 8
    )
    default boolean shadowVeil() {
        return false;
    }

    @ConfigItem(
            keyName = "useRedemption",
            name = "Use Redemption/Ancient Brews?",
            description = "Enable this to use redemption prayer and ancient brews",
            section = "setupConfig",
            position = 9
    )
    default boolean useRedemption() {
        return false;
    }

    @ConfigItem(
            keyName = "dodgyNecklace",
            name = "Use Dodgy?",
            description = "Enable this to use dodgy necklaces",
            section = "setupConfig",
            position = 10
    )
    default boolean dodgyNecklace() {
        return false;
    }

    @ConfigItem(
            keyName = "dodgyAmount",
            name = "Amount of necklaces",
            position = 11,
            section = "setupConfig",
            description = "Amount of dodgy necklaces to withdraw."
    )
    default int dodgyAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "AncientBrewAmount",
            name = "Ancient Brew Amount:",
            position = 12,
            section = "setupConfig",
            description = "Amount of ancient brews to withdraw."
    )
    default int AncientBrewAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "HealthLowAmount",
            name = "Eat at health:",
            position = 14,
            section = "setupConfig",
            description = "Will eat set health."
    )
    default int HealthLowAmount() {
        return 10;
    }

    @ConfigItem(
            keyName = "FoodItemName",
            name = "Food to eat",
            description = "Case sensitive. Input the name of the food you want to eat.",
            position = 15,
            section = "setupConfig"
    )
    default String FoodName() {
        return "";
    }

    @ConfigItem(
            keyName = "FoodAmount",
            name = "# food to withdraw",
            position = 16,
            section = "setupConfig",
            description = "How many food should be withdrawn."
    )
    default int FoodAmount() {
        return 5;
    }

    @Range(
            min = 1,
            max = 139
    )
    @ConfigItem(
            keyName = "minPouch",
            name = "Min Pouches",
            position = 17,
            section = "setupConfig",
            description = "Minimum amount of pouches to have in inventory before opening."
    )
    default int MinPouches() {
        return 15;
    }

    @Range(
            min = 2,
            max = 140
    )
    @ConfigItem(
            keyName = "maxPouch",
            name = "Max Pouches",
            position = 18,
            section = "setupConfig",
            description = "Maximum amount of pouches to have in inventory before opening."
    )
    default int MaxPouches() {
        return 28;
    }

    @ConfigSection(
            name = "Custom NPC Config",
            description = "Custom NPC configuration section.",
			closedByDefault = true,
			position = 13
    )
    String customNPCConfig = "customNPCConfig";

    @ConfigItem(
            keyName = "customNPC",
            name = "NPC Name:",
            description = "Input the name of the npc you want to thieve.",
            position = 15,
            section = "customNPCConfig"
    )
    default String CustomNPCName() {
        return "";
    }

    @ConfigSection(
            //keyName = "delayTickConfig",
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            closedByDefault = true,
            position = 57
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
    default int tickDelayMin() {
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
    default int tickDelayMax() {
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
    default int tickDelayTarget() {
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
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 62,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @ConfigSection(
            name = "UI Settings",
            description = "UI settings.",
            closedByDefault = true,
            position = 80
    )
    String UIConfig = "UIConfig";

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }

}
