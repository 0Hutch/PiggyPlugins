package com.piggyplugins.BobTheCombatBoy;

import net.runelite.client.config.*;

@ConfigGroup("BobTheCombatBoy")
public interface BobTheCombatBoyConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 13

    )
    String tickDelaySection = "Tick Delays";

    @ConfigSection(
            name = "Items selection",
            description = "Configuration for items",
            position = 10

    )
    String itemSelection = "Item selection";

    @ConfigSection(
            name = "Cannon",
            description = "Configuration for cannon",
            position = 11

    )
    String cannonSelection = "Cannon selection";
    @ConfigSection(
            name = "Escape options",
            description = "Configuration for GTFO",
            position = 12

    )
    String escapeSelection = "Escape options";


    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "",
            position = 1
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "target",
            name = "Target",
            description = "",
            position = 1
    )
    default String target() {
        return "Rat";
    }
    @ConfigItem(
            keyName = "pestControl",
            name = "Pest Control (BETA)",
            description = "",
            position = 1
    )
    default boolean pestControl() {
        return false;
    }
    @ConfigItem(
            keyName = "eat",
            name = "Eat HP",
            description = "",
            position = 2
    )
    default int eat() {
        return 50;
    }

    @ConfigItem(
            keyName = "prayer",
            name = "Prayer",
            description = "",
            position = 2
    )
    default int prayer() {
        return 20;
    }

    @ConfigItem(
            keyName = "safeSpot",
            name = "Use safe spot",
            description = "",
            position = 3
    )
    default boolean safeSpot() {
        return false;
    }

    @ConfigItem(
            keyName =  "maniacalMonkey",
            name = "MM2 Maniacal monkey mode",
            description = "",
            position = 4
    )
    default boolean maniacalMonkey() {
        return false;
    }
    @ConfigItem(
            keyName =  "maniacalMonkeyReset",
            name = "MM2 Maniacal monkey reset time",
            description = "",
            position = 4
    )
    default int maniacalMonkeyReset() {
        return 15;
    }

    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 4,
            section = tickDelaySection
    )
    default int tickdelayMin() {
        return 0;
    }

    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 5,
            section = tickDelaySection
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            name = "Attack timeout",
            keyName = "attackTimeout",
            description = "Time before attacking",
            position = 6,
            section = tickDelaySection
    )
    default int attackTimeout() {
        return 5;
    }

    @ConfigItem(
            name = "Loot",
            keyName = "loot",
            description = "",
            position = 0,
            section = itemSelection
    )
    default boolean loot() {
        return true;
    }

    @ConfigItem(
            name = "Loot coins",
            keyName = "lootCoins",
            description = "",
            position = 0,
            section = itemSelection
    )
    default boolean lootCoins() {
        return true;
    }

    @ConfigItem(
            keyName = "bury",
            name = "Bury bones",
            description = "",
            position = 0,
            section = itemSelection
    )
    default boolean bury() {
        return true;
    }

    @ConfigItem(
            name = "Use GE or HA Value",
            keyName = "useHAValue",
            description = "Check for HA value and uncheck for GE value",
            position = 1,
            section = itemSelection
    )
    default boolean useHAValue() {
        return false;
    }

    @ConfigItem(
            name = "Min value",
            keyName = "minVal",
            description = "",
            position = 2,
            section = itemSelection
    )
    default int minVal() {
        return 5000;
    }

    @ConfigItem(
            name = "Min stackable value",
            keyName = "minStackVal",
            description = "",
            position = 3,
            section = itemSelection
    )
    default int minStackVal() {
        return 1000;
    }

    @ConfigItem(
            name = "Drop items",
            keyName = "dropItems",
            description = "",
            position = 4,
            section = itemSelection
    )
    default boolean dropItems() {
        return true;
    }

    @ConfigItem(
            name = "Drop value",
            keyName = "dropValue",
            description = "If inventory is full drop items less of value",
            position = 5,
            section = itemSelection
    )
    default int dropValue() {
        return 5;
    }

    @ConfigItem(
            name = "Use cannon",
            keyName = "useCannon",
            description = "",
            position = 0,
            section = cannonSelection
    )
    default boolean useCannon() {
        return true;
    }

    @ConfigItem(
            name = "Cannon time",
            keyName = "cannonTime",
            description = "",
            position = 1,
            section = cannonSelection
    )
    default int cannonTime() {
        return 30;
    }


    @ConfigItem(
            name = "Stop when task is done",
            keyName = "slayerStop",
            description = "",
            position = 0,
            section = escapeSelection
    )
    default boolean slayerStop() {
        return true;
    }

    @ConfigItem(
            name = "Break tab when task is done",
            keyName = "slayerBreak",
            description = "",
            position = 1,
            section = escapeSelection
    )
    default boolean slayerBreak() {
        return true;
    }

    @ConfigItem(
            name = "Low Prayer break tab",
            keyName = "lowPrayer",
            description = "",
            position = 2,
            section = escapeSelection
    )
    default int lowPrayer() {
        return 15;
    }

    @ConfigItem(
            name = "Low Health break tab",
            keyName = "lowHealth",
            description = "",
            position = 2,
            section = escapeSelection
    )
    default int lowHealth() {
        return 30;
    }

}
