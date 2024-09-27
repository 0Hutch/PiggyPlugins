package com.piggyplugins.BobTheChef;

import net.runelite.client.config.*;

@ConfigGroup("BobTheChef")
public interface BobTheChefConfig extends Config {
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
            name = "Raw Food",
            keyName = "rawFood",
            description = "",
            position = 4
    )
    default String rawFood() {
        return "";
    }

    @ConfigItem(
            name = "Finished Food",
            keyName = "finishedFood",
            description = "",
            position = 4
    )
    default String finishedFood() {
        return "";
    }

    @ConfigItem(
            name = "Keyboard key",
            keyName = "keyboardKey",
            description = "",
            position = 4
    )
    default int keyboardKey() {
        return 1;
    }

    @ConfigItem(
            name = "One Tick",
            keyName = "oneTick",
            description = "",
            position = 4
    )
    default boolean oneTick() {
        return false;
    }

    @Range(min = 0, max = 100)
    @ConfigItem(
            name = "One Tick Success rate %",
            keyName = "oneTickSuccessRate",
            description = "",
            position = 4
    )
    default int oneTickSuccessRate() {
        return 95;
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
