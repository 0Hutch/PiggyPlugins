/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.wigglydonplugins.autoVorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("AutoVorkath")
public interface AutoVorkathConfig extends Config {

    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 0
    )
    default CROSSBOW CROSSBOW() {
        return CROSSBOW.DRAGON_HUNTER_CROSSBOW;
    }

    @ConfigItem(
            keyName = "slayersStaff",
            name = "Slayers Staff",
            description = "Choose your slayers staff",
            position = 1
    )
    default STAFF SLAYERSTAFF() {
        return STAFF.SLAYER_STAFF;
    }

    @ConfigItem(
            keyName = "teleport",
            name = "Teleport",
            description = "Choose your teleport",
            position = 2
    )
    default TELEPORT TELEPORT() {
        return TELEPORT.CONSTRUCT_CAPE_T;
    }

    @ConfigItem(
            keyName = "portal",
            name = "Portal",
            description = "What Portal to use to teleport to Lunar Isle.",
            position = 3
    )
    default PORTAL PORTAL() {
        return PORTAL.PORTAL_NEXUS;
    }

    @ConfigItem(
            keyName = "rigour",
            name = "Rigour",
            description = "Activate Rigour?",
            position = 4
    )
    default boolean ACTIVATERIGOUR() {
        return true;
    }

    @ConfigItem(
            keyName = "rangePotion",
            name = "Ranging Potion",
            description = "What Ranging potion to use?",
            position = 5
    )
    default RANGE_POTION RANGEPOTION() {
        return RANGE_POTION.DIVINE_RANGING_POTION;
    }

    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 6
    )
    default PRAYER_POTION PRAYERPOTION() {
        return PRAYER_POTION.PRAYER;
    }

    @ConfigItem(
            keyName = "antiFirePotion",
            name = "Antifire Potion",
            description = "What Antifire potion to use?",
            position = 7
    )
    default ANTIFIRE ANTIFIRE() {
        return ANTIFIRE.EXTENDED_SUPER_ANTIFIRE;
    }

    @ConfigItem(
            keyName = "antiVenomPotion",
            name = "Antivenom Potion",
            description = "What Antivenom potion to use?",
            position = 8
    )
    default ANTIVENOM ANTIVENOM() { return ANTIVENOM.ANTI_VENOM_PLUS; }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "MAX FOOD : MIN FOOD",
            position = 9
    )
    default Dimension FOODAMOUNT() {
        return new Dimension(15, 10);
    }

    @ConfigItem(
            keyName = "poolDrinkat",
            name = "Ornate Pool Drink",
            description = "HEALTH : PRAYER",
            position = 10
    )
    default Dimension POOLDRINK() {
        return new Dimension(90, 90);
    }

    @ConfigItem(
            keyName = "eatat",
            name = "Eat at",
            description = "Eat at what health?",
            position = 11
    )
    default int EATAT() {
        return 50;
    }

    @ConfigItem(
            keyName = "drinkprayerat",
            name = "Drink prayer potion at",
            description = "Drink prayer potion when?",
            position = 12
    )
    default int DRINKPRAYERAT() { return 15; }

    @ConfigItem(
            keyName = "food",
            name = "Food",
            description = "What food to use?",
            position = 13
    )
    default FOOD_TYPE FOOD_TYPE() {
        return FOOD_TYPE.MANTA_RAY;
    }

    @ConfigItem(
            keyName = "minimum loot value",
            name = "Minimum loot value",
            description = "Minimum loot value",
            position = 14
    )
    default int MIN_PRICE() { return 0; }

    @ConfigItem(
            keyName = "prayer pots to bring",
            name = "Prayer pots to bring",
            description = "# of ppots to bring",
            position = 15
    )
    default int PRAYER_POTION_AMOUNT() { return 3; }

}
