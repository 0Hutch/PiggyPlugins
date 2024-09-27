package com.piggyplugins.BobTheTemplate;

import net.runelite.client.config.*;

@ConfigGroup("BobTheTemplate")
public interface BobTheTemplateConfig extends Config {
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
