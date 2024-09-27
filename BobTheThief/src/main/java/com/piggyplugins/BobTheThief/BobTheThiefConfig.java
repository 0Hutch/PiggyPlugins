package com.piggyplugins.BobTheThief;

import net.runelite.client.config.*;

@ConfigGroup("BobTheThief")
public interface BobTheThiefConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 3

    )
    String tickDelaySection = "Tick Delays";
    @ConfigSection(
            name = "Drop Config",
            description = "Configuration for amount of items to drop",
            position = 5

    )
    String dropConfigSection = "Drop Config";

    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "",
            position = -2
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }


    @ConfigItem(
            name = "Should Bank?",
            keyName = "shouldBank",
            description = "Search for a bank in the nearby area, if found bank when full",
            position = -3
    )
    default boolean shouldBank() {
        return false;
    }

    @ConfigItem(
            keyName = "searchNpc",
            name = "Search NPCs",
            description = "For things like fishing spots",
            position = -1
    )
    default boolean searchNpc() {
        return false;
    }
    @ConfigItem(
            name = "Use Dodgy necklaces",
            keyName = "useDodgy",
            description = "For things like fishing spots",
            position = -1
    )
    default boolean useDodgy() {
        return false;
    }

    @ConfigItem(
            name = "Object",
            keyName = "objectToInteract",
            description = "Game obejct you will be interacting with",
            position = 0
    )
    default String objectToInteract() {
        return "Tree";
    }

    @ConfigItem(
            name = "Expected Action",
            keyName = "Expected Action",
            description = "The action you wish to do on the object",
            position = 1
    )
    default String expectedAction() {
        return "Chop";
    }

    @ConfigItem(
            name = "Food(s)",
            keyName = "foodToUse",
            description = "Food to eat",
            position = 2
    )
    default String foodToUse() {
        return " Karambwan";
    }

    @Range(
            max = 99,
            min = 1
    )
    @ConfigItem(
            name = "Food Threshold Max",
            keyName = "foodThresholdMax",
            description = "When to eat",
            position = 2
    )
    default int foodThresholdMax() {
        return 50;
    }
    @Range(
            max = 99,
            min = 1
    )
    @ConfigItem(
            name = "Food Threshold Min",
            keyName = "foodThresholdMin",
            description = "When to eat",
            position = 2
    )
    default int foodThresholdMin() {
        return 50;
    }

    @ConfigItem(
            name = "Keep Items",
            keyName = "itemToKeep",
            description = "Items you don't want dropped. Separate items by comma,no space. Good for UIM",
            position = 3
    )
    default String itemsToKeep() {
        return "coins,rune pouch,divine rune pouch,looting bag,clue scroll";
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

    @Range(

    )
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


    @Range(
            max = 9
    )
    @ConfigItem(
            name = "Drop Per Tick Min",
            keyName = "numToDrop1",
            description = "Minimum amount of items dropped per tick",
            position = 6,
            section = dropConfigSection
    )
    default int dropPerTickOne() {
        return 1;
    }

    @Range(
            max = 9
    )
    @ConfigItem(
            name = "Drop Per Tick Max",
            keyName = "numToDrop2",
            description = "Maximum amount of items dropped per tick",
            position = 7,
            section = dropConfigSection
    )
    default int dropPerTickTwo() {
        return 3;
    }


}
