package com.santaplugins.AutoLoot;

import net.runelite.client.config.*;

@ConfigGroup("AutoCombatConfig")
public interface AutoLootConfig extends Config {
    @ConfigSection(
            name = "Looting Configuration",
            description = "Configure how to handle looting",
            position = 2,
            closedByDefault = false
    )
    String lootingConfig = "lootingConfig";
    @ConfigItem(
            keyName = "lootEnabled",
            name = "Should loot toggle",
            description = "Loots items",
            position = 1,
            section = lootingConfig
    )
    default boolean lootEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot items",
            description = "Write items to loot, seperated by ;",
            position = 3,
            section = lootingConfig
    )
    default String itemsToLoot() {
        return "Feather";
    }
}
