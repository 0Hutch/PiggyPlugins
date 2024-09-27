package com.piggyplugins.BobTheBlower;

import net.runelite.client.config.*;

@ConfigGroup("BobTheBlower")
public interface BobTheBlowerConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 3

    )
    String tickDelaySection = "Tick Delays";

    @ConfigSection(
            name = "Items selection",
            description = "Configuration for items",
            position = 2

    )
    String itemSelection = "Item selection";

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
            name = "Option",
            keyName = "option",
            description = "",

            position = 4
    )
    default int option() {
        return -1;
    }

    @ConfigItem(
            name = "Item 1 / Tool",
            keyName = "items1",
            description = "",
            section = itemSelection,
            position = 3
    )
    default String items1() {
        return "Molten glass";
    }
    @ConfigItem(
            name = "Item 1 amount",
            keyName = "items1Amount",
            description = "",
            section = itemSelection,
            position = 3
    )
    default int items1Amount() {
        return 1;
    }

    @ConfigItem(
            name = "Item 2",
            keyName = "items2",
            description = "",
            section = itemSelection,
            position = 4
    )
    default String items2() {
        return "Molten glass";
    }
    @ConfigItem(
            name = "Item 2 amount",
            keyName = "items2Amount",
            description = "",
            section = itemSelection,
            position = 4
    )
    default int items2Amount() {
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



}
