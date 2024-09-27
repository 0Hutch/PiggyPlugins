package com.ozplugins.AutoMTA;

import com.ozplugins.AutoMTA.Spells.Alch;
import com.ozplugins.AutoMTA.Spells.BonesTo;
import com.ozplugins.AutoMTA.Spells.Enchant;
import net.runelite.client.config.*;

@ConfigGroup("AutoMTA")
public interface AutoMTAConfiguration extends Config {
    String version = "v0.2";

    @ConfigSection(
            name = "Instructions",
            description = "Plugin instructions.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Start at Mage Training Arena. \n\n Select the room you want to do in Setup. \n\n" +
                "Make sure you have all required runes for your room. Plugin will turn off if not.";
    }

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
            keyName = "room",
            name = "Select room:",
            position = 7,
            section = "setupConfig",
            description = "Input the area where you want to mine at."
    )
    default Room Room() {
        return Room.TELEKINETIC;
    }

    @ConfigItem(
            keyName = "stopAtPoints",
            name = "Stop At Points",
            description = "Number of points to stop at (0 to never stop)",
            position = 8,
            section = "setupConfig"
    )
    default int stopAtPoints() {
        return 0;
    }

    @ConfigSection(
            name = "Enchant Room Configuration",
            description = "Enchant room config.",
            closedByDefault = true,
            position = 10
    )
    String enchantRoomConfig = "enchantRoomConfig";

    @ConfigItem(
            keyName = "enchantSpell",
            name = "Enchant Spell:",
            position = 12,
            section = "enchantRoomConfig",
            description = "Select the enchant spell for Enchantment room."
    )
    default Enchant EnchantSpell() {
        return Enchant.LVL_3;
    }

    @ConfigItem(
            keyName = "pickUpDragonstone",
            name = "Pick up dragonsgtones",
            description = "Will pick up and enchant dragonstones in Enchant room",
            position = 12,
            section = "enchantRoomConfig"
    )
    default boolean pickUpDragonstone() {
        return false;
    }

    @ConfigItem(
            keyName = "turnInEnchant",
            name = "Turn In Enchanted items",
            description = "Will turn in if checked, drop otherwise (more efficient)",
            position = 14,
            section = "enchantRoomConfig"
    )
    default boolean enchantTurnIn() {
        return false;
    }

    @ConfigSection(
            name = "Graveyard Room Configuration",
            description = "Graveyard room config.",
            closedByDefault = true,
            position = 18
    )
    String graveyardRoomConfig = "graveyardRoomConfig";

    @ConfigItem(
            keyName = "bonesToSpell",
            name = "Bones To Spell:",
            position = 20,
            section = "graveyardRoomConfig",
            description = "Select the Bones To spell for the Graveyard room."
    )
    default BonesTo BonesToSpell() {
        return BonesTo.BANANAS;
    }

    @ConfigSection(
            name = "Alchemy Room Configuration",
            description = "Graveyard room config.",
            closedByDefault = true,
            position = 24
    )
    String alchemyRoomConfig = "alchemyRoomConfig";

    @ConfigItem(
            keyName = "alchemySpell",
            name = "Alchemy Spell:",
            position = 20,
            section = "alchemyRoomConfig",
            description = "Select alchemy spell for the Alchemy room."
    )
    default Alch AlchemySpell() {
        return Alch.HIGH_ALCH;
    }

    @Range(
            min = 100,
            max = 9900
    )
    @ConfigItem(
            keyName = "minimumCoinsDeposit",
            name = "Deposit Alch Coins At",
            description = "Will deposit coins at set amount in Alchemy room",
            position = 28,
            section = "alchemyRoomConfig"
    )
    default int minimumCoinsDeposit() {
        return 2000;
    }

    @ConfigSection(
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
