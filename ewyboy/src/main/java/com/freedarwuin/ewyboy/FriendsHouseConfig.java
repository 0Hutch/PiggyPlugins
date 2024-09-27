package com.freedarwuin.ewyboy;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Friends House")
public interface FriendsHouseConfig extends Config {
    @ConfigItem(
            keyName = "name",
            name = "Name",
            description = "Name of the players house to enter"
    )

    default String name() {
        return null;
    }

}