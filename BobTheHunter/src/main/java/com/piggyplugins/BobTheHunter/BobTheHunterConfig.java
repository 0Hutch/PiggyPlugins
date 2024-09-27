package com.piggyplugins.BobTheHunter;

import net.runelite.client.config.*;

@ConfigGroup("BobTheHunter")
public interface BobTheHunterConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 3

    )
    String tickDelaySection = "Tick Delays";

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
            keyName = "teakTik",
            name = "Teak tick manipulation",
            description = "",
            position = 0
    )
    default boolean teakTik() {
        return false;
    }


    @ConfigItem(
            name = "Trap Type",
            keyName = "trapType",
            description = "Type of trap",
            position = 3
    )
    default TrapType trapType() {
        return TrapType.BIRD_SNARE;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            name = "Number of traps",
            keyName = "trapCount",
            description = "Number of traps to place",
            position = 3
    )
    default int trapCount() {
        return 1;
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
            name = "Keep Items",
            keyName = "itemToKeep",
            description = "Items you don't want dropped. Separate items by comma,no space. Good for UIM",
            position = 3
    )
    default String itemsToKeep() {
        return "coins,rune pouch,divine rune pouch,looting bag,clue scroll";
    }

}
