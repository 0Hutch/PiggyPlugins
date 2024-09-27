package com.ozplugins.AutoShopper;

import net.runelite.client.config.*;

@ConfigGroup("AutoShopper")
public interface AutoShopperConfiguration extends Config {
    String version = "v0.2";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Start next to the shop you want to buy from. \n\nSet the item name, amount and minimum amount to stop buying the item from the shop. \n\n" +
                "Make sure you have coins in your inventory. If banking is enabled, ensure a bank is nearby.";
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
            keyName = "EnableBanking",
            name = "Bank?",
            description = "If player should bank and deposit items.",
            position = 9,
            section = "setupConfig"
    )
    default boolean EnableBanking() {
        return false;
    }

    @ConfigItem(
            keyName = "NPCName",
            name = "NPC Name",
            description = "Input the name of the shop you want to buy from.",
            position = 11,
            section = "setupConfig"
    )
    default String NPCName() {
        return "";
    }

    @ConfigItem(
            keyName = "ItemName",
            name = "Item to buy",
            description = "Input the name of the item you want to buy.",
            position = 15,
            section = "setupConfig"
    )
    default String ItemName() {
        return "";
    }

    @Range(
            min = 0,
            max = 250
    )
    @ConfigItem(
            keyName = "AmountToBuy",
            name = "Amount To Buy",
            position = 17,
            section = "setupConfig",
            description = "Amount of item to buy."
    )
    default int AmountToBuy()
    {
        return 28;
    }

    @Range(
            min = 0,
            max = 250
    )
    @ConfigItem(
            keyName = "ItemMinimumAmount",
            name = "Stop buying at amount",
            position = 19,
            section = "setupConfig",
            description = "Will stop buying if item is under this amount."
    )
    default int ItemMinimumAmount()
    {
        return 100;
    }

    @ConfigItem(
            keyName = "ItemMaximumAmount",
            name = "Anti Overstock",
            position = 21,
            section = "setupConfig",
            description = "If shop has more than x amount it will avoid buying from it."
    )
    default int ItemMaximumAmount()
    {
        return 10;
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
