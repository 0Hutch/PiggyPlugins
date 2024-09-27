package com.piggyplugins.BobTheFarmer;

import net.runelite.client.config.*;

@ConfigGroup("BobTheFarmer")
public interface BobTheFarmerConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 3

    )
    String tickDelaySection = "Tick Delays";

    @ConfigSection(
            name = "Herb patches",
            description = "Configuration for herb patches",
            position = 2

    )
    String herbPatches = "Herb patches";

    @ConfigSection(
            name = "Tree patches",
            description = "Configuration for tree patches",
            position = 2

    )
    String treePatches = "Tree patches";

    @ConfigSection(
            name = "General",
            description = "General configuration",
            position = 1

    )
    String generalConfiguration = "General configuration";

    //------------------------------------- Keybinds -------------------------------------
    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "",
            position = -3
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }
    @ConfigItem(
            keyName = "doHerbRun",
            name = "Do Herb run",
            description = "",
            position = -2
    )
    default Keybind doHerbRun() {
        return Keybind.NOT_SET;
    }
    @ConfigItem(
            keyName = "doTreeRun",
            name = "Do Tree run",
            description = "",
            position = -2
    )
    default Keybind doTreeRun() {
        return Keybind.NOT_SET;
    }
    @ConfigItem(
            keyName = "debugKey",
            name = "Bob the debugger",
            description = "",
            position = -2
    )
    default Keybind debugKey() {
        return Keybind.NOT_SET;
    }

    //------------------------------------- General configuration -------------------------------------
    @ConfigItem(
            keyName = "additionalItems",
            name = "Additional items",
            description = "",
            position = 1,
            section = generalConfiguration
    )
    default String additionalItems() {
        return "Rune pouch";
    }
    @ConfigItem(
            keyName = "bankTeleport",
            name = "Bank teleport",
            description = "",
            position = 1,
            section = generalConfiguration
    )
    default BankTeleport bankTeleport() {
        return BankTeleport.NONE;
    }

    @ConfigItem(
            keyName = "debugDisableRestock",
            name = "debugDisableRestock",
            description = "",
            position = -1
    )
    default boolean debugDisableRestock() {
        return false;
    }
    @ConfigItem(
            keyName = "debugStateMachine",
            name = "debugStateMachine",
            description = "",
            position = -1
    )
    default boolean debugStateMachine() {
        return false;
    }

    //------------------------------------- Tree run configuration -------------------------------------
    @ConfigItem(
            keyName = "tree",
            name = "Tree",
            description = "What type of tree do you want to plant",
            position = -1,
            section = treePatches

    )
    default Tree tree() { return Tree.Oak; }

    @ConfigItem(
            keyName = "enableTreeFalador",
            name = "Falador Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeFalador() { return false; }
    @ConfigItem(
            keyName = "enableTreeFarmingGuild",
            name = "Farming Guild Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeFarmingGuild() { return false; }
    @ConfigItem(
            keyName = "enableTreeLumbridge",
            name = "Lumbridge Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeLumbridge() { return false; }
    @ConfigItem(
            keyName = "enableTreeTaverley",
            name = "Taverley Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeTaverley() { return false; }
    @ConfigItem(
            keyName = "enableTreeGnomeStronghold",
            name = "Gnome Stronghold Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeGnomeStronghold() { return false; }
    @ConfigItem(
            keyName = "enableTreeVarrock",
            name = "Varrock Tree",
            description = "",
            position = 1,
            section = treePatches
    )
    default boolean enableTreeVarrock() { return false; }

    //------------------------------------- Herb run configuration -------------------------------------
    @ConfigItem(
            keyName = "herb",
            name = "Herb",
            description = "",
            position = -6,
            section = herbPatches

    )
    default Herb herb() {
        return Herb.Guam_leaf;
    }
    @ConfigItem(
            keyName = "compost",
            name = "Compost",
            description = "",
            position = -5,
            section = herbPatches
    )
    default Compost_Type compost() { return Compost_Type.Ultracompost; }
    @ConfigItem(
            keyName = "allotment",
            name = "Allotment",
            description = "",
            position = -4,
            section = herbPatches

    )
    default Allotment allotment() {
        return Allotment.NONE;
    }
    @ConfigItem(
            keyName = "whiteLilly",
            name = "White Lilly planted",
            description = "",
            position = -4,
            section = herbPatches

    )
    default boolean whiteLilly() { return false; }
    @ConfigItem(
            keyName = "cleanHerbs",
            name = "Clean herbs",
            description = "",
            position = -3,
            section = herbPatches
    )
    default boolean cleanHerbs() {
        return false;
    }
    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "cleanHerbsTick",
            name = "Clean herbs per tick",
            description = "",
            position = -3,
            section = herbPatches
    )
    default int cleanHerbsTick() {
        return 2;
    }
    @ConfigItem(
            keyName = "bottomlessBucket",
            name = "Use Bottomless Bucket",
            description = "",
            position = -2,
            section = herbPatches
    )
    default boolean bottomlessBucket() { return false; }
    @ConfigItem(
            name = "Ardougne herb patch",
            keyName = "enableArdougne",
            description = "Requires Ardougne cloak 2 or higher",
            position = 0,
            section = herbPatches
    )
    default boolean enableArdougne() {
        return false;
    }
    @ConfigItem(
            name = "Catherby herb patch",
            keyName = "enableCatherby",
            description = "",
            position = 1,
            section = herbPatches
    )
    default boolean enableCatherby() {
        return false;
    }
    @ConfigItem(
            name = "Civitas illa Fortis herb patch(WIP)",
            keyName = "enableCivitasIllaFortis",
            description = "",
            position = 2,
            section = herbPatches
    )
    default boolean enableCivitasIllaFortis() {
        return false;
    }
    @ConfigItem(
            name = "Falador herb patch",
            keyName = "enableFalador",
            description = "Requires Explorer's ring 2 or higher",
            position = 3,
            section = herbPatches
    )
    default boolean enableFalador() {
        return false;
    }
    @ConfigItem(
            name = "Farming guild herb patch",
            keyName = "enableFarmingGuild",
            description = "",
            position = 4,
            section = herbPatches
    )
    default boolean enableFarmingGuild() {
        return false;
    }
    @ConfigItem(
            name = "Harmony Island herb patch(WIP)",
            keyName = "enableHarmonyIsland",
            description = "",
            position = 5,
            section = herbPatches
    )
    default boolean enableHarmonyIsland() {
        return false;
    }
    @ConfigItem(
            name = "Hosidius herb patch",
            keyName = "enableHosidius",
            description = "",
            position = 5,
            section = herbPatches
    )
    default boolean enableHosidius() {
        return false;
    }
    @ConfigItem(
            name = "Port Phasmatys herb patch",
            keyName = "enablePortPhasmatys",
            description = "",
            position = 6,
            section = herbPatches
    )
    default boolean enablePortPhasmatys() {
        return false;
    }
    @ConfigItem(
            name = "Troll Stronghold herb patch(WIP)",
            keyName = "enableTrollStronghold",
            description = "",
            position = 7,
            section = herbPatches
    )
    default boolean enableTrollStronghold() {
        return false;
    }
    @ConfigItem(
            name = "Weiss herb patch(WIP)",
            keyName = "enableWeiss",
            description = "",
            position = 8,
            section = herbPatches
    )
    default boolean enableWeiss() {
        return false;
    }

    //------------------------------------- Tick delay configuration -------------------------------------
    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 0,
            section = tickDelaySection
    )
    default int tickDelayMin() {
        return 0;
    }
    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 1,
            section = tickDelaySection
    )
    default int tickDelayMax() {
        return 3;
    }



}
