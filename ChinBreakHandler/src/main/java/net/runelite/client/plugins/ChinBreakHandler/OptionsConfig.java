package net.runelite.client.plugins.ChinBreakHandler;

import net.runelite.client.config.*;

@ConfigGroup("piggyBreakHandler")
public interface OptionsConfig extends Config {
    @ConfigSection(
            name = "Settings",
            description = "",
            position = 0
    )
    String misc = "Misc";

    @ConfigItem(
            keyName = "stopAfterBreaks",
            name = "Stop after X breaks",
            description = "Stop after a given amount of breaks (0 to disable)",
            position = 1,
            section = misc
    )
    default int stopAfterBreaks()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "autoBankPin",
            name = "Auto Bank Pin",
            description = "Will automatically enter your bank pin",
            position = 2,
            section = misc
    )
    default boolean autoBankPin() {
        return false;
    }

    @ConfigItem(
            keyName = "avoidWorldsPlayerCount",
            name = "Max Players",
            description = "Maximum amount of players a world should have when hopping",
            position = 3,
            section = misc
    )
    default int avoidWorldsPlayerCount() {
        return 1500;
    }

    @ConfigItem(
            keyName = "avoidWorldsNumbers",
            name = "Avoid Worlds",
            description = "World numbers you want to avoid hopping to separated by commas.",
            position = 4,
            section = misc
    )
    default String avoidWorldsNumbers() {
        return "302,330";
    }

    @ConfigSection(
            name = "Hopping",
            description = "",
            position = 2
    )
    String hoppingTitle = "Hopping";

    @ConfigItem(
            keyName = "hop-after-break",
            name = "Hop world after break",
            description = "Hop to a different world after taking a break",
            position = 3,
            section = hoppingTitle
    )
    default boolean hopAfterBreak()
    {
        return false;
    }

    @ConfigItem(
            keyName = "american",
            name = "American",
            description = "Enable hopping to American worlds",
            position = 4,
            section = hoppingTitle
    )
    default boolean american()
    {
        return false;
    }

    @ConfigItem(
            keyName = "united-kingdom",
            name = "United kingdom",
            description = "Enable hopping to UK worlds",
            position = 5,
            section = hoppingTitle
    )
    default boolean unitedKingdom()
    {
        return false;
    }

    @ConfigItem(
            keyName = "german",
            name = "German",
            description = "Enable hopping to German worlds",
            position = 6,
            section = hoppingTitle
    )
    default boolean german()
    {
        return false;
    }

    @ConfigItem(
            keyName = "australian",
            name = "Australian",
            description = "Enable hopping to Australian worlds",
            position = 7,
            section = hoppingTitle
    )
    default boolean australian()
    {
        return false;
    }

    @ConfigSection(
            name = "Inactive Hours",
            description = "Set the time range during which the handler will be inactive",
            position = 3
    )
    String inactiveHoursTitle = "Inactive Hours";

    @ConfigItem(
            keyName = "inactiveHoursToggle",
            name = "Enable Inactive Hours",
            description = "Enables Inactive Hours",
            position = 7,
            section = inactiveHoursTitle
    )
    default boolean inactiveHoursToggle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "inactiveStartTime",
            name = "Start Time",
            description = "Start time of the inactive period (24-hour format, e.g., 22:00)",
            position = 1,
            section = inactiveHoursTitle
    )
    default String inactiveStartTime() {
        return "00:00";
    }

    @ConfigItem(
            keyName = "inactiveEndTime",
            name = "End Time",
            description = "End time of the inactive period (24-hour format, e.g., 06:00)",
            position = 2,
            section = inactiveHoursTitle
    )
    default String inactiveEndTime() {
        return "00:00";
    }
}
