package com.ozplugins.AutoMinnows;

import net.runelite.client.config.*;

@ConfigGroup("AutoMinnows")
public interface AutoMinnowsConfiguration extends Config {
    String version = "v0.2";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Start next to minnows.\n\nYou can set up the amount of Minnows to catch before turning in for Shark. Set to 0 to deactivate this function.\n\nSet up configuration settings and hotkey, then run the plugin.";
    }

    @ConfigSection(
            name = "Instructions",
            description = "Plugin instructions.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";

    @ConfigSection(
            name = "Setup",
            description = "Plugin setup.",
            position = 5
    )
    String setupConfig = "setupConfig";

    @ConfigItem(
            keyName = "start/stop hotkey",
            name = "Start/Stop Hotkey",
            description = "Toggle for turning plugin on and off.",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "stopAtMinnows",
            name = "Turn in at",
            description = "Number of minnows to stop at to exchange for shark (0 to never stop)",
            position = 8,
            section = "setupConfig"
    )
    default int stopAtMinnows() {
        return 0;
    }

    @ConfigSection(
            //keyName = "delayTickConfig",
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 57,
            closedByDefault = true
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 58,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 59,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 60,
            section = "delayTickConfig"
    )
    default int tickDelayTarget() {
        return 2;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviation",
            description = "",
            position = 61,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 62,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @ConfigSection(
            name = "UI Settings",
            description = "UI settings.",
            position = 80,
            closedByDefault = true
    )
    String UIConfig = "UIConfig";

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }

}
