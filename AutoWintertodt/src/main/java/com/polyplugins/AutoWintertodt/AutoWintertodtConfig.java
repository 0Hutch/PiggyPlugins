package com.polyplugins.AutoWintertodt;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("WintertodtConfig")
public interface AutoWintertodtConfig extends Config {
    /*@ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )*/
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Game Tick Configuracion",
            description = "",
            position = 11
    )
    String delayTickConfig = "delayTickConfig";

    /*@ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Mensaje";
    }*/

    @ConfigItem(
            keyName = "renderOverlay",
            name = "Pintura",
            description = "",
            position = 0
    )
    default boolean renderOverlay() {
        return true;
    }

    @ConfigItem(
            name = "Max recursos",
            keyName = "maxResources",
            description = "",
            position = 0
    )
    default int maxResources() {
        return 19;
    }

    @ConfigItem(
            name = "Comida",
            keyName = "Foods",
            description = "Comida para comer",
            position = 1
    )
    default Foods foodsToEat() {
        return Foods.MANTA_RAY;
    }

    @ConfigItem(
            name = "Comer entre",
            keyName = "eatAt",
            description = "",
            position = 2
    )
    default int eatAt() {
        return 20;
    }

    @ConfigItem(
            name = "Min Comida para retirarse",
            keyName = "minFood",
            description = "",
            position = 4
    )
    default int minFood() {
        return 1;
    }

    @ConfigItem(
            name = "# Comida",
            keyName = "foodAmount",
            description = "",
            position = 3
    )
    default int foodAmount() {
        return 6;
    }

    @ConfigItem(
            name = "Fletch",
            keyName = "doFletching",
            description = "",
            position = 5
    )
    default boolean doFletching() {
        return true;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 12,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 13,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Tick delay",
            description = "enables some tick delays",
            position = 14,
            section = "delayTickConfig"
    )
    default boolean tickDelay() {
        return false;
    }
}
