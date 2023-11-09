package com.piggyplugins.PowerSkiller;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("PowerSkiller")
public interface PowerSkillerConfig extends Config {
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
            keyName = "searchNpc",
            name = "Search NPCs (for fishing, etc)",
            description = "For things like fishing spots",
            position = -1
    )
    default boolean searchNpc() {
        return false;
    }

    @ConfigItem(
            name = "Object",
            keyName = "objectToInteract",
            description = "Game object you will be interacting with",
            position = 0
    )
    default String objectToInteract() {
        return "Tree";
    }

    @ConfigItem(
            name = "Tool(s)",
            keyName = "toolsToUse",
            description = "Tools required to act with your object, can type ` axe` or ` pickaxe` to ignore the type",
            position = 1
    )
    default String toolsToUse() {
        return " axe";
    }

    @ConfigItem(
            name = "Keep Items",
            keyName = "itemToKeep",
            description = "Items you don't want dropped. Separate items by comma,no space. Good for UIM",
            position = 2
    )
    default String itemsToKeep() {
        return "coins,rune pouch,divine rune pouch,looting bag,clue scroll";
    }

    @Range(
            max = 9
    )
    @ConfigItem(
            name = "Drop Per Tick Min",
            keyName = "numToDrop1",
            description = "Minimum amount of items dropped per tick",
            position = 3
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
            position = 4
    )
    default int dropPerTickTwo() {
        return 3;
    }
//      not sure if this is possible
    @ConfigItem(
            name = "Forestry Tree",
            keyName = "dropItems",
            description = "Object w most players,UNCHECK IF NOT WC",
            position = 5
    )
    default boolean useForestryTreeNotClosest() {
        return false;
    }


//    @ConfigItem(
//            name = "Empty slots",
//            keyName = "emptySlots",
//            description = "Amount of empty slots you have to skill with, mostly a UIM feature lol",
//            position = 3
//    )
//    default int emptySlots() {
//        return 28;
//    }
}
