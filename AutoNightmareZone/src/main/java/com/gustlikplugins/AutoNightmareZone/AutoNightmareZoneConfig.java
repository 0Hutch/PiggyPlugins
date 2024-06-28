package com.gustlikplugins.AutoNightmareZone;


import net.runelite.client.config.*;

@ConfigGroup("gustation")
public interface AutoNightmareZoneConfig extends Config {

    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "",
            position = -1
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "Strategy",
            name = "Strategy",
            description = "The strategy to use inside Nightmare zone",
            position = 1
    )
    default NightmareZoneStrategy strategy() {
        return NightmareZoneStrategy.PRAYER;
    }

    @ConfigItem(
            keyName = "PrayerFlickMode",
            name = "Prayer Flick Mode",
            description = "Specifies the prayer flicking mode NOTE: absorption mode will flick prayers Rapid heal seperately",
            position = 2
    )
    default PrayerFlickMode prayerFlickMode() {
        return PrayerFlickMode.OFF;
    }

    @ConfigItem(keyName = "Prayers", name = "Prayers", description = "List of prayers to flick", position = 3)
    default String prayers() {
        return "Protect from Melee";
    }

    @ConfigItem(
            keyName = "HealthReductionItem",
            name = "Health Reduction Item",
            description = "Specifies the item to be used for reduction player's health when using absorption mode",
            position = 4
    )
    default HealthReductionItem healthReductionitem() {
        return HealthReductionItem.LOCATOR_ORB;
    }


    @ConfigItem(keyName = "usePrayerPots", name = "Use prayer potions?", description = "", position = 5)
    default boolean usePrayerPots() {
        return true;
    }


    @ConfigItem(keyName = "useOverloads", name = "Use overload potions?", description = "", position = 6)
    default boolean useOverloads() {
        return false;
    }

    /** TODO ADD flick rapid heal
    @ConfigItem(keyName = "flickRapidHead", name = "Flick rapid heal?", description = "", position = 7)
    default boolean flickRapidHead() {
        return false;
    }
    **/

    @ConfigSection(
            name = "Randomization",
            description = "Configuration for Randomization",
            position = 8

    )
    String randomizationSection = "Randomization";

    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 1,
            section = randomizationSection
    )
    default int tickdelayMin() {
        return 0;
    }

    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 2,
            section = randomizationSection
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            name = "Absorption Offset",
            keyName = "AbsorptionOffset",
            description = "Will generate a random number between 1 and this setting for when to refill absorption",
            position = 3,
            section = randomizationSection
    )
    default int absorptionOffset() {
        return 300;
    }

    @ConfigItem(
            name = "Prayer Offset",
            keyName = "prayerOffset",
            description = "Will generate a random offset on when to drink a prayer potion",
            position = 4,
            section = randomizationSection
    )
    default int prayerOffset() {
        return 10;
    }





}
