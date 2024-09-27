package com.piggyplugins.CannonReloader;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("CannonReloader")
public interface CannonReloaderConfig extends Config {

    @Range(
            max = 30
    )
    @ConfigItem(
            keyName = "reloadMin",
            name = "Reload Min",
            description = "Amount of balls left before relaoding"
    )
    default int reloadMin() {
        return 5;
    }

    @Range(
            max = 30
    )
    @ConfigItem(
            keyName = "reloadMax",
            name = "Reload Max",
            description = "Amount of balls left before relaoding"
    )
    default int reloadMax() {
        return 20;
    }
}
