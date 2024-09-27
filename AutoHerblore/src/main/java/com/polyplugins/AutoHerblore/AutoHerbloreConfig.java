package com.polyplugins.AutoHerblore;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoHerblore")
public interface AutoHerbloreConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "";
    }

    @ConfigItem(
            keyName = "base potion",
            name = "Base de la pocion",
            description = "",
            position = 0
    )
    default String BASE_POTION() {
        return "Snapdragon potion (unf)";
    }

    @ConfigItem(
            keyName = "secondary",
            name = "Secundario",
            description = "",
            position = 1
    )
    default String SECONDARY() {
        return "Red spiders' eggs";
    }
}
