package com.ozplugins.AutoScurrius;

import net.runelite.client.config.*;

@ConfigGroup("AutoScurriusConfig")
public interface AutoScurriusConfig extends Config {

    String version = "v0.2";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions()
    {
        return "Start at a Varrock bank. \n\nSet hotkey up and activate plugin with the hotkey.";
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
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    /*@ConfigSection(
            name = "Tick Delay",
            description = "",
            position = 1

    )
    String tickDelaySection = "Tick Delay";

    @ConfigItem(
            name = "Tick Delay",
            keyName = "tickDelay",
            description = "",
            position = -998,
            section = tickDelaySection
    )
    default int tickDelay() {
        return 0;
    }*/
    @ConfigItem(
            name = "Varrock Travel",
            keyName = "varrockTravel",
            description = "",
            position = 11,
            section = "setupConfig"
    )
    default VarrockTele varrockTravel() {
        return VarrockTele.TELETAB;
    }

    @ConfigItem(
            keyName = "oneTickFlick",
            name = "1Tick Flick",
            description = "",
            position = 19,
            section = "setupConfig"
    )
    default boolean oneTickFlick() {
        return false;
    }

    //CONSUMABLES
    @ConfigSection(
            name = "Banking Config",
            description = "Consumables configuration section.",
            closedByDefault = true,
            position = 25
    )
    String bankingConfig = "bankingConfig";

    @ConfigItem(
            keyName = "bankingInstructions",
            name = "",
            description = "Banking Instructions.",
            position = 27,
            section = "bankingConfig"
    )
    default String bankingConfigInstructions() {
        return "Set up how much of each item you would like to withdraw when banking. Any option that is set to 0 is deactivated.";
    }

    @Range(
            min = 1,
            max = 20
    )
    @ConfigItem(
            keyName = "FoodWithdrawAmount",
            name = "# Food",
            position = 31,
            section = "bankingConfig",
            description = "Amount of ranging potions to withdraw."
    )
    default int foodAmount()
    {
        return 0;
    }


    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "PrayerWithdrawAmount",
            name = "# Prayer Potions",
            position = 35,
            section = "bankingConfig",
            description = "Amount of prayer potions to withdraw."
    )
    default int prayerAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "superAttackWithdrawAmount",
            name = "# S. Attack Potions",
            position = 36,
            section = "bankingConfig",
            description = "Amount of S. Attack potions to withdraw."
    )
    default int superAttackAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "attackWithdrawAmount",
            name = "# Attack Potions",
            position = 37,
            section = "bankingConfig",
            description = "Amount of Attack potions to withdraw."
    )
    default int attackAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "superStrengthWithdrawAmount",
            name = "# S. Strength Potions",
            position = 38,
            section = "bankingConfig",
            description = "Amount of S. Strength potions to withdraw."
    )
    default int superStrengthAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "strengthWithdrawAmount",
            name = "# Strength Potions",
            position = 39,
            section = "bankingConfig",
            description = "Amount of Strength potions to withdraw."
    )
    default int strengthAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "superDefenceWithdrawAmount",
            name = "# S. Defence Potions",
            position = 40,
            section = "bankingConfig",
            description = "Amount of S. Defence potions to withdraw."
    )
    default int superDefenceAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "defenceWithdrawAmount",
            name = "# Defence Potions",
            position = 41,
            section = "bankingConfig",
            description = "Amount of defence potions to withdraw."
    )
    default int defenceAmount()
    {
        return 0;
    }

    @Range(
            min = 0,
            max = 6
    )
    @ConfigItem(
            keyName = "RangingWithdrawAmount",
            name = "# Ranging Potions",
            position = 42,
            section = "bankingConfig",
            description = "Amount of ranging potions to withdraw."
    )
    default int rangingAmount()
    {
        return 0;
    }



    @ConfigSection(
            name = "Fight Config",
            description = "Fight configuration section.",
            closedByDefault = true,
            position = 46
    )
    String fightConfig = "fightConfig";

    @ConfigItem(
            keyName = "fightInstructions",
            name = "",
            description = "Fight Instructions.",
            position = 48,
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
            position = 50
    )
    default boolean enableSpec()
    {
        return true;
    }

    @Range(
            min = 25,
            max = 100
    )
    @ConfigItem(
            keyName = "useSpecAtAmount",
            name = "Spec at:",
            position = 51,
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
            position = 52,
            section = "fightConfig"
    )
    default String MainWeapons() {
        return "bone mace, dragon defender";
    }

    @ConfigItem(
            keyName = "Spec Attack Gear Swap",
            name = "Spec Attack Gear Swap",
            description = "Gear to use special attack with during power surge.",
            position = 54,
            section = "fightConfig"
    )
    default String SpecWeapons() {
        return "dragon dagger,dragon defender";
    }


    //CONSUMABLES
    @ConfigSection(
            name = "Consumables Config",
            description = "Consumables configuration section.",
            closedByDefault = true,
            position = 56
    )
    String consumablesConfig = "consumablesConfig";

    @ConfigItem(
            keyName = "consumableInstructions",
            name = "",
            description = "Consumable Instructions.",
            position = 57,
            section = "consumablesConfig"
    )
    default String consumablesConfigInstructions() {
        return "Any option that is set to 0 is deactivated. \n\n Works with normal potions and Super potions.";
    }


    @ConfigItem(
            keyName = "FoodItemName",
            name = "Food to eat",
            description = "Input the name of the food you want to eat.",
            position = 59,
            section = "consumablesConfig"
    )
    default String foodName() {
        return "Shark";
    }

    @Range(
            min = 10,
            max = 98
    )
    @ConfigItem(
            keyName = "HealthLowAmount",
            name = "Eat at health:",
            position = 61,
            section = "consumablesConfig",
            description = "Will eat set health."
    )
    default int healthLowAmount()
    {
        return 55;
    }

    @Range(
            min = 0,
            max = 110
    )
    @ConfigItem(
            keyName = "PrayerLowAmount",
            name = "Drink Prayer potion at",
            position = 62,
            section = "consumablesConfig",
            description = "Will drink prayer potion at set amount."
    )
    default int PrayerLowAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 110
    )
    @ConfigItem(
            keyName = "AttackDrinkAmount",
            name = "Drink Attack at",
            position = 64,
            section = "consumablesConfig",
            description = "Will drink attack potion at set amount."
    )
    default int AttackDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 110
    )
    @ConfigItem(
            keyName = "StrengthDrinkAmount",
            name = "Drink Strength at",
            position = 66,
            section = "consumablesConfig",
            description = "Will drink strength potion at set amount."
    )
    default int StrengthDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 110
    )
    @ConfigItem(
            keyName = "DefenceDrinkAmount",
            name = "Drink Defence at",
            position = 68,
            section = "consumablesConfig",
            description = "Will drink defence potion at set amount."
    )
    default int DefenceDrinkAmount() {
        return 0;
    }

    @Range(
            min = 0,
            max = 110
    )
    @ConfigItem(
            keyName = "RangedDrinkAmount",
            name = "Drink Ranged potion at",
            position = 69,
            section = "consumablesConfig",
            description = "Will drink ranged potion at set amount."
    )
    default int RangedDrinkAmount() {
        return 0;
    }

    @ConfigSection(
            name = "Loot Config",
            description = "Loot configuration section.",
            closedByDefault = true,
            position = 90
    )
    String lootConfig = "lootNPCConfig";

    @ConfigItem(
            keyName = "Loot Items",
            name = "Loot Items?",
            description = "If enabled, will loot items until inventory is full",
            position = 94,
            section = "lootNPCConfig"
    )
    default boolean LootItems() {
        return false;
    }

    @ConfigItem(
            keyName = "EatMakeSpace",
            name = "Eat to make space?",
            description = "If enabled, will eat food to make space for loot",
            position = 95,
            section = "lootNPCConfig"
    )
    default boolean EatMakeSpace() {
        return false;
    }

    @ConfigItem(
            keyName = "AlchItems",
            name = "Alch Items",
            description = "If enabled, will alch items that you input into the Alch Item list.",
            position = 106,
            section = "lootNPCConfig"
    )
    default boolean AlchItems() {
        return false;
    }

    @ConfigItem(
            keyName = "lootList",
            name = "Loot Items",
            description = "Items to loot",
            position = 105,
            section = "lootNPCConfig"
    )
    default String lootList() {
        return "Scurrius' spine,Rune med helm,Earth battlestaff,Mystic eath staff,Rune warhammer,Rune chainbody,Runite ore," +
                "Dragonfruit tree seed,Celastrus seed,Grimy kwuarm,Grimy cadantine,Grimy dwarf weed,Grimy lantadyme," +
                " Rune arrow, Rune battleaxe,Adamant platebody,Shark,Death rune,Rune sq shield,Rune full helm,Prayer potion(4), Chaos rune,Coins,Law rune,";
    }

    @ConfigItem(
            keyName = "AlchItemName",
            name = "Alch Item Name:",
            description = "Input the complete name of the item you want to alch.",
            position = 108,
            section = "lootNPCConfig"
    )
    default String alchItemName() {
        return "Rune med helm,Earth battlestaff,Mystic eath staff,Rune warhammer,Rune chainbody,Runite ore,Rune sq shield," +
                "Rune full helm,Rune battleaxe,Adamant platebody";
    }

    @ConfigSection(
            name = "UI Settings",
            description = "UI settings.",
            position = 180,
            closedByDefault = true
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
            position = 140
    )
    default boolean enableUI()
    {
        return true;
    }

}

