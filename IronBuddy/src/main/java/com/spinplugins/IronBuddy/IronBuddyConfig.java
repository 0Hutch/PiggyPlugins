package com.spinplugins.IronBuddy;

import com.spinplugins.IronBuddy.data.Const;
import net.runelite.client.config.*;

@ConfigGroup("IronBuddyConfig")
public interface IronBuddyConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Plugin Configuration",
            description = "Configure different tasks & load config options for your IronBuddy plugin.",
            position = 1,
            closedByDefault = false
    )
    String pluginConfigSection = "pluginConfigSection";

    @ConfigItem(
            keyName = "bankPinString",
            name = "Bank pin (leave blank if not needed)",
            description = "Select the type of seaweed to burn.",
            position = 1,
            section = pluginConfigSection
    )
    default String bankPinString() {
        return "";
    }

    @ConfigItem(
            keyName = "taskType",
            name = "Task",
            description = "Select the type of task you want to run IronBuddy for.",
            position = 2,
            section = pluginConfigSection
    )
    default Const.BuddyTasks taskType() {
        return Const.BuddyTasks.CRAFTING_GLASS;
    }

    @ConfigItem(
            keyName = "seaweedType",
            name = "Seaweed Type",
            description = "Select the type of seaweed to burn.",
            position = 3,
            section = pluginConfigSection
    )
    default Const.Seaweed seaweedType() {
        return Const.Seaweed.GIANT_SEAWEED;
    }

    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how to handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 2,
            closedByDefault = false
    )
    String delayTickConfig = "delayTickConfig";


    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 2,
            section = delayTickConfig
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
            position = 3,
            section = delayTickConfig
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Tick delay",
            description = "enables some tick delays",
            position = 4,
            section = delayTickConfig
    )
    default boolean tickDelay() {
        return true;
    }
}