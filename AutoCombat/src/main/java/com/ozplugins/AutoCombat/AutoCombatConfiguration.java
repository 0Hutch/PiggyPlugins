package com.ozplugins.AutoCombat;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("OzAutoCombat")
public interface AutoCombatConfiguration extends Config {
    String version = "v0.4";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Configure your Enemy NPC Config section first. Make sure you set up your enemy NPC name. \n\n Flick Prayers will flick the entire time the plugin is active." +
                "\n\nSet hotkey up and activate plugin with the hotkey after you complete the setup.";
    }

    @ConfigSection(
            //keyName = "delayTickConfig",
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
            keyName = "FlickPrayers",
            name = "Flick Prayers",
            description = "Should quick prayer be flicked?",
            position = 9,
            section = "setupConfig"
    )
    default boolean FlickPrayers() {
        return false;
    }

    @ConfigItem(
            keyName = "StopSlayerTaskDone",
            name = "Stop If Slayer Task Done?",
            description = "Stop plugin if slayer task is completed",
            position = 9,
            section = "setupConfig"
    )
    default boolean StopSlayerIfTaskDone() {
        return false;
    }


    //CONSUMABLES
    @ConfigSection(
            name = "Consumables Config",
            description = "Consumables configuration section.",
            closedByDefault = true,
            position = 13
    )
    String consumablesConfig = "consumablesConfig";

    @ConfigItem(
            keyName = "consumableInstructions",
            name = "",
            description = "Consumable Instructions.",
            position = 14,
            section = "consumablesConfig"
    )
    default String consumablesConfigInstructions() {
        return "Any option that is set to 0 is deactivated. \n\nMore options coming soon.";
    }

    @ConfigItem(
            keyName = "FoodItemName",
            name = "Food to eat",
            description = "Input the name of the food you want to eat.",
            position = 15,
            section = "consumablesConfig"
    )
    default String FoodName() {
        return "";
    }

    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "HealthLowAmount",
            name = "Eat at health:",
            position = 16,
            section = "consumablesConfig",
            description = "Will eat set health."
    )
    default int HealthLowAmount() {
        return 55;
    }


    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "PrayerLowAmount",
            name = "Drink Prayer potion at",
            position = 17,
            section = "consumablesConfig",
            description = "Will drink prayer potion at set amount."
    )
    default int PrayerLowAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "SuperAttackDrinkAmount",
            name = "Drink S. Attack at",
            position = 18,
            section = "consumablesConfig",
            description = "Will drink super attack potion at set amount."
    )
    default int SuperAttackDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "SuperStrengthDrinkAmount",
            name = "Drink S. Strength at",
            position = 19,
            section = "consumablesConfig",
            description = "Will drink super strength potion at set amount."
    )
    default int SuperStrengthDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "SuperDefenceDrinkAmount",
            name = "Drink S. Defence at",
            position = 20,
            section = "consumablesConfig",
            description = "Will drink super defence potion at set amount."
    )
    default int SuperDefenceDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 98
    )
    @ConfigItem(
            keyName = "RangedDrinkAmount",
            name = "Drink Ranged potion at",
            position = 21,
            section = "consumablesConfig",
            description = "Will drink ranged potion at set amount."
    )
    default int RangedDrinkAmount() {
        return 0;
    }

    @ConfigSection(
            name = "Fight Config",
            description = "Fight configuration section.",
            closedByDefault = true,
            position = 25
    )
    String fightConfig = "fightConfig";

    @ConfigItem(
            keyName = "fightInstructions",
            name = "",
            description = "Fight Instructions.",
            position = 27,
            section = "fightConfig"
    )
    default String fightConfigInstructions() {
        return "If enabled, will switch to gear of your choice to use special attack. \n\nWill switch back to main gear when out of spec.";
    }

    @ConfigItem(
            keyName = "enableSpec",
            name = "Enable Spec Attack",
            description = "Enable to turn on special attack",
            section = "fightConfig",
            position = 29
    )
    default boolean enableSpec()
    {
        return false;
    }

    @Range(
            min = 25,
            max = 100
    )
    @ConfigItem(
            keyName = "useSpecAtAmount",
            name = "Spec at:",
            position = 31,
            section = "fightConfig",
            description = "Amount of ranging potions to withdraw."
    )
    default int specAtAmount()
    {
        return 25;
    }

    @ConfigItem(
            keyName = "Main Gear",
            name = "Main Gear",
            description = "Main gear names",
            position = 32,
            section = "fightConfig"
    )
    default String MainWeapons() {
        return "bone mace, dragon defender";
    }

    @ConfigItem(
            keyName = "Spec Attack Gear Swap",
            name = "Spec Attack Gear Swap",
            description = "Gear to use special attack with during power surge.",
            position = 33,
            section = "fightConfig"
    )
    default String SpecWeapons() {
        return "dragon dagger,dragon defender";
    }


    @ConfigSection(
            name = "Enemy NPC Config",
            description = "Enemy NPC configuration section.",
            closedByDefault = true,
            position = 40
    )
    String enemyNPCConfig = "enemyNPCConfig";

    @ConfigItem(
            keyName = "enemyNPCInstructions",
            name = "",
            description = "Eneny NPC Instructions.",
            position = 41,
            section = "enemyNPCConfig"
    )
    default String enemyNPCConfigInstructions() {
        return "First input your Enemy NPC name. The field is not case sensitive.\n\n" +
                "Use Safespot option is recommended for ranging. Make sure you set up the Safespot XY coords befure starting plugin if you use this.\n\n" +
                "Attack Radius will attack NPCs around the set Safespot XY coords. Use Safespot does not need to be active for this to work.";
    }

    @ConfigItem(
            keyName = "enemyNPC",
            name = "Enemy NPC Name:",
            description = "Input the name of the npc you want to kill. Not case sensitive.",
            position = 42,
            section = "enemyNPCConfig"
    )
    default String EnemyNPCName() {
        return "goblin";
    }

    @ConfigItem(
            keyName = "ignoreEnemyLevels",
            name = "Ignore Enemies With Levels",
            description = "",
            position = 43,
            section = "enemyNPCConfig"
    )
    default String ignoreLevels() {
        return "";
    }

    @ConfigItem(
            keyName = "UseSafespot",
            name = "Use Safespot?",
            description = "Should safespot be used?",
            position = 44,
            section = "enemyNPCConfig"
    )
    default boolean UseSafespot() {
        return false;
    }

    @ConfigItem(
            keyName = "UseSafespotNPCRadius",
            name = "Use Attack Radius?",
            description = "You would only attack NPCs within X radius of your set safespot.",
            position = 45,
            section = "enemyNPCConfig"
    )
    default boolean UseSafespotNPCRadius() {
        return false;
    }


    @ConfigItem(
            keyName = "SafespotMeleeMode",
            name = "Roam Safespot Area",
            description = "Allows roaming within configured area",
            position = 46,
            section = "enemyNPCConfig"
    )
    default boolean safespotRoam() {
        return false;
    }

    @Range(
            min = 1,
            max = 25
    )
    @ConfigItem(
            keyName = "SafespotNPCRadius",
            name = "Safespot NPC Radius:",
            position = 47,
            section = "enemyNPCConfig",
            description = "Radius from safe spot to attack NPC from."
    )
    default int SafespotNPCRadius() {
        return 12;
    }

    @ConfigItem(
            keyName = "SafespotCoords",
            name = "Safespot X,Y Coords",
            description = "Set your safespot coords X,Y. Example: 3024,1028",
            position = 48,
            section = "enemyNPCConfig"
    )
    default String SafespotCoords() {
        return "1234,1234";
    }

    @ConfigSection(
            name = "Loot Config",
            description = "Loot configuration section.",
            closedByDefault = true,
            position = 70
    )
    String lootConfig = "lootNPCConfig";

    @ConfigItem(
            keyName = "Loot Items",
            name = "Loot Items?",
            description = "If enabled, will loot items until inventory is full",
            position = 74,
            section = "lootNPCConfig"
    )
    default boolean LootItems() {
        return false;
    }

    @ConfigItem(
            keyName = "EatMakeSpace",
            name = "Eat to make space?",
            description = "If enabled, will eat food to make space for loot",
            position = 75,
            section = "lootNPCConfig"
    )
    default boolean EatMakeSpace() {
        return false;
    }

    @ConfigItem(
            keyName = "stopWhenInventoryFull",
            name = "Stop when inventory full",
            description = "",
            position = 76,
            section = lootConfig
    )
    default boolean stopIfFull() {
        return false;
    }

    @ConfigItem(
            keyName = "AlchItems",
            name = "Alch Items",
            description = "If enabled, will alch items that you input into the Alch Item list.",
            position = 77,
            section = "lootNPCConfig"
    )
    default boolean AlchItems() {
        return false;
    }

    @ConfigItem(
            keyName = "BuryBones",
            name = "Bury Bones/Scatter Ashes?",
            description = "If enabled, will bury bones or scatter ashes.",
            position = 78,
            section = "lootNPCConfig"
    )
    default boolean BuryBonesOrAshes() {
        return false;
    }

    @ConfigItem(
            keyName = "ItemsToLoot",
            name = "Items to loot",
            description = "Write down all complete item names you want to loot separated by a comma.",
            position = 79,
            section = "lootNPCConfig"
    )
    default String ItemsToLoot() {
        return "bones,earth rune";
    }

    @ConfigItem(
            keyName = "AlchWhitelistItemName",
            name = "Don't Alch Items:",
            description = "Input the complete name of the items you want to alch, separated by a comma.",
            position = 84,
            section = "lootNPCConfig"
    )
    default String alchWhitelist() {
        return "rune pouch,nature rune, fire rune,";
    }

    @ConfigItem(
            keyName = "AlchItemName",
            name = "Alch Item Name:",
            description = "Input the complete name of the item you want to alch.",
            position = 85,
            section = "lootNPCConfig"
    )
    default String alchItemName() {
        return "rune platebody,";
    }


    @ConfigSection(
            name = "Cannon Config",
            description = "Cannon configuration section.",
            closedByDefault = true,
            position = 95
    )
    String cannonConfig = "cannonConfig";

    @ConfigItem(
            keyName = "cannonInstructions",
            name = "",
            description = "Cannon Instructions.",
            position = 96,
            section = "cannonConfig"
    )
    default String cannonConfigInstructions() {
        return "Set up coordinates where you want cannon to be placed.\n\n" +
                "Enemy Radius setting is recommended so you don't go too far away from your cannon.\n\nMake sure you set up the Safespot XY coords befure starting plugin if you use this.";
    }

    @ConfigItem(
            keyName = "UseCannon",
            name = "Use Cannon?",
            description = "Should cannon be used?",
            position = 98,
            section = "cannonConfig"
    )
    default boolean UseCannon() {
        return false;
    }

    @ConfigItem(
            keyName = "CannonCoords",
            name = "Cannon X,Y Coords",
            description = "Set your cannon coords X,Y. Example: 3024,1028",
            position = 100,
            section = "cannonConfig"
    )
    default String CannonCoords() {
        return "1234,1234";
    }

    @Range(
            min = 1,
            max = 30
    )
    @ConfigItem(
            keyName = "CannonLowAmount",
            name = "Reload cannon at:",
            position = 104,
            section = "cannonConfig",
            description = "Will reload cannon at set amount."
    )
    default int CannonLowAmount() {
        return 5;
    }


    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            closedByDefault = true,
            position = 150
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
            position = 151,
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
            position = 152,
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
            position = 153,
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
            position = 154,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 155,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }


    @ConfigSection(
            name = "Debug Tile Configuration",
            description = "Debugging stuff for tiles section.",
            closedByDefault = true,
            position = 166
    )
    String tileDebugConfig = "tileDebugConfig";

    @ConfigItem(
            keyName = "enemyRadius",
            name = "enemyRadius tile colour",
            position = 167,
            description = "",
            section = "tileDebugConfig"
    )
    default Color enemyRadius() {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            keyName = "enemyRadiusAreaFill",
            name = "enemyRadiusAreaFill fill colour",
            position = 168,
            description = "",
            section = "tileDebugConfig"
    )
    default Color enemyRadiusAreaFill() {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(
            keyName = "safespotTile",
            name = "safespot tile colour",
            position = 169,
            description = "",
            section = "tileDebugConfig"
    )
    default Color safespotTile() {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "safespotTileFill",
            name = "safespotTile fill colour",
            position = 170,
            description = "",
            section = "tileDebugConfig"
    )
    default Color safespotTileFill() {
        return new Color(0, 0, 0, 30);
    }

    @ConfigItem(
            keyName = "enemyAreaFillColour",
            name = "enemyAreaFill tile colour",
            position = 171,
            description = "",
            section = "tileDebugConfig"
    )
    default Color enemyArea() {
        return Color.MAGENTA;
    }

    @Alpha
    @ConfigItem(
            keyName = "enemyAreaFill",
            name = "enemyAreaFill fill colour",
            position = 172,
            description = "",
            section = "tileDebugConfig"
    )
    default Color enemyAreaFill() {
        return new Color(0, 0, 0, 30);
    }

    @ConfigItem(
            keyName = "cannonspotFillColour",
            name = "cannonSpotTile tile colour",
            position = 173,
            description = "",
            section = "tileDebugConfig"
    )
    default Color cannonSpotTile() {
        return Color.YELLOW;
    }

    @Alpha
    @ConfigItem(
            keyName = "cannonSpotTileFill",
            name = "cannonSpotTileFill fill colour",
            position = 174,
            description = "",
            section = "tileDebugConfig"
    )
    default Color cannonSpotTileFill() {
        return new Color(0, 0, 0, 50);
    }

    @ConfigSection(
            name = "UI Settings",
            description = "UI settings.",
            closedByDefault = true,
            position = 180
    )
    String UIConfig = "UIConfig";
    @ConfigItem(
            keyName = "enableDebugTiles",
            name = "Enable Debug Tiles",
            description = "Enable to turn on in game Debug Tiles",
            section = "UIConfig",
            position = 182
    )
    default boolean enableDebugTiles() {
        return true;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            section = "UIConfig",
            position = 182
    )
    default boolean enableUI() {
        return true;
    }

}
