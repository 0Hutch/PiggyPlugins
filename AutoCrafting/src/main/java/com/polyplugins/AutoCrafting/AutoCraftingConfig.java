package com.polyplugins.AutoCrafting;

import com.polyplugins.AutoCrafting.config_types.ARMOR_TYPE;
import com.polyplugins.AutoCrafting.config_types.LEATHER_TYPE;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoVardorvis")
public interface AutoCraftingConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -50
    )
    String instructionsConfig99 = "instructionsConfig99";

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
            keyName = "instructions65",
            name = "",
            description = "Instructions.",
            position = -50,
            section = "instructionsConfig99"
    )
    default String instructions65() {
        return "Para que funcione. \n\nDeja afuera del banco. \n\nLas agujas.\n\nLos hilos.  \n\nY el leather que vas a craft..";
    }

    @ConfigItem(
            keyName = "Leather type",
            name = "Tipo de Leather",
            description = "Type of leather?",
            position = 0
    )
    default LEATHER_TYPE LEATHER_TYPE() {
        return LEATHER_TYPE.LEATHER;
    }

    @ConfigItem(
            keyName = "Armor type",
            name = "Tipo de Armadura",
            description = "Type of armor?",
            position = 1
    )
    default ARMOR_TYPE ARMOR_TYPE() {
        return ARMOR_TYPE.VAMBRACES;
    }
}
