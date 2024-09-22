package com.spinplugins.IronBuddy.data;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public interface Const {
    Integer plankPrice = 5;
    Integer POHPortalID = ObjectID.PORTAL_4525;
    Integer rimmingtonPortalID = ObjectID.PORTAL_15478;

    Integer portPhasDoorIDOpen = ObjectID.DOOR_5244;
    Integer portPhasDoorIDClose = ObjectID.DOOR_5245;

    WorldPoint portPhasRangeDoor = new WorldPoint(3472, 3674, 0);
    WorldArea portPhasRangeArea = new WorldArea(
            new WorldPoint(3681, 3461, 0),
            10,
            10
    );

    enum BuddyTasks {
        PATHING_TESTING,
        CONSTRUCTION_POH,
        CRAFTING_GLASS,
        CRAFTING_GLASS_ITEM,
    }

    enum POHLocation {
        RIMMINGTON,
        TAVERLY,
        YANILLE,
    }

    @Getter
    enum Seaweed {
        SEAWEED(ItemID.SEAWEED),
        GIANT_SEAWEED(ItemID.GIANT_SEAWEED);

        private final int id;

        Seaweed(int id) {
            this.id = id;
        }
    }
}